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
import com.shatteredpixel.citnutpixeldungeon.Challenges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.citnutpixeldungeon.effects.Beam;
import com.shatteredpixel.citnutpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.citnutpixeldungeon.effects.Speck;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.citnutpixeldungeon.levels.Terrain;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfPrismaticLight extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_PRISMATIC_LIGHT;

		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	public long min(long lvl){
		return 1+lvl;
	}

	public long max(long lvl){
		return 5+3*lvl;
	}

	@Override
	public void onZap(Ballistica beam) {
		affectMap(beam);
		
		if (Dungeon.level.viewDistance < 6 ){
			if (Dungeon.isChallenged(Challenges.DARKNESS)){
				Buff.prolong( curUser, Light.class, 2f + buffedLvl());
			} else {
				Buff.prolong( curUser, Light.class, 10f+buffedLvl()*5);
			}
		}
		
		Char ch = Actor.findChar(beam.collisionPos);
		if (ch != null){
			wandProc(ch, chargesPerCast());
			affectTarget(ch);
		}
	}

	private void affectTarget(Char ch){
		long dmg = damageRoll();

		//three in (5+lvl) chance of failing
		if (Dungeon.Long(0, 5+buffedLvl()) >= 3) {
			Buff.prolong(ch, Blindness.class, 2f + (buffedLvl() * 0.333f));
			ch.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6 );
		}

		if (ch.properties().contains(Char.Property.DEMONIC) || ch.properties().contains(Char.Property.UNDEAD)){
			ch.sprite.emitter().start( ShadowParticle.UP, 0.05f,  (int) Math.min(Math.sqrt(10 + buffedLvl()), 1000));
			Sample.INSTANCE.play(Assets.Sounds.BURNING);

			ch.damage(Math.round(dmg*1.333f), this);
		} else {
			ch.sprite.centerEmitter().burst( RainbowParticle.BURST,  (int) Math.min(Math.sqrt(10 + buffedLvl()), 1000));

			ch.damage(dmg, this);
		}

	}

	private void affectMap(Ballistica beam){
		boolean noticed = false;
		for (int c : beam.subPath(0, beam.dist)){
			if (!Dungeon.level.insideMap(c)){
				continue;
			}
			for (int n : PathFinder.NEIGHBOURS9){
				int cell = c+n;

				if (Dungeon.level.discoverable[cell])
					Dungeon.level.mapped[cell] = true;

				int terr = Dungeon.level.map[cell];
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

					Dungeon.level.discover( cell );

					GameScene.discoverTile( cell, terr );
					ScrollOfMagicMapping.discover(cell);

					noticed = true;
				}
			}

			CellEmitter.center(c).burst( RainbowParticle.BURST, Random.IntRange( 1, 2 ) );
		}
		if (noticed)
			Sample.INSTANCE.play( Assets.Sounds.SECRET );

		GameScene.updateFog();
	}

	@Override
	public String upgradeStat2(long level) {
		return Messages.decimalFormat("#", 100*(1-(3/(float)(5+level)))) + "%";
	}

	@Override
	public String upgradeStat3(long level) {
		if (Dungeon.isChallenged(Challenges.DARKNESS)){
			return Long.toString(2 + level);
		} else {
			return Long.toString(10 + 5*level);
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(
				new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, long damage) {
		//cripples enemy
		Buff.prolong( defender, Cripple.class, Math.round((1+staff.buffedLvl())*procChanceMultiplier(attacker)));
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( Dungeon.Int( 0x1000000 ) );
		particle.am = 0.5f;
		particle.setLifespan(1f);
		particle.speed.polar(Random.Float(PointF.PI2), 2f);
		particle.setSize( 1f, 2f);
		particle.radiateXY( 0.5f);
	}

}
