/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.items.treasurebags;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Gold;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.citnutpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.citnutpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.citnutpixeldungeon.items.quest.RustyShield;
import com.shatteredpixel.citnutpixeldungeon.items.quest.SuperPickaxe;
import com.shatteredpixel.citnutpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.citnutpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class DM300TreasureBag extends TreasureBag {
    {
        image = ItemSpriteSheet.DM300_BAG;
    }

    @Override
    protected ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new MetalShard().quantity(Random.Int(2, 4)));
        items.add(new Gold().quantity(Random.Int( 1000 + Dungeon.escalatingDepth() * 140, 1600 + Dungeon.escalatingDepth() * 165 )));
        items.add(Generator.randomUsingDefaults(Generator.Category.ARMOR).random());
        if (Dungeon.cycle > 0){
            if (!Dungeon.LimitedDrops.DM_DROPS.dropped()){
                items.add(new CapeOfThorns());
                items.add(new RustyShield());
                if (Dungeon.cycle > 1){
                    items.add(new SuperPickaxe());
                }
                Dungeon.LimitedDrops.DM_DROPS.drop();
            }
            for (int i = 0; i < 20; i++) items.add(Generator.randomUsingDefaults(Generator.Category.SCROLL).random());
            for (int i = 0; i < 20; i++) items.add(Reflection.newInstance(ExoticPotion.exoToReg.get(Generator.randomUsingDefaults(Generator.Category.SCROLL).getClass())));
            items.add(Generator.randomUsingDefaults(Generator.Category.FOOD));
            items.add(new ReclaimTrap().quantity(10));
            items.add(new CurseInfusion().quantity(10));
        }
        if (Dungeon.cycle > 1){
            items.add(new MetalShard().quantity(Random.Int(5, 15)));
            for (int i = 0; i < 50; i++) items.add(Generator.random(Bomb.class));
        }
        return items;
    }
}
