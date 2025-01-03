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

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.items.Gold;
import com.shatteredpixel.citnutpixeldungeon.sprites.GnollSprite;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;
		
		HP = HT = 12;
		defenseSkill = 4;
		
		EXP = 2;
		maxLvl = 8;
		
		loot = Gold.class;
		lootChance = 0.5f;

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 115;
                defenseSkill = 27;
                EXP = 17;
                break;
            case 2:
                HP = HT = 1500;
                defenseSkill = 126;
                EXP = 140;
                break;
            case 3:
                HP = HT = 22000;
                defenseSkill = 360;
                EXP = 934;
                break;
            case 4:
                HP = HT = 1400000;
                defenseSkill = 1450;
                EXP = 31000;
                break;
            case 5:
                HP = HT = 500000000;
                defenseSkill = 22500;
                EXP = 12500000;
                break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Dungeon.NormalLongRange(28, 40);
            case 2: return Dungeon.NormalLongRange(130, 167);
            case 3: return Dungeon.NormalLongRange(512, 644);
            case 4: return Dungeon.NormalLongRange(4000, 7000);
            case 5: return Dungeon.NormalLongRange(400000, 575000);
        }
		return Dungeon.NormalLongRange( 1, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 42;
            case 2: return 190;
            case 3: return 540;
            case 4: return 1580;
            case 5: return 21500;
        }
		return 10;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Dungeon.NormalLongRange(6, 17);
            case 2: return Dungeon.NormalLongRange(69, 130);
            case 3: return Dungeon.NormalLongRange(275, 500);
            case 4: return Dungeon.NormalLongRange(3000, 6000);
            case 5: return Dungeon.NormalLongRange(375000, 525000);
        }
		return Dungeon.NormalLongRange(0, 2);
	}
}
