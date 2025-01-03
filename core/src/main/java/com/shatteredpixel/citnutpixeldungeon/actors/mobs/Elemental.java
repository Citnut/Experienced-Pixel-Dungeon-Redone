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

package com.shatteredpixel.citnutpixeldungeon.actors.mobs;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.Statistics;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.citnutpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.citnutpixeldungeon.effects.Lightning;
import com.shatteredpixel.citnutpixeldungeon.effects.Splash;
import com.shatteredpixel.citnutpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.citnutpixeldungeon.items.quest.Embers;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.citnutpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.citnutpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.citnutpixeldungeon.sprites.ElementalSprite;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Elemental extends Mob {

	{
		HP = HT = 60;
		defenseSkill = 20;
		
		EXP = 10;
		maxLvl = 20;
		
		flying = true;
        switch (Dungeon.cycle){
            case 1:
                HP = HT = 715;
                defenseSkill = 69;
                EXP = 65;
                break;
            case 2:
                HP = HT = 9431;
                defenseSkill = 278;
                EXP = 571;
                break;
            case 3:
                HP = HT = 231900;
                defenseSkill = 624;
                EXP = 5890;
                break;
            case 4:
                HP = HT = 70000000;
                defenseSkill = 5600;
                EXP = 500000;
                break;
			case 5:
				HP = HT = 2400000000L;
				defenseSkill = 86500;
				EXP = 110000000;
				break;
        }
	}

	protected boolean summonedALly;
	@Override
	public long damageRoll() {
		if (!summonedALly) {
			switch (Dungeon.cycle) {
				case 1: return Dungeon.NormalLongRange(64, 83);
				case 2: return Dungeon.NormalLongRange(291, 434);
				case 3: return Dungeon.NormalLongRange(1650, 2100);
				case 4: return Dungeon.NormalLongRange(30000, 85000);
				case 5: return Dungeon.NormalLongRange(3000000, 7000000);
			}
			return Dungeon.NormalLongRange( 16, 26 );
		} else {
			int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
			return Dungeon.NormalLongRange(6*regionScale, 15 + 10*regionScale);
		}
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (!summonedALly) {
			switch (Dungeon.cycle){
				case 1: return 102;
				case 2: return 355;
				case 3: return 930;
				case 4: return 6000;
				case 5: return 100000;
			}
			return 25;
		} else {
			int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
			return 5 + 10*regionScale;
		}
	}

	public void setSummonedALly(){
		summonedALly = true;
		//sewers are prison are equivalent, otherwise scales as normal (2/2/3/4/5)
		int regionScale = Math.max(2, (1 + Dungeon.scalingDepth()/5));
		defenseSkill = 10*regionScale;
		HT = 30*regionScale;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Dungeon.NormalLongRange(24, 50);
            case 2: return Dungeon.NormalLongRange(121, 243);
            case 3: return Dungeon.NormalLongRange(700, 1321);
            case 4: return Dungeon.NormalLongRange(22000, 64000);
			case 5: return Dungeon.NormalLongRange(2200000, 4250000);
        }
		return Dungeon.NormalLongRange(0, 5);
	}
	
	protected int rangedCooldown = Random.NormalIntRange( 3, 5 );
	
	@Override
	protected boolean act() {
		if (state == HUNTING){
			rangedCooldown--;
		}
		
		return super.act();
	}

	@Override
	public void die(Object cause) {
		flying = false;
		super.die(cause);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (super.canAttack(enemy)){
			return true;
		} else {
			return rangedCooldown < 0 && new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
		}
	}
	
	protected boolean doAttack( Char enemy ) {
		
		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| rangedCooldown > 0
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos != enemy.pos) {
			
			return super.doAttack( enemy );
			
		} else {
			
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}
	
	@Override
	public long attackProc( Char enemy, long damage ) {
		damage = super.attackProc( enemy, damage );
		meleeProc( enemy, damage );
		
		return damage;
	}
	
	protected void zap() {
		spend( 1f );

		Invisibility.dispel(this);
		Char enemy = this.enemy;
		if (hit( this, enemy, true )) {
			
			rangedProc( enemy );
			
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}

		rangedCooldown = Random.NormalIntRange( 3, 5 );
	}
	
	public void onZapComplete() {
		zap();
		next();
	}
	
	@Override
	public boolean add( Buff buff ) {
		if (harmfulBuffs.contains( buff.getClass() )) {
			damage( Dungeon.NormalLongRange( HT/2, HT * 3/5 ), buff );
			return false;
		} else {
			return super.add( buff );
		}
	}
	
	protected abstract void meleeProc( Char enemy, long damage );
	protected abstract void rangedProc( Char enemy );
	
	protected ArrayList<Class<? extends Buff>> harmfulBuffs = new ArrayList<>();
	
	private static final String COOLDOWN = "cooldown";
	private static final String SUMMONED_ALLY = "summoned_ally";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, rangedCooldown );
		bundle.put( SUMMONED_ALLY, summonedALly);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (bundle.contains( COOLDOWN )){
			rangedCooldown = bundle.getInt( COOLDOWN );
		}
		summonedALly = bundle.getBoolean( SUMMONED_ALLY );
		if (summonedALly){
			setSummonedALly();
		}
	}
	
	public static class FireElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Fire.class;
			
			loot = new PotionOfLiquidFlame();
			lootChance = 1/8f;
			
			properties.add( Property.FIERY );
			
			harmfulBuffs.add( com.shatteredpixel.citnutpixeldungeon.actors.buffs.Frost.class );
			harmfulBuffs.add( Chill.class );
		}

        @Override
        public long damageRoll() {
            return Math.round(super.damageRoll()*Dungeon.fireDamage);
        }

        @Override
		protected void meleeProc( Char enemy, long damage ) {
			if (Random.Int( 2 ) == 0 && !Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			if (!Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy, 4f );
			}
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}

	//used in wandmaker quest, a fire elemental with lower ACC/EVA/DMG, no on-hit fire
	// and a unique 'fireball' style ranged attack, which can be dodged
	public static class NewbornFireElemental extends FireElemental {

		{
			spriteClass = ElementalSprite.NewbornFire.class;

			defenseSkill = 12;

			properties.add(Property.MINIBOSS);
		}

		private int targetingPos = -1;

		@Override
		protected boolean act() {
			//fire a charged attack instead of any other action, as long as it is possible to do so
			if (targetingPos != -1 && state == HUNTING){
				//account for bolt hitting walls, in case position suddenly changed
				targetingPos = new Ballistica( pos, targetingPos, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET ).collisionPos;
				if (sprite != null && (sprite.visible || Dungeon.level.heroFOV[targetingPos])) {
					sprite.zap( targetingPos );
					return false;
				} else {
					zap();
					return true;
				}
			} else {

				if (state != HUNTING){
					targetingPos = -1;
				}

				return super.act();
			}
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			if (super.canAttack(enemy)){
				return true;
			} else {
				return rangedCooldown < 0 && new Ballistica( pos, enemy.pos, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET ).collisionPos == enemy.pos;
			}
		}

		protected boolean doAttack( Char enemy ) {

			if (rangedCooldown > 0) {

				return super.doAttack( enemy );

			} else if (new Ballistica( pos, enemy.pos, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET ).collisionPos == enemy.pos) {

				//set up an attack for next turn
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int i : PathFinder.NEIGHBOURS8){
					int target = enemy.pos + i;
					if (target != pos && new Ballistica(pos, target, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET).collisionPos == target){
						candidates.add(target);
					}
				}

				if (!candidates.isEmpty()){
					targetingPos = Random.element(candidates);

					for (int i : PathFinder.NEIGHBOURS9){
						if (!Dungeon.level.solid[targetingPos + i]) {
							sprite.parent.addToBack(new TargetedCell(targetingPos + i, 0xFF0000));
						}
					}

					GLog.n(Messages.get(this, "charging"));
					spend(GameMath.gate(attackDelay(), (int)Math.ceil(Dungeon.hero.cooldown()), 3*attackDelay()));
					Dungeon.hero.interrupt();
					return true;
				} else {
					rangedCooldown = 1;
					return super.doAttack(enemy);
				}


			} else {

				if (sprite != null && (sprite.visible || Dungeon.level.heroFOV[targetingPos])) {
					sprite.zap( targetingPos );
					return false;
				} else {
					zap();
					return true;
				}

			}
		}

		@Override
		protected void zap() {
			if (targetingPos != -1) {
				spend(1f);

				Invisibility.dispel(this);

				for (int i : PathFinder.NEIGHBOURS9) {
					if (!Dungeon.level.solid[targetingPos + i]) {
						CellEmitter.get(targetingPos + i).burst(ElmoParticle.FACTORY, 5);
						if (Dungeon.level.water[targetingPos + i]) {
							GameScene.add(Blob.seed(targetingPos + i, 2, Fire.class));
						} else {
							GameScene.add(Blob.seed(targetingPos + i, 8, Fire.class));
						}

						Char target = Actor.findChar(targetingPos + i);
						if (target != null && target != this) {
							Buff.affect(target, Burning.class).reignite(target);
						}
					}
				}
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
			}

			targetingPos = -1;
			rangedCooldown = Random.NormalIntRange( 3, 5 );
		}

		@Override
		public int attackSkill(Char target) {
			if (!summonedALly) {
				return 15;
			} else {
				return super.attackSkill(target);
			}
		}

		@Override
		public long damageRoll() {
			if (!summonedALly) {
				return Dungeon.NormalLongRange(10, 12);
			} else {
				return super.damageRoll();
			}
		}

		@Override
		protected void meleeProc(Char enemy, long damage) {
			//no fiery on-hit unless it is an ally summon
			if (summonedALly) {
				super.meleeProc(enemy, damage);
			}
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
			if (alignment == Alignment.ENEMY) {
				Dungeon.level.drop( new Embers(), pos ).sprite.drop();
				Statistics.questScores[1] = 2000;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.fadeOut(1f, new Callback() {
							@Override
							public void call() {
								if (Dungeon.level != null) {
									Dungeon.level.playLevelMusic();
								}
							}
						});
					}
				});
			}
		}

		@Override
		public boolean reset() {
			return !summonedALly;
		}

		@Override
		public String description() {
			String desc = super.description();

			if (summonedALly){
				desc += " " + Messages.get(this, "desc_ally");
			} else {
				desc += " " + Messages.get(this, "desc_boss");
			}

			return desc;
		}

		private static final String TARGETING_POS = "targeting_pos";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TARGETING_POS, targetingPos);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			targetingPos = bundle.getInt(TARGETING_POS);
		}
	}

	//not a miniboss, no ranged attack, otherwise a newborn elemental
	public static class AllyNewBornElemental extends NewbornFireElemental {

		{
			rangedCooldown = Integer.MAX_VALUE;

			properties.remove(Property.MINIBOSS);
		}

	}

	public static class FrostElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Frost.class;
			
			loot = new PotionOfFrost();
			lootChance = 1/8f;
			
			properties.add( Property.ICY );
			
			harmfulBuffs.add( Burning.class );
		}
		
		@Override
		protected void meleeProc( Char enemy, long damage ) {
			if (Random.Int( 3 ) == 0 || Dungeon.level.water[enemy.pos]) {
				Freezing.freeze( enemy.pos );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Freezing.freeze( enemy.pos );
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}
	
	public static class ShockElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Shock.class;
			
			loot = new ScrollOfRecharging();
			lootChance = 1/4f;
			
			properties.add( Property.ELECTRIC );
		}
		
		@Override
		protected void meleeProc( Char enemy, long damage ) {
			ArrayList<Char> affected = new ArrayList<>();
			ArrayList<Lightning.Arc> arcs = new ArrayList<>();
			Shocking.arc( this, enemy, 2, affected, arcs );
			
			if (!Dungeon.level.water[enemy.pos]) {
				affected.remove( enemy );
			}
			
			for (Char ch : affected) {
				ch.damage( Math.round( damage * 0.4f ), new Shocking() );
				if (ch == Dungeon.hero && !ch.isAlive()){
					Dungeon.fail(this);
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
				}
			}

			boolean visible = sprite.visible || enemy.sprite.visible;
			for (Char ch : affected){
				if (ch.sprite.visible) visible = true;
			}

			if (visible) {
				sprite.parent.addToFront(new Lightning(arcs, null));
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Buff.affect( enemy, Blindness.class, Blindness.DURATION/2f );
			if (enemy == Dungeon.hero) {
				GameScene.flash(0x80FFFFFF);
			}
		}
	}
	
	public static class ChaosElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Chaos.class;
			
			loot = new ScrollOfTransmutation();
			lootChance = 1f;
		}
		
		@Override
		protected void meleeProc( Char enemy, long damage ) {
			Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
			//TODO shortcutting the fx seems fine for now but may cause problems with new cursed effects
			//of course, not shortcutting it means actor ordering issues =S
			CursedWand.randomValidEffect(null, this, aim, false).effect(null, this, aim, false);
		}

		@Override
		protected void zap() {
			spend( 1f );

			Invisibility.dispel(this);
			Char enemy = this.enemy;
			//skips accuracy check, always hits
			rangedProc( enemy );

			rangedCooldown = Random.NormalIntRange( 3, 5 );
		}

		@Override
		public void onZapComplete() {
			zap();
			//next(); triggers after wand effect
		}

		@Override
		protected void rangedProc( Char enemy ) {
			CursedWand.cursedZap(null, this, new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET), new Callback() {
				@Override
				public void call() {
					next();
				}
			});
		}
	}
	
	public static Class<? extends Elemental> random(){
		float altChance = 1/50f * RatSkull.exoticChanceMultiplier();
		if (Random.Float() < altChance){
			return ChaosElemental.class;
		}
		
		float roll = Random.Float();
		if (roll < 0.4f){
			return FireElemental.class;
		} else if (roll < 0.8f){
			return FrostElemental.class;
		} else {
			return ShockElemental.class;
		}
	}
}
