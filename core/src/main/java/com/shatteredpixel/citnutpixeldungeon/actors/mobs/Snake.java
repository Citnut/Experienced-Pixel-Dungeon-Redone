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

import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.journal.Document;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.SnakeSprite;
import com.watabou.utils.Random;

public class Snake extends Mob {
	
	{
		spriteClass = SnakeSprite.class;
		
		HP = HT = 4;
		defenseSkill = 25;
		
		EXP = 2;
		maxLvl = 7;
		
		loot = Generator.Category.SEED;
		lootChance = 0.25f;

        switch (Dungeon.cycle){
            case 1:
                HP = HT = 55;
                defenseSkill = 82;
                EXP = 16;
                break;
            case 2:
                HP = HT = 985;
                defenseSkill = 550;
                EXP = 136;
                break;
            case 3:
                HP = HT = 9000;
                defenseSkill = 1200;
                EXP = 921;
                break;
            case 4:
                HP = HT = 900000;
                defenseSkill = 9000;
                EXP = 30000;
                break;
            case 5:
                HP = HT = 300000000;
                defenseSkill = 56000;
                EXP = 12500000;
                break;
        }
	}
	
	@Override
	public long damageRoll() {
        switch (Dungeon.cycle) {
            case 1: return Random.NormalIntRange(21, 36);
            case 2: return Random.NormalIntRange(120, 160);
            case 3: return Random.NormalIntRange(498, 621);
            case 4: return Random.NormalIntRange(3900, 5780);
            case 5: return Random.NormalIntRange(370000, 525000);
        }
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
        switch (Dungeon.cycle){
            case 1: return 40;
            case 2: return 180;
            case 3: return 540;
            case 4: return 1400;
            case 5: return 21000;
        }
	    return 10;
	}

	private static int dodges = 0;

	@Override
	public String defenseVerb() {
		dodges++;
		if ((dodges >= 2 && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_SURPRISE_ATKS))
				|| (dodges >= 4 && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1))){
			GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_SURPRISE_ATKS);
			dodges = 0;
		}
		return super.defenseVerb();
	}
}
