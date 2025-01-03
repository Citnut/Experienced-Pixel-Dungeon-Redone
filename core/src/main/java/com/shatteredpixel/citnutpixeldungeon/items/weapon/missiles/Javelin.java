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
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

import static com.shatteredpixel.citnutpixeldungeon.items.rings.RingOfWealth.genConsumableDrop;

public class Javelin extends MissileWeapon {

	{
		image = ItemSpriteSheet.JAVELIN;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1f;
		
		internalTier = tier = 4;
	}

    @Override
    protected void rangedHit(Char enemy, int cell) {
        super.rangedHit(enemy, cell);
        if (enemy == null || !enemy.isAlive()){
            Item item = genConsumableDrop(buffedLvl() * buffedLvl() + 2);
            if (item != null) Dungeon.level.drop(item, cell).sprite.drop();
        }
    }
}
