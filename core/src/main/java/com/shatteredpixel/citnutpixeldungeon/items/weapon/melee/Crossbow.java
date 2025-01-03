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

package com.shatteredpixel.citnutpixeldungeon.items.weapon.melee;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;

public class Crossbow extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.CROSSBOW;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		
		//check Dart.class for additional properties
		
		internalTier = tier = 4;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (hero.buff(ChargedShot.class) != null &&
					!(hero.belongings.weapon() instanceof Crossbow)
					&& !(hero.belongings.secondWep() instanceof Crossbow)){
				//clear charged shot if no crossbow is equipped
				hero.buff(ChargedShot.class).detach();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		if (owner.buff(Crossbow.ChargedShot.class) != null){
			Actor.add(new Actor() {
				{ actPriority = VFX_PRIO; }
				@Override
				protected boolean act() {
					if (owner instanceof Hero && !target.isAlive()){
						onAbilityKill((Hero)owner, target);
					}
					Actor.remove(this);
					return true;
				}
			});
			return Float.POSITIVE_INFINITY;
		} else {
			return super.accuracyFactor(owner, target);
		}
	}

	@Override
	public long proc(Char attacker, Char defender, long damage) {
		long dmg = super.proc(attacker, defender, damage);

		//stronger elastic effect
		if (attacker.buff(ChargedShot.class) != null && !(curItem instanceof Dart)){
			//trace a ballistica to our target (which will also extend past them
			Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(defender,
					trajectory,
					3,
					true,
					true,
					this);
			attacker.buff(Crossbow.ChargedShot.class).detach();
		}
		return dmg;
	}

	@Override
	public long max(long lvl) {
		return  5L*(tier()+1) +    //25 base, down from 30
				lvl*(tier()+1);     //+5 per level, down from +6
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (hero.buff(ChargedShot.class) != null){
			GLog.w(Messages.get(this, "ability_cant_use"));
			return;
		}

		beforeAbilityUsed(hero, null);
		Buff.affect(hero, ChargedShot.class);
		hero.sprite.operate(hero.pos);
		hero.next();
		afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 3+buffedLvl(), 3+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 3, 3);
		}
	}

	@Override
	public String upgradeAbilityStat(long level) {
		return Long.toString(3 + level);
	}

	public static class ChargedShot extends Buff{

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_XBOW;
		}

	}

}
