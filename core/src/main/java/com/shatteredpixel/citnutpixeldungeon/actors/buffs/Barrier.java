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

package com.shatteredpixel.citnutpixeldungeon.actors.buffs;

import com.shatteredpixel.citnutpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.citnutpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Barrier extends ShieldBuff {
	
	{
		type = buffType.POSITIVE;
	}

	double partialLostShield;

	@Override
	public void incShield(long amt) {
		super.incShield(amt);
		partialLostShield = 0;
	}

	@Override
	public void setShield(long shield) {
		super.setShield(shield);
		if (shielding() == shield) partialLostShield = 0;
	}

	@Override
	public boolean act() {

		partialLostShield += Math.min(1f, shielding()/20f);

		if (partialLostShield >= 1f) {
			absorbDamage(1);
			partialLostShield = 0;
		}
		
		if (shielding() <= 0){
			detach();
		}
		
		spend( TICK );
		
		return true;
	}
	
	@Override
	public void fx(boolean on) {
		if (on) {
			target.sprite.add(CharSprite.State.SHIELDED);
		} else if (target.buff(Blocking.BlockBuff.class) == null) {
			target.sprite.remove(CharSprite.State.SHIELDED);
		}
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 1f, 2f);
	}

	@Override
	public String iconTextDisplay() {
		return Long.toString(shielding());
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", shielding());
	}

	private static final String PARTIAL_LOST_SHIELD = "partial_lost_shield";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PARTIAL_LOST_SHIELD, partialLostShield);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		partialLostShield = bundle.getDouble(PARTIAL_LOST_SHIELD);
	}
}
