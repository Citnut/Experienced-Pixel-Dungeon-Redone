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

package com.shatteredpixel.citnutpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Thorns extends Armor.Glyph {

	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0x660022 );

	@Override
	public long proc(Armor armor, Char attacker, Char defender, long damage) {

		long level = Math.max(0, armor.buffedLvl());

		// lvl 0 - 16.7%
		// lvl 1 - 23.1%
		// lvl 2 - 28.5%
		float procChance = (level+2f)/(level+12f) * procChanceMultiplier(defender);
		if ( attacker.alignment != defender.alignment && Random.Float() < procChance ) {

			float powerMulti = Math.max(1f, procChance);

			Buff.affect( attacker, Bleeding.class).set( Math.round((4 + level)*powerMulti) );

		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return RED;
	}
}
