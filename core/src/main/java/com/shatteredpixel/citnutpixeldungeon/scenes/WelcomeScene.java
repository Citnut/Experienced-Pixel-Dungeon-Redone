/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.scenes;

import com.shatteredpixel.citnutpixeldungeon.*;
import com.shatteredpixel.citnutpixeldungeon.journal.Document;
import com.shatteredpixel.citnutpixeldungeon.journal.Journal;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

import java.util.Collections;

public class WelcomeScene extends PixelScene {

	private static final int LATEST_UPDATE = ShatteredPixelDungeon.v2_5_0;

	//used so that the game does not keep showing the window forever if cleaning fails
	private static boolean triedCleaningTemp = false;

	@Override
	public void create() {
		super.create();

		final int previousVersion = SPDSettings.version();
			ShatteredPixelDungeon.switchNoFade(TitleScene.class);

	}

	private void updateVersion(int previousVersion){

		//update rankings, to update any data which may be outdated
		if (previousVersion < LATEST_UPDATE){

			Badges.loadGlobal();
			Journal.loadGlobal();

			if (previousVersion <= ShatteredPixelDungeon.v2_4_2){
				//Dwarf King's final journal entry changed, set it as un-read
				if (Document.HALLS_KING.isPageRead(Document.KING_ATTRITION)){
					Document.HALLS_KING.unreadPage(Document.KING_ATTRITION);
				}

				//don't victory nag people who have already gotten a win in older versions
				if (Badges.isUnlocked(Badges.Badge.VICTORY)){
					//TODO commented out for the beta as we want to test the window!
					//SPDSettings.victoryNagged(true);
				}
			}

			//pre-unlock Duelist for those who already have a win
			if (previousVersion <= ShatteredPixelDungeon.v2_0_2){
				if (Badges.isUnlocked(Badges.Badge.VICTORY) && !Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST)){
					Badges.unlock(Badges.Badge.UNLOCK_DUELIST);
				}
			}

			try {
				Rankings.INSTANCE.load();
				for (Rankings.Record rec : Rankings.INSTANCE.records.toArray(new Rankings.Record[0])){
					try {
						Rankings.INSTANCE.loadGameData(rec);
						Rankings.INSTANCE.saveGameData(rec);
					} catch (Exception e) {
						//if we encounter a fatal per-record error, then clear that record's data
						rec.gameData = null;
						Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
					}
				}
				if (Rankings.INSTANCE.latestDaily != null){
					try {
						Rankings.INSTANCE.loadGameData(Rankings.INSTANCE.latestDaily);
						Rankings.INSTANCE.saveGameData(Rankings.INSTANCE.latestDaily);
					} catch (Exception e) {
						//if we encounter a fatal per-record error, then clear that record's data
						Rankings.INSTANCE.latestDaily.gameData = null;
						Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
					}
				}
				Collections.sort(Rankings.INSTANCE.records, Rankings.scoreComparator);
				Rankings.INSTANCE.save();
			} catch (Exception e) {
				//if we encounter a fatal error, then just clear the rankings
				FileUtils.deleteFile( Rankings.RANKINGS_FILE );
				Game.reportException( new RuntimeException("Rankings Updating Failed!",e));
			}
			Dungeon.daily = Dungeon.dailyReplay = false;

			if (previousVersion <= ShatteredPixelDungeon.v2_3_2){
				Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_ALCHEMY);
			}

			Badges.saveGlobal(true);
			Journal.saveGlobal(true);

		}

		SPDSettings.version(ShatteredPixelDungeon.versionCode);
	}
	
}
