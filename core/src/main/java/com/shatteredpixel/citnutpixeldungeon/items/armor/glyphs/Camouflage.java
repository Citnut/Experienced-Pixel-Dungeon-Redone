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

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;

public class Camouflage extends Armor.Glyph {

	private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing( 0x448822 );

	@Override
	public long proc(Armor armor, Char attacker, Char defender, long damage) {
		//no proc effect, see HighGrass.trample
		return damage;
	}

	public static void activate(Char ch, long level){
		Buff.prolong(ch, Invisibility.class, Math.round((3 + level/2f)* genericProcChanceMultiplier(ch)));
		if ( Dungeon.level.heroFOV[ch.pos] ) {
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GREEN;
	}

}

