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

package com.shatteredpixel.citnutpixeldungeon;

import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.scenes.TitleScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

public class ShatteredPixelDungeon extends Game {

	//variable constants for specific older versions of shattered, used for data conversion
	public static final int v1_2_3 = 628; //v1.2.3 is kept for now, for old rankings score logic

	//savegames from versions older than v1.4.3 are no longer supported, and data from them is ignored
	public static final int v1_4_3 = 668;

	public static final int v2_0_2 = 700;
	public static final int v2_1_4 = 737; //iOS was 737, other platforms were 736
	public static final int v2_2_1 = 755; //iOS was 755 (also called v2.2.2), other platforms were 754
	public static final int v2_3_2 = 768;
	public static final int v2_4_2 = 782;
	public static final int v2_5_0 = 794;
	
	public ShatteredPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? TitleScene.class : sceneClass, platform );
		//pre-2.18.1
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.scrolls.exotic.ScrollOfMidas.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPolymorph" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.potions.exotic.PotionOfOverload.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfHolyFuror" );

		//pre-2.17
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.scrolls.exotic.ScrollOfDetermination.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfConfusion" );

		//pre-v2.14
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.ArenaLevel.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.OldCavesBossLevel" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.ArenaLevel.ArenaBuff.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.OldCavesBossLevel$ArenaBuff" );

		//pre-v2.5.0
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.actors.mobs.MobSpawner.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.Level$Respawner" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.actors.buffs.Invulnerability.class,
				"com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AnkhInvulnerability" );

		//pre-v2.4.0
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.potions.brews.UnstableBrew.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.spells.UnstableSpell.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.spells.ArcaneCatalyst" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.spells.FeatherFall" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall.FeatherBuff.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.spells.FeatherFall$FeatherBuff" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.potions.brews.AquaBrew.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast" );

		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.standard.entrance.EntranceRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.standard.exit.ExitRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom" );

		//pre-v2.3.0
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.items.bombs.Bomb.ConjuredBomb.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb$MagicalBomb" );

		//pre-v2.2.0
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.BlacksmithRoom.QuestEntrance.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BlacksmithRoom$QuestEntrance" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.BlacksmithRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BlacksmithRoom" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.MassGraveRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.MassGraveRoom.Bones.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom$Bones" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.RitualSiteRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.RitualSiteRoom.RitualMarker.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom$RitualMarker" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.citnutpixeldungeon.levels.rooms.quest.RotGardenRoom.class,
				"com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RotGardenRoom" );
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		SPDAction.loadBindings();
		
		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()*SPDSettings.musicVol()/100f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()*SPDSettings.SFXVol()/100f );

		Sample.INSTANCE.load( Assets.Sounds.all );
		
	}

	@Override
	public void finish() {
		if (!DeviceCompat.isiOS()) {
			super.finish();
		} else {
			//can't exit on iOS (Apple guidelines), so just go to title screen
			switchScene(TitleScene.class);
		}
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (width == 0 || height == 0){
			return;
		}

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			PixelScene.noFade = true;
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}
	
	@Override
	public void destroy(){
		super.destroy();
		GameScene.endActorThread();
	}
	
	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}
}