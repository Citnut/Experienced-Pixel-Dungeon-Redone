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
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

public class HandAxe extends MeleeWeapon {

	{
		image = ItemSpriteSheet.HAND_AXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		internalTier = tier = 2;
		ACC = 1.32f; //32% boost to accuracy
	}

	@Override
	public long max(long lvl) {
		return  5L*(tier()+1) +    //15 base, down from 18
				lvl*(tier()+2);   //+4
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(4+1.5*lvl) damage, roughly +55% base dmg, +75% scaling
		long dmgBoost = augment.damageFactor(4 + Math.round(1.5d*buffedLvl()));
		Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		long dmgBoost = levelKnown ? 4 + Math.round(1.5d*buffedLvl()) : 4;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(long level){
		int dmgBoost = 4 + Math.round(1.5f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
