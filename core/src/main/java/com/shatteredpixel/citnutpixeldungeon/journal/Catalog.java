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

package com.shatteredpixel.citnutpixeldungeon.journal;

import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.items.Amulet;
import com.shatteredpixel.citnutpixeldungeon.items.Ankh;
import com.shatteredpixel.citnutpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.citnutpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.citnutpixeldungeon.items.Dewdrop;
import com.shatteredpixel.citnutpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Gold;
import com.shatteredpixel.citnutpixeldungeon.items.Honeypot;
import com.shatteredpixel.citnutpixeldungeon.items.KingsCrown;
import com.shatteredpixel.citnutpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.citnutpixeldungeon.items.OverloadBeacon;
import com.shatteredpixel.citnutpixeldungeon.items.Stylus;
import com.shatteredpixel.citnutpixeldungeon.items.TengusMask;
import com.shatteredpixel.citnutpixeldungeon.items.TicketToArena;
import com.shatteredpixel.citnutpixeldungeon.items.Torch;
import com.shatteredpixel.citnutpixeldungeon.items.Waterskin;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.items.bags.CheeseCheest;
import com.shatteredpixel.citnutpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.citnutpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.citnutpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.citnutpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.ArcaneBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Flashbang;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.ShockBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.citnutpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.AvaritiaFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.BasicFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.ChaosFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.GoldenFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.NeutroniumFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.fishingrods.SpiritualFishingRod;
import com.shatteredpixel.citnutpixeldungeon.items.food.Berry;
import com.shatteredpixel.citnutpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.citnutpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.citnutpixeldungeon.items.food.Cheese;
import com.shatteredpixel.citnutpixeldungeon.items.food.CheeseChunk;
import com.shatteredpixel.citnutpixeldungeon.items.food.Food;
import com.shatteredpixel.citnutpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.citnutpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.citnutpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.citnutpixeldungeon.items.food.Pasty;
import com.shatteredpixel.citnutpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.citnutpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.citnutpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.citnutpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.citnutpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.citnutpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.citnutpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.citnutpixeldungeon.items.keys.KeyToTruth;
import com.shatteredpixel.citnutpixeldungeon.items.keys.SkeletonKey;
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
import com.shatteredpixel.citnutpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.citnutpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.citnutpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.citnutpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.citnutpixeldungeon.items.quest.Embers;
import com.shatteredpixel.citnutpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.citnutpixeldungeon.items.quest.KingBlade;
import com.shatteredpixel.citnutpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.citnutpixeldungeon.items.quest.RatSkull;
import com.shatteredpixel.citnutpixeldungeon.items.quest.RustyShield;
import com.shatteredpixel.citnutpixeldungeon.items.quest.SuperPickaxe;
import com.shatteredpixel.citnutpixeldungeon.items.quest.TenguBomb;
import com.shatteredpixel.citnutpixeldungeon.items.quest.TenguShuriken;
import com.shatteredpixel.citnutpixeldungeon.items.remains.BowFragment;
import com.shatteredpixel.citnutpixeldungeon.items.remains.BrokenHilt;
import com.shatteredpixel.citnutpixeldungeon.items.remains.BrokenStaff;
import com.shatteredpixel.citnutpixeldungeon.items.remains.CloakScrap;
import com.shatteredpixel.citnutpixeldungeon.items.remains.SealShard;
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
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.AlchemyBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.BiggerGambleBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.BurntBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.DKTreasureBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.DM300TreasureBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.GambleBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.GooTreasureBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.IdealBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.QualityBag;
import com.shatteredpixel.citnutpixeldungeon.items.treasurebags.TenguTreasureBag;
import com.shatteredpixel.citnutpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.CreativeGloves;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.blacksmith.FantasmalStabber;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.blacksmith.FiringSnapper;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.blacksmith.GleamingStaff;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.blacksmith.RegrowingSlasher;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.blacksmith.StarlightSmasher;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.Clayball;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

//For items, but includes a few item-like effects, such as enchantments
public enum Catalog {

	//EQUIPMENT
	MELEE_WEAPONS,
	ARMOR,
	ENCHANTMENTS,
	GLYPHS,
	THROWN_WEAPONS,
	WANDS,
	RINGS,
	ARTIFACTS,
	TRINKETS,
	FISHING_RODS,
	MISC_EQUIPMENT,

	//CONSUMABLES
	POTIONS,
	SEEDS,
	SCROLLS,
	STONES,
	FOOD,
	EXOTIC_POTIONS,
	EXOTIC_SCROLLS,
	BOMBS,
	TIPPED_DARTS,
	BREWS_ELIXIRS,
	SPELLS,
	LOOT_BAGS,
	MISC_CONSUMABLES;

	//tracks whether an item has been collected while identified
	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	//tracks upgrades spent for equipment, uses for consumables
	private final LinkedHashMap<Class<?>, Long> useCount = new LinkedHashMap<>();
	
	public Collection<Class<?>> items(){
		return seen.keySet();
	}

	//should only be used when initializing
	private void addItems( Class<?>... items){
		for (Class<?> item : items){
			seen.put(item, false);
			useCount.put(item, 0L);
		}
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public int totalItems(){
		return seen.size();
	}

	public int totalSeen(){
		int seenTotal = 0;
		for (boolean itemSeen : seen.values()){
			if (itemSeen) seenTotal++;
		}
		return seenTotal;
	}

	static {

		MELEE_WEAPONS.addItems(Generator.Category.WEP_T1.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T2.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T3.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T4.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T5.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T6.classes);
		MELEE_WEAPONS.addItems(StarlightSmasher.class, FiringSnapper.class, RegrowingSlasher.class,
				GleamingStaff.class, FantasmalStabber.class);

		ARMOR.addItems(Generator.Category.ARMOR.classes);

		THROWN_WEAPONS.addItems(Generator.Category.MIS_T1.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T2.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T3.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T4.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T5.classes);

		ENCHANTMENTS.addItems(Weapon.Enchantment.common);
		ENCHANTMENTS.addItems(Weapon.Enchantment.uncommon);
		ENCHANTMENTS.addItems(Weapon.Enchantment.rare);
		ENCHANTMENTS.addItems(Weapon.Enchantment.curses);

		GLYPHS.addItems(Armor.Glyph.common);
		GLYPHS.addItems(Armor.Glyph.uncommon);
		GLYPHS.addItems(Armor.Glyph.rare);
		GLYPHS.addItems(Armor.Glyph.curses);

		WANDS.addItems(Generator.Category.WAND.classes);

		RINGS.addItems(Generator.Category.RING.classes);

		ARTIFACTS.addItems(Generator.Category.ARTIFACT.classes);

		TRINKETS.addItems(Generator.Category.TRINKET.classes);

		FISHING_RODS.addItems(BasicFishingRod.class, GoldenFishingRod.class, NeutroniumFishingRod.class,
				AvaritiaFishingRod.class, ChaosFishingRod.class, SpiritualFishingRod.class);

		MISC_EQUIPMENT.addItems(BrokenSeal.class, SpiritBow.class, Waterskin.class, VelvetPouch.class,
				PotionBandolier.class, ScrollHolder.class, MagicalHolster.class, Amulet.class,
				CheeseCheest.class);



		POTIONS.addItems(Generator.Category.POTION.classes);

		SCROLLS.addItems(Generator.Category.SCROLL.classes);

		SEEDS.addItems(Generator.Category.SEED.classes);

		STONES.addItems(Generator.Category.STONE.classes);

		FOOD.addItems( Food.class, Pasty.class, MysteryMeat.class, ChargrilledMeat.class,
				StewedMeat.class, FrozenCarpaccio.class, SmallRation.class, Berry.class,
				SupplyRation.class, Blandfruit.class, PhantomMeat.class, MeatPie.class,
				Cheese.class, CheeseChunk.class );

		EXOTIC_POTIONS.addItems(ExoticPotion.exoToReg.keySet().toArray(new Class[0]));

		EXOTIC_SCROLLS.addItems(ExoticScroll.exoToReg.keySet().toArray(new Class[0]));

		BOMBS.addItems( Bomb.class, FrostBomb.class, Firebomb.class, Flashbang.class, RegrowthBomb.class,
				WoollyBomb.class, Noisemaker.class, ShockBomb.class, HolyBomb.class, ArcaneBomb.class, ShrapnelBomb.class, IdentificationBomb.class);

		TIPPED_DARTS.addItems(TippedDart.types.values().toArray(new Class[0]));

		BREWS_ELIXIRS.addItems( UnstableBrew.class, InfernalBrew.class, BlizzardBrew.class,
				ShockingBrew.class, CausticBrew.class, AquaBrew.class, ElixirOfHoneyedHealing.class,
				ElixirOfAquaticRejuvenation.class, ElixirOfArcaneArmor.class, ElixirOfDragonsBlood.class,
				ElixirOfIcyTouch.class, ElixirOfToxicEssence.class, ElixirOfMight.class, ElixirOfFeatherFall.class);

		SPELLS.addItems( UnstableSpell.class, WildEnergy.class, TelekineticGrab.class, PhaseShift.class,
				Alchemize.class, CurseInfusion.class, /*MagicalInfusion.class,*/ Recycle.class,
				ReclaimTrap.class, SummonElemental.class, BeaconOfReturning.class, FireBooster.class,
				Vampirism.class, RespawnBooster.class);

		LOOT_BAGS.addItems(GambleBag.class, BiggerGambleBag.class, QualityBag.class, AlchemyBag.class,
				GooTreasureBag.class, RatSkull.class, TenguTreasureBag.class, TenguShuriken.class, TenguBomb.class,
				DM300TreasureBag.class, RustyShield.class, SuperPickaxe.class, DKTreasureBag.class, KingBlade.class, OverloadBeacon.class,
				BurntBag.class, IdealBag.class, IdealBag.Plutonium.class, IdealBag.BrokenEnderiumBlade.class,
				IdealBag.EnergyBottle.class, IdealBag.OsmiridiumPlate.class);

		MISC_CONSUMABLES.addItems( Gold.class, EnergyCrystal.class, Dewdrop.class,
				IronKey.class, GoldenKey.class, CrystalKey.class, SkeletonKey.class,
				TrinketCatalyst.class, Stylus.class, Torch.class, Honeypot.class, Ankh.class,
				CorpseDust.class, Embers.class, CeremonialCandle.class, DarkGold.class, DwarfToken.class,
				GooBlob.class, TengusMask.class, MetalShard.class, KingsCrown.class,
				LiquidMetal.class, ArcaneResin.class, TicketToArena.class,
				SealShard.class, BrokenStaff.class, CloakScrap.class, BowFragment.class, BrokenHilt.class,
				CreativeGloves.class, Clayball.class, KeyToTruth.class);

	}

	//old badges for pre-2.5
	public static LinkedHashMap<Catalog, Badges.Badge> catalogBadges = new LinkedHashMap<>();
	static {
		catalogBadges.put(MELEE_WEAPONS, Badges.Badge.ALL_WEAPONS_IDENTIFIED);
		catalogBadges.put(ARMOR, Badges.Badge.ALL_ARMOR_IDENTIFIED);
		catalogBadges.put(WANDS, Badges.Badge.ALL_WANDS_IDENTIFIED);
		catalogBadges.put(RINGS, Badges.Badge.ALL_RINGS_IDENTIFIED);
		catalogBadges.put(ARTIFACTS, Badges.Badge.ALL_ARTIFACTS_IDENTIFIED);
		catalogBadges.put(POTIONS, Badges.Badge.ALL_POTIONS_IDENTIFIED);
		catalogBadges.put(SCROLLS, Badges.Badge.ALL_SCROLLS_IDENTIFIED);
	}

	public static ArrayList<Catalog> equipmentCatalogs = new ArrayList<>();
	static {
		equipmentCatalogs.add(MELEE_WEAPONS);
		equipmentCatalogs.add(ARMOR);
		equipmentCatalogs.add(ENCHANTMENTS);
		equipmentCatalogs.add(GLYPHS);
		equipmentCatalogs.add(THROWN_WEAPONS);
		equipmentCatalogs.add(WANDS);
		equipmentCatalogs.add(RINGS);
		equipmentCatalogs.add(ARTIFACTS);
		equipmentCatalogs.add(TRINKETS);
		equipmentCatalogs.add(FISHING_RODS);
		equipmentCatalogs.add(MISC_EQUIPMENT);
	}

	public static ArrayList<Catalog> consumableCatalogs = new ArrayList<>();
	static {
		consumableCatalogs.add(POTIONS);
		consumableCatalogs.add(SCROLLS);
		consumableCatalogs.add(SEEDS);
		consumableCatalogs.add(STONES);
		consumableCatalogs.add(FOOD);
		consumableCatalogs.add(EXOTIC_POTIONS);
		consumableCatalogs.add(EXOTIC_SCROLLS);
		consumableCatalogs.add(BOMBS);
		consumableCatalogs.add(TIPPED_DARTS);
		consumableCatalogs.add(BREWS_ELIXIRS);
		consumableCatalogs.add(SPELLS);
		consumableCatalogs.add(LOOT_BAGS);
		consumableCatalogs.add(MISC_CONSUMABLES);
	}

	public static boolean isSeen(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(cls)) {
				return cat.seen.get(cls);
			}
		}
		return false;
	}
	
	public static void setSeen(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
				cat.seen.put(cls, true);
				Journal.saveNeeded = true;
			}
		}
		Badges.validateCatalogBadges();
	}

	public static long useCount(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.useCount.containsKey(cls)) {
				return cat.useCount.get(cls);
			}
		}
		return 0;
	}

	public static void countUse(Class<?> cls){
		countUses(cls, 1);
	}

	public static void countUses(Class<?> cls, long uses){
		for (Catalog cat : values()) {
			if (cat.useCount.containsKey(cls) && cat.useCount.get(cls) != Long.MAX_VALUE) {
				cat.useCount.put(cls, cat.useCount.get(cls)+uses);
				if (cat.useCount.get(cls) < -1_000_000_000){ //to catch cases of overflow
					cat.useCount.put(cls, Long.MAX_VALUE);
				}
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String CATALOG_CLASSES = "catalog_classes";
	private static final String CATALOG_SEEN    = "catalog_seen";
	private static final String CATALOG_USES    = "catalog_uses";
	
	public static void store( Bundle bundle ){

		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seen = new ArrayList<>();
		ArrayList<Long> uses = new ArrayList<>();
		
		for (Catalog cat : values()) {
			for (Class<?> item : cat.items()) {
				if (cat.seen.get(item) || cat.useCount.get(item) > 0){
					classes.add(item);
					seen.add(cat.seen.get(item));
					uses.add(cat.useCount.get(item));
				}
			}
		}

		Class<?>[] storeCls = new Class[classes.size()];
		boolean[] storeSeen = new boolean[seen.size()];
		long[] storeUses = new long[uses.size()];

		for (int i = 0; i < storeCls.length; i++){
			storeCls[i] = classes.get(i);
			storeSeen[i] = seen.get(i);
			storeUses[i] = uses.get(i);
		}
		
		bundle.put( CATALOG_CLASSES, storeCls );
		bundle.put( CATALOG_SEEN, storeSeen );
		bundle.put( CATALOG_USES, storeUses );
		
	}

	//pre-v2.5
	private static final String CATALOG_ITEMS = "catalog_items";

	public static void restore( Bundle bundle ){

		//old logic for pre-v2.5 catalog-specific badges
		Badges.loadGlobal();
		for (Catalog cat : values()){
			if (Badges.isUnlocked(catalogBadges.get(cat))){
				for (Class<?> item : cat.items()){
					cat.seen.put(item, true);
				}
			}
		}
		if (bundle.contains(CATALOG_ITEMS)) {
			for (Class<?> cls : Arrays.asList(bundle.getClassArray(CATALOG_ITEMS))){
				for (Catalog cat : values()) {
					if (cat.seen.containsKey(cls)) {
						cat.seen.put(cls, true);
					}
				}
			}
		}
		//end of old logic

		if (bundle.contains(CATALOG_CLASSES)){
			Class<?>[] classes = bundle.getClassArray(CATALOG_CLASSES);
			boolean[] seen = bundle.getBooleanArray(CATALOG_SEEN);
			long[] uses = bundle.getLongArray(CATALOG_USES);

			for (int i = 0; i < classes.length; i++){
				for (Catalog cat : values()) {
					if (cat.seen.containsKey(classes[i])) {
						cat.seen.put(classes[i], seen[i]);
						cat.useCount.put(classes[i], uses[i]);
					}
				}

			}
		}

	}
	
}
