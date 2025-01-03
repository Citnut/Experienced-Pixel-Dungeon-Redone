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

package com.shatteredpixel.citnutpixeldungeon.items.spells;

import com.shatteredpixel.citnutpixeldungeon.Challenges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.effects.Speck;
import com.shatteredpixel.citnutpixeldungeon.effects.Transmuting;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.potions.Potion;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.citnutpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.citnutpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.plants.Plant;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

public class Recycle extends InventorySpell {
	
	{
		image = ItemSpriteSheet.RECYCLE;

		talentFactor = 2;
		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew)) ||
				item instanceof Scroll ||
				item instanceof Plant.Seed ||
				item instanceof Runestone ||
				item instanceof TippedDart;
	}

	@Override
	protected void onItemSelected(Item item) {
		Item result;
		do {
			if (item instanceof Potion) {
				result = Generator.randomUsingDefaults(Generator.Category.POTION);
				if (item instanceof ExoticPotion){
					result = Reflection.newInstance(ExoticPotion.regToExo.get(result.getClass()));
				}
			} else if (item instanceof Scroll) {
				result = Generator.randomUsingDefaults(Generator.Category.SCROLL);
				if (item instanceof ExoticScroll){
					result = Reflection.newInstance(ExoticScroll.regToExo.get(result.getClass()));
				}
			} else if (item instanceof Plant.Seed) {
				result = Generator.randomUsingDefaults(Generator.Category.SEED);
			} else if (item instanceof Runestone) {
				result = Generator.randomUsingDefaults(Generator.Category.STONE);
			} else {
				result = TippedDart.randomTipped(1);
			}
		} while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));
		
		item.detach(curUser.belongings.backpack);
		GLog.p(Messages.get(this, "recycled", result.name()));
		if (!result.collect()){
			Dungeon.level.drop(result, curUser.pos).sprite.drop();
		}
		Transmuting.show(curUser, item, result);
		curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
	}
	
	@Override
	public long value() {
		return (long)(60 * (quantity/(float)Recipe.OUT_QUANTITY));
	}

	@Override
	public long energyVal() {
		return (long)(12 * (quantity/(float)Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends com.shatteredpixel.citnutpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 12;

		{
			inputs =  new Class[]{ScrollOfTransmutation.class};
			inQuantity = new int[]{1};
			
			cost = 12;
			
			output = Recycle.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}
}
