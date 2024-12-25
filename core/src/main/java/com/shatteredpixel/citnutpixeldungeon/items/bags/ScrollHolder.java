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

package com.shatteredpixel.citnutpixeldungeon.items.bags;

import com.shatteredpixel.citnutpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.Stylus;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.citnutpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.citnutpixeldungeon.items.spells.IdentificationBomb;
import com.shatteredpixel.citnutpixeldungeon.items.spells.Spell;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

public class ScrollHolder extends Bag {

	{
		image = ItemSpriteSheet.HOLDER;
	}

	@Override
	public boolean canHold( Item item ) {
		if (item instanceof Scroll || (item instanceof Spell && !(item instanceof IdentificationBomb))
				|| item instanceof ArcaneResin || item instanceof Stylus){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	public int capacity(){
		return 48;
	}
	
	@Override
	public void onDetach( ) {
		super.onDetach();
		for (Item item : items) {
			if (item instanceof BeaconOfReturning) {
				((BeaconOfReturning) item).returnDepth = -1;
			}
		}
	}
	
	@Override
	public long value() {
		return 40;
	}

}
