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

package com.shatteredpixel.citnutpixeldungeon.items.potions.brews;

import com.shatteredpixel.citnutpixeldungeon.Challenges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.potions.Potion;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.citnutpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.citnutpixeldungeon.plants.Plant;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

public class UnstableBrew extends Brew {

	{
		image = ItemSpriteSheet.BREW_UNSTABLE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_DRINK);
		return actions;
	}

	@Override
	public String defaultAction() {
		return AC_CHOOSE;
	}

	private static HashMap<Class<? extends Potion>, Float> potionChances = new HashMap<>();
	static {
		potionChances.put(PotionOfHealing.class, 3f);
		potionChances.put(PotionOfMindVision.class, 2f);
		potionChances.put(PotionOfFrost.class, 2f);
		potionChances.put(PotionOfLiquidFlame.class, 2f);
		potionChances.put(PotionOfToxicGas.class, 2f);
		potionChances.put(PotionOfHaste.class, 2f);
		potionChances.put(PotionOfInvisibility.class, 2f);
		potionChances.put(PotionOfLevitation.class, 2f);
		potionChances.put(PotionOfParalyticGas.class, 2f);
		potionChances.put(PotionOfPurity.class, 2f);
		potionChances.put(PotionOfExperience.class, 1f);
	}
	
	@Override
	public void apply(Hero hero) {
		//Don't allow this to roll healing in pharma
		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			potionChances.put(PotionOfHealing.class, 0f);
		}

		Potion p = Reflection.newInstance(Random.chances(potionChances));

		//reroll the potion if it wasn't a good potion to drink
		while (mustThrowPots.contains(p.getClass())){
			p = Reflection.newInstance(Random.chances(potionChances));
		}

		p.anonymize();
		p.apply(hero);

		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			potionChances.put(PotionOfHealing.class, 3f);
		}
	}
	
	@Override
	public void shatter(int cell) {
		Potion p = Reflection.newInstance(Dungeon.chances(potionChances));

		//reroll the potion if it wasn't a good potion to throw
		while (!mustThrowPots.contains(p.getClass()) && !canThrowPots.contains(p.getClass())){
			p = Reflection.newInstance(Random.chances(potionChances));
		}
		p.anonymize();
		curItem = p;
		p.shatter(cell);
	}
	
	@Override
	public boolean isKnown() {
		return true;
	}

	//lower values, as it's cheaper to make
	@Override
	public long value() {
		return 40 * quantity;
	}

	@Override
	public long energyVal() {
		return 8 * quantity;
	}

	public static class Recipe extends com.shatteredpixel.citnutpixeldungeon.items.Recipe {
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean potion = false;
			boolean seed = false;

			for (Item i : ingredients){
				if (i instanceof Plant.Seed) {
					seed = true;
					//if it is a regular or exotic potion
				} else if (ExoticPotion.regToExo.containsKey(i.getClass())
						|| ExoticPotion.regToExo.containsValue(i.getClass())) {
					potion = true;
				}
			}

			return potion && seed;
		}

		@Override
		public long cost(ArrayList<Item> ingredients) {
			return 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {

			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
			}
			
			return sampleOutput(null);
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new UnstableBrew();
		}
	}
	
}
