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

package com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

public class Bolas extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.BOLAS;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		
		internalTier = tier = 3;
		baseUses = 5;
	}
	
	@Override
	public long max(long lvl) {
		return  5L * tier() +                      //9 base, down from 15
				(tier == 1 ? 3*lvl : tier()*lvl*2); //scaling unchanged
	}
	
	@Override
	public long proc( Char attacker, Char defender, long damage ) {
		Buff.prolong( defender, Cripple.class, Cripple.DURATION );
		return super.proc( attacker, defender, damage );
	}
}