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

package com.shatteredpixel.citnutpixeldungeon.items.spells;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.citnutpixeldungeon.effects.FloatingText;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.citnutpixeldungeon.journal.Catalog;
import com.shatteredpixel.citnutpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;

public class RespawnBooster extends Spell{

    {
        image = ItemSpriteSheet.RESPAWN;

        talentChance = 1/(float) Recipe.OUT_QUANTITY;
    }

    @Override
    protected void onCast(Hero hero) {
        if (Dungeon.respawn_timer > 1) {
            Dungeon.respawn_timer = (int) GameMath.gate(1, Dungeon.respawn_timer - 2, 50);
            hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "-2", FloatingText.RESPAWN_BOOST);
        }
        else {
            Dungeon.respawn_timer = Dungeon.respawn_timer * 0.7f;
            hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "-30%", FloatingText.RESPAWN_BOOST);
        }
        if (Dungeon.respawn_timer % 3 == 0 || Dungeon.respawn_timer <= 0.7f){
            Dungeon.additionalMobs = Dungeon.additionalMobs + 1;
            hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "+1", FloatingText.MOB_BOOST);
        }

        for (int i : PathFinder.NEIGHBOURS9){
            CellEmitter.center(hero.pos + i).burst(SacrificialParticle.FACTORY, 10);
        }
        Catalog.countUse(getClass());
        detach( curUser.belongings.backpack );
    }

    @Override
    public long value() {
        return (long)(60 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public long energyVal() {
        return (long)(12 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.citnutpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{PotionOfExperience.class, CurseInfusion.class};
            inQuantity = new int[]{1, 1};

            cost = 33;

            output = RespawnBooster.class;
            outQuantity = OUT_QUANTITY;
        }

    }
}
