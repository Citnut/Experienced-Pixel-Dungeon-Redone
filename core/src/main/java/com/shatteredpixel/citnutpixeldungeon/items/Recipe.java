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

package com.shatteredpixel.citnutpixeldungeon.items;

import com.shatteredpixel.citnutpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.citnutpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.citnutpixeldungeon.items.food.CheeseChunk;
import com.shatteredpixel.citnutpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.citnutpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.citnutpixeldungeon.items.potions.Potion;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.citnutpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.citnutpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.citnutpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.citnutpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.citnutpixeldungeon.items.spells.FireBooster;
import com.shatteredpixel.citnutpixeldungeon.items.spells.IdentificationBomb;
import com.shatteredpixel.citnutpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.citnutpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.citnutpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.citnutpixeldungeon.items.spells.RespawnBooster;
import com.shatteredpixel.citnutpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.citnutpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.citnutpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.citnutpixeldungeon.items.spells.Vampirism;
import com.shatteredpixel.citnutpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.citnutpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.citnutpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.citnutpixeldungeon.items.wands.Wand;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfEarthblast;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class Recipe {
	
	public abstract boolean testIngredients(ArrayList<Item> ingredients);
	
	public abstract long cost(ArrayList<Item> ingredients);
	
	public abstract Item brew(ArrayList<Item> ingredients);
	
	public abstract Item sampleOutput(ArrayList<Item> ingredients);
	
	//subclass for the common situation of a recipe with static inputs and outputs
	public static abstract class SimpleRecipe extends Recipe {
		
		//*** These elements must be filled in by subclasses
		protected Class<?extends Item>[] inputs; //each class should be unique
		protected int[] inQuantity;
		
		protected int cost;
		
		protected Class<?extends Item> output;
		protected int outQuantity;
		//***
		
		//gets a simple list of items based on inputs
		public ArrayList<Item> getIngredients() {
			ArrayList<Item> result = new ArrayList<>();
			for (int i = 0; i < inputs.length; i++) {
				Item ingredient = Reflection.newInstance(inputs[i]);
				ingredient.quantity(inQuantity[i]);
				result.add(ingredient);
			}
			return result;
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			
			int[] needed = inQuantity.clone();
			
			for (Item ingredient : ingredients){
				if (!ingredient.isIdentified()) return false;
				for (int i = 0; i < inputs.length; i++){
					if (ingredient.getClass() == inputs[i]){
						needed[i] -= ingredient.quantity();
						break;
					}
				}
			}
			
			for (long i : needed){
				if (i > 0){
					return false;
				}
			}
			
			return true;
		}
		
		public long cost(ArrayList<Item> ingredients){
			return cost;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			int[] needed = inQuantity.clone();
			
			for (Item ingredient : ingredients){
				for (int i = 0; i < inputs.length; i++) {
					if (ingredient.getClass() == inputs[i] && needed[i] > 0) {
						if (needed[i] <= ingredient.quantity()) {
							ingredient.quantity(ingredient.quantity() - needed[i]);
							needed[i] = 0;
						} else {
							needed[i] -= ingredient.quantity();
							ingredient.quantity(0);
						}
					}
				}
			}
			
			//sample output and real output are identical in this case.
			return sampleOutput(null);
		}
		
		//ingredients are ignored, as output doesn't vary
		public Item sampleOutput(ArrayList<Item> ingredients){
			try {
				Item result = Reflection.newInstance(output);
				result.quantity(outQuantity);
				return result;
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException( e );
				return null;
			}
		}
	}
	
	
	//*******
	// Static members
	//*******

	private static Recipe[] variableRecipes = new Recipe[]{
			new LiquidMetal.Recipe()
	};

	private static Recipe[] oneIngredientRecipes = new Recipe[]{
		new Scroll.ScrollToStone(),
		new ExoticPotion.PotionToExotic(),
		new ExoticScroll.ScrollToExotic(),
		new ArcaneResin.Recipe(),
		new BlizzardBrew.Recipe(),
		new InfernalBrew.Recipe(),
		new AquaBrew.Recipe(),
		new ShockingBrew.Recipe(),
		new ElixirOfDragonsBlood.Recipe(),
		new ElixirOfIcyTouch.Recipe(),
		new ElixirOfToxicEssence.Recipe(),
		new ElixirOfMight.Recipe(),
		new ElixirOfFeatherFall.Recipe(),
//		new MagicalInfusion.Recipe(),
		new BeaconOfReturning.Recipe(),
		new PhaseShift.Recipe(),
		new Recycle.Recipe(),
		new TelekineticGrab.Recipe(),
		new SummonElemental.Recipe(),
		new StewedMeat.oneMeat(),
		new TrinketCatalyst.Recipe(),
		new Trinket.UpgradeTrinket()
	};
	
	private static Recipe[] twoIngredientRecipes = new Recipe[]{
		new Blandfruit.CookFruit(),
		new Bomb.EnhanceBomb(),
		new UnstableBrew.Recipe(),
		new CausticBrew.Recipe(),
		new ElixirOfArcaneArmor.Recipe(),
		new ElixirOfAquaticRejuvenation.Recipe(),
		new ElixirOfHoneyedHealing.Recipe(),
		new UnstableSpell.Recipe(),
		new Alchemize.Recipe(),
		new CurseInfusion.Recipe(),
		new ReclaimTrap.Recipe(),
		new WildEnergy.Recipe(),
		new StewedMeat.twoMeat(),
		new FireBooster.Recipe(),
		new RespawnBooster.Recipe(),
		new Vampirism.Recipe(),
		new IdentificationBomb.Recipe(),
		new CheeseChunk.oneMeat()
	};
	
	private static Recipe[] threeIngredientRecipes = new Recipe[]{
		new Potion.SeedToPotion(),
		new StewedMeat.threeMeat(),
		new MeatPie.Recipe(),
		new WandOfEarthblast.Recipe()
	};
	
	public static ArrayList<Recipe> findRecipes(ArrayList<Item> ingredients){

		ArrayList<Recipe> result = new ArrayList<>();

		for (Recipe recipe : variableRecipes){
			if (recipe.testIngredients(ingredients)){
				result.add(recipe);
			}
		}

		if (ingredients.size() == 1){
			for (Recipe recipe : oneIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
			
		} else if (ingredients.size() == 2){
			for (Recipe recipe : twoIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
			
		} else if (ingredients.size() == 3){
			for (Recipe recipe : threeIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
		}
		
		return result;
	}
	
	public static boolean usableInRecipe(Item item){
		if (item instanceof EquipableItem){
			//only thrown weapons and wands allowed among equipment items
			return item.isIdentified() && !item.cursed && item instanceof MissileWeapon;
		} else if (item instanceof Wand) {
			return item.isIdentified() && !item.cursed;
		} else {
			//other items can be unidentified, but not cursed
			return !item.cursed;
		}
	}
}


