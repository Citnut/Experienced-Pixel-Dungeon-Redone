/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
import com.shatteredpixel.citnutpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Acidic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Albino;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Bandit;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.BlackMimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.CausticSlime;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.FetidRat;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GnollSapper;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.GreatCrab;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.OOFThief;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.PhantomPiranha;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Senior;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.SpectralNecromancer;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Bbat;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Hook;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.citnutpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.citnutpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.citnutpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GnollRockfallTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.citnutpixeldungeon.plants.Blindweed;
import com.shatteredpixel.citnutpixeldungeon.plants.Earthroot;
import com.shatteredpixel.citnutpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.citnutpixeldungeon.plants.Firebloom;
import com.shatteredpixel.citnutpixeldungeon.plants.Icecap;
import com.shatteredpixel.citnutpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.citnutpixeldungeon.plants.Rotberry;
import com.shatteredpixel.citnutpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.citnutpixeldungeon.plants.Starflower;
import com.shatteredpixel.citnutpixeldungeon.plants.Stormvine;
import com.shatteredpixel.citnutpixeldungeon.plants.Sungrass;
import com.shatteredpixel.citnutpixeldungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

//contains all the game's various entities, mostly enemies, NPCS, and allies, but also traps and plants
public enum Bestiary {

	REGIONAL,
	BOSSES,
	UNIVERSAL,
	RARE,
	QUEST,
	NEUTRAL,
	ALLY,
	TRAP,
	PLANT;

	//tracks whether an entity has been encountered
	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	//tracks enemy kills, trap activations, plant tramples, or just sets to 1 for seen on allies
	private final LinkedHashMap<Class<?>, Long> encounterCount = new LinkedHashMap<>();

	//should only be used when initializing
	private void addEntities(Class<?>... classes ){
		for (Class<?> cls : classes){
			seen.put(cls, false);
			encounterCount.put(cls, 0L);
		}
	}

	public Collection<Class<?>> entities(){
		return seen.keySet();
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public int totalEntities(){
		return seen.size();
	}

	public int totalSeen(){
		int seenTotal = 0;
		for (boolean entitySeen : seen.values()){
			if (entitySeen) seenTotal++;
		}
		return seenTotal;
	}

	static {

		REGIONAL.addEntities(Rat.class, Snake.class, Gnoll.class, Swarm.class, Crab.class, Slime.class,
				Skeleton.class, Thief.class, DM100.class, Guard.class, Necromancer.class,
				Bat.class, Brute.class, Shaman.RedShaman.class, Shaman.BlueShaman.class, Shaman.PurpleShaman.class, Spinner.class, DM200.class,
				Ghoul.class, Elemental.FireElemental.class, Elemental.FrostElemental.class, Elemental.ShockElemental.class, Warlock.class, Monk.class, Golem.class,
				RipperDemon.class, DemonSpawner.class, Succubus.class, Eye.class, Scorpio.class);

		BOSSES.addEntities(Goo.class,
				Tengu.class,
				Pylon.class, DM300.class,
				DwarfKing.class,
				YogDzewa.Larva.class, YogFist.BurningFist.class, YogFist.SoiledFist.class, YogFist.RottingFist.class, YogFist.RustedFist.class,YogFist.BrightFist.class, YogFist.DarkFist.class, YogDzewa.class, BlackMimic.class);

		UNIVERSAL.addEntities(Wraith.class, Piranha.class, Mimic.class, GoldenMimic.class, EbonyMimic.class, Statue.class, GuardianTrap.Guardian.class, SentryRoom.Sentry.class);

		RARE.addEntities(Albino.class, CausticSlime.class,
				Bandit.class, SpectralNecromancer.class,
				ArmoredBrute.class, DM201.class,
				Elemental.ChaosElemental.class, Senior.class,
				Acidic.class,
				TormentedSpirit.class, PhantomPiranha.class, CrystalMimic.class, ArmoredStatue.class, OOFThief.class);

		QUEST.addEntities(FetidRat.class, GnollTrickster.class, GreatCrab.class,
				Elemental.NewbornFireElemental.class, RotLasher.class, RotHeart.class,
				CrystalWisp.class, CrystalGuardian.class, CrystalSpire.class, GnollGuard.class, GnollSapper.class, GnollGeomancer.class);

		NEUTRAL.addEntities(Ghost.class, RatKing.class, Shopkeeper.class, Wandmaker.class, Blacksmith.class, Imp.class, Sheep.class, Bee.class);

		ALLY.addEntities(MirrorImage.class, PrismaticImage.class,
				DriedRose.GhostHero.class,
				WandOfWarding.Ward.class, WandOfWarding.Ward.WardSentry.class, WandOfLivingEarth.EarthGuardian.class,
				ShadowClone.ShadowAlly.class, SmokeBomb.NinjaLog.class, SpiritHawk.HawkAlly.class, Bbat.class, Hook.class);

		TRAP.addEntities(WornDartTrap.class, PoisonDartTrap.class, DisintegrationTrap.class, GatewayTrap.class,
				ChillingTrap.class, BurningTrap.class, ShockingTrap.class, AlarmTrap.class, GrippingTrap.class, TeleportationTrap.class, OozeTrap.class,
				FrostTrap.class, BlazingTrap.class, StormTrap.class, GuardianTrap.class, FlashingTrap.class, WarpingTrap.class,
				ConfusionTrap.class, ToxicTrap.class, CorrosionTrap.class,
				FlockTrap.class, SummoningTrap.class, WeakeningTrap.class, CursingTrap.class,
				GeyserTrap.class, ExplosiveTrap.class, RockfallTrap.class, PitfallTrap.class,
				DistortionTrap.class, DisarmingTrap.class, GrimTrap.class);

		PLANT.addEntities(Rotberry.class, Sungrass.class, Fadeleaf.class, Icecap.class,
				Firebloom.class, Sorrowmoss.class, Swiftthistle.class, Blindweed.class,
				Stormvine.class, Earthroot.class, Mageroyal.class, Starflower.class,
				BlandfruitBush.class,
				WandOfRegrowth.Dewcatcher.class, WandOfRegrowth.Seedpod.class, WandOfRegrowth.Lotus.class);

	}

	//some mobs and traps have different internal classes in some cases, so need to convert here
	private static final HashMap<Class<?>, Class<?>> classConversions = new HashMap<>();
	static {
		classConversions.put(CorpseDust.DustWraith.class,      Wraith.class);

		classConversions.put(Necromancer.NecroSkeleton.class,  Skeleton.class);

		classConversions.put(TenguDartTrap.class,              PoisonDartTrap.class);
		classConversions.put(GnollRockfallTrap.class,          RockfallTrap.class);

		classConversions.put(DwarfKing.DKGhoul.class,          Ghoul.class);
		classConversions.put(DwarfKing.DKWarlock.class,        Warlock.class);
		classConversions.put(DwarfKing.DKMonk.class,           Monk.class);
		classConversions.put(DwarfKing.DKGolem.class,          Golem.class);

		classConversions.put(YogDzewa.YogRipper.class,         RipperDemon.class);
		classConversions.put(YogDzewa.YogEye.class,            Eye.class);
		classConversions.put(YogDzewa.YogScorpio.class,        Scorpio.class);
	}

	public static boolean isSeen(Class<?> cls){
		for (Bestiary cat : values()) {
			if (cat.seen.containsKey(cls)) {
				return cat.seen.get(cls);
			}
		}
		return false;
	}

	public static void setSeen(Class<?> cls){
		if (classConversions.containsKey(cls)){
			cls = classConversions.get(cls);
		}
		for (Bestiary cat : values()) {
			if (cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
				cat.seen.put(cls, true);
				Journal.saveNeeded = true;
			}
		}
		Badges.validateCatalogBadges();
	}

	public static long encounterCount(Class<?> cls) {
		for (Bestiary cat : values()) {
			if (cat.encounterCount.containsKey(cls)) {
				return cat.encounterCount.get(cls);
			}
		}
		return 0;
	}

	//used primarily when bosses are killed and need to clean up their minions
	public static boolean skipCountingEncounters = false;

	public static void countEncounter(Class<?> cls){
		countEncounters(cls, 1);
	}

	public static void countEncounters(Class<?> cls, long encounters){
		if (skipCountingEncounters){
			return;
		}
		if (classConversions.containsKey(cls)){
			cls = classConversions.get(cls);
		}
		for (Bestiary cat : values()) {
			if (cat.encounterCount.containsKey(cls) && cat.encounterCount.get(cls) != Integer.MAX_VALUE){
				cat.encounterCount.put(cls, cat.encounterCount.get(cls)+encounters);
				if (cat.encounterCount.get(cls) < -1_000_000_000){ //to catch cases of overflow
					cat.encounterCount.put(cls, 2_000_000_000_000L);
				}
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String BESTIARY_CLASSES    = "bestiary_classes";
	private static final String BESTIARY_SEEN       = "bestiary_seen";
	private static final String BESTIARY_ENCOUNTERS = "bestiary_encounters";

	public static void store( Bundle bundle ){

		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seen = new ArrayList<>();
		ArrayList<Long> encounters = new ArrayList<>();

		for (Bestiary cat : values()) {
			for (Class<?> entity : cat.entities()) {
				if (cat.seen.get(entity) || cat.encounterCount.get(entity) > 0){
					classes.add(entity);
					seen.add(cat.seen.get(entity));
					encounters.add(cat.encounterCount.get(entity));
				}
			}
		}

		Class<?>[] storeCls = new Class[classes.size()];
		boolean[] storeSeen = new boolean[seen.size()];
		long[] storeEncounters = new long[encounters.size()];

		for (int i = 0; i < storeCls.length; i++){
			storeCls[i] = classes.get(i);
			storeSeen[i] = seen.get(i);
			storeEncounters[i] = encounters.get(i);
		}

		bundle.put( BESTIARY_CLASSES, storeCls );
		bundle.put( BESTIARY_SEEN, storeSeen );
		bundle.put( BESTIARY_ENCOUNTERS, storeEncounters );

	}

	public static void restore( Bundle bundle ){

		if (bundle.contains(BESTIARY_CLASSES)
				&& bundle.contains(BESTIARY_SEEN)
				&& bundle.contains(BESTIARY_ENCOUNTERS)){
			Class<?>[] classes = bundle.getClassArray(BESTIARY_CLASSES);
			boolean[] seen = bundle.getBooleanArray(BESTIARY_SEEN);
			long[] encounters = bundle.getLongArray(BESTIARY_ENCOUNTERS);

			for (int i = 0; i < classes.length; i++){
				for (Bestiary cat : values()){
					if (cat.seen.containsKey(classes[i])){
						cat.seen.put(classes[i], seen[i]);
						cat.encounterCount.put(classes[i], encounters[i]);
					}
				}
			}
		}

	}

}
