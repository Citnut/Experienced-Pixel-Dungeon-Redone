/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.citnutpixeldungeon.items.wands;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.citnutpixeldungeon.effects.Beam;
import com.shatteredpixel.citnutpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.citnutpixeldungeon.effects.FloatingText;
import com.shatteredpixel.citnutpixeldungeon.effects.Speck;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfTransfusion extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_TRANSFUSION;

		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public long min(long level) {
		return 3 + level;
	}

	@Override
	public long max(long level) {
		return 6 + 2*level;
	}

	private boolean freeCharge = false;

	@Override
    public void onZap(Ballistica beam) {

		for (int c : beam.subPath(0, beam.dist))
			CellEmitter.center(c).burst( BloodParticle.BURST, 1 );

		int cell = beam.collisionPos;

		Char ch = Actor.findChar(cell);

		if (ch instanceof Mob){
			
			wandProc(ch, chargesPerCast());
			
			//this wand does different things depending on the target.
			
			//heals/shields an ally or a charmed enemy while damaging self
			if (ch.alignment == Char.Alignment.ALLY || ch.buff(Charm.class) != null){
				
				// 5% of max hp
				long selfDmg = Math.round(curUser.HT*0.05f);
				
				long healing = (selfDmg + 3*buffedLvl());
				long shielding = (ch.HP + healing) - ch.HT;
				if (shielding > 0){
					healing -= shielding;
					Buff.affect(ch, Barrier.class).setShield(shielding);
				} else {
					shielding = 0;
				}
				
				ch.HP += healing;
				
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING),  (int) Math.min(Math.sqrt(2 + buffedLvl() / 2f), 1000));
				if (healing > 0) {
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Long.toString(healing), FloatingText.HEALING);
				}
				if (shielding > 0){
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Long.toString(shielding), FloatingText.SHIELDING);
				}
				
				if (!freeCharge) {
					damageHero(selfDmg);
				} else {
					freeCharge = false;
				}

			//for enemies...
			//(or for mimics which are hiding, special case)
			} else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {

				//grant a self-shield, and...
				Buff.affect(curUser, Barrier.class).setShield((5 + buffedLvl()));
curUser.sprite.showStatusWithIcon(CharSprite.POSITIVE, Long.toString(5+buffedLvl()), FloatingText.SHIELDING);

				//charms living enemies
				if (!ch.properties().contains(Char.Property.UNDEAD)) {
					Charm charm = Buff.affect(ch, Charm.class, Charm.DURATION/2f);
					charm.object = curUser.id();
					charm.ignoreHeroAllies = true;
					ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f,  (int) Math.sqrt(3 ) );
				
				//harms the undead
				} else {
					ch.damage((long) (Random.NormalLongRange(3 + buffedLvl(), 6+2*buffedLvl())*(1+ Dungeon.hero.lvl/150f)), this);
					ch.sprite.emitter().start(ShadowParticle.UP, 0.05f,  (int) Math.min(Math.sqrt(10 + buffedLvl()), 1000));
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}

			}
			
		}
		
	}

	//this wand costs health too
	private void damageHero(long damage){
		
		curUser.damage(damage, this);

		if (!curUser.isAlive()){
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, long damage) {
		if (defender.buff(Charm.class) != null && defender.buff(Charm.class).object == attacker.id()){
			//grants a free use of the staff and shields self
			freeCharge = true;
			int shieldToGive = Math.round((2*(5 + buffedLvl()))*procChanceMultiplier(attacker));
			Buff.affect(attacker, Barrier.class).setShield(shieldToGive);
			attacker.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
			GLog.p( Messages.get(this, "charged") );
			attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0xCC0000 );
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	@Override
	public String statsDesc() {
		long selfDMG = Dungeon.hero != null ? Math.round(Dungeon.hero.HT*0.05d): 1;
		if (levelKnown)
			return Messages.get(this, "stats_desc", selfDMG, selfDMG + 3*buffedLvl(), 5+buffedLvl(), min(), max()) + "\n\n" + Messages.get(Wand.class, "charges", curCharges, maxCharges);
		else
			return Messages.get(this, "stats_desc", selfDMG, selfDMG, 5, min(0), max(0));
	}

	@Override
	public String upgradeStat1(long level) {
		long selfDMG = Dungeon.hero != null ? Math.round(Dungeon.hero.HT*0.05f): 1;
		return Long.toString(selfDMG + 3*level);
	}

	@Override
	public String upgradeStat2(long level) {
		return Long.toString(5 + level);
	}

	@Override
	public String upgradeStat3(long level) {
		return super.upgradeStat1(level); //damage
	}

	private static final String FREECHARGE = "freecharge";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		freeCharge = bundle.getBoolean( FREECHARGE );
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FREECHARGE, freeCharge );
	}

}