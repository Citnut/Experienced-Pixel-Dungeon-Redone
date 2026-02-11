/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.ui.changelist;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.citnutpixeldungeon.ui.Icons;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class v3_X_Changes {

	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ) {
		add_Coming_Soon(changeInfos);
		add_v3_3_Changes(changeInfos);
		add_v3_2_Changes(changeInfos);
		add_v3_1_Changes(changeInfos);
		add_v3_0_Changes(changeInfos);
	}

	public static void add_Coming_Soon( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("Coming Soon", true, "");
		changes.hardlight(0xCCCCCC);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Overview (updated)",
				"After v3.3.5, the next major ShPD update focuses on the Ambitious Imp quest overhaul.\n" +
						"The version label may be v3.4 or v4.0 depending on final scope.\n\n" +
						"This replaces older 'coming soon' notes from v2.x that predicted v3.0."));

		changes.addButton(new ChangeButton(Icons.get(Icons.STAIRS), "Quest Direction",
				"Primary direction is a vault-style infiltration flow:\n" +
						"_-_ iterative tester-area expansion\n" +
						"_-_ stealth-friendly enemy behavior improvements\n" +
						"_-_ new room patterns, hazards, and reward pacing"));

		changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"Patch-line maintenance continues in parallel:\n" +
						"_-_ balance follow-ups for new/changed items\n" +
						"_-_ UI and performance polish\n" +
						"_-_ routine bugfixes and translation refreshes"));
	}

	public static void add_v3_3_Changes( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("v3.3", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes = new ChangeInfo("v3.3.5", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.NEWS), "Vault Tester Iteration",
				"Latest 3.3 patchline keeps iterating on the vault tester area:\n" +
						"_-_ better enemy behavior tuning for stealth interactions\n" +
						"_-_ additional room scenarios\n" +
						"_-_ more stable descend/return behavior"));

		changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"3.3.5 includes smaller gameplay/system polish:\n" +
						"_-_ randomization/badge UX feedback improvements\n" +
						"_-_ AI edge-case handling tweaks\n" +
						"_-_ ongoing text and translation cleanup"));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
				Messages.get(ChangesScene.class, "bugfixes"),
				"3.3.5 bugfix focus:\n" +
						"_-_ rare freeze/crash conditions around tester-area transitions\n" +
						"_-_ interaction exploits and state desync issues\n" +
						"_-_ legacy issues carried from earlier 3.3 patches"));

		changes = new ChangeInfo("v3.3.4", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.STAIRS), "Levelgen Progress",
				"3.3.4 improved vault tester generation internals:\n" +
						"_-_ more flexible room builder base\n" +
						"_-_ better control over encounter pacing\n" +
						"_-_ groundwork for future hazard/treasure layouts"));

		changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"_-_ additional scene/window persistence polish\n" +
						"_-_ clarity improvements in item text\n" +
						"_-_ translation updates"));

		changes = new ChangeInfo("v3.3.3 & v3.3.2", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.WARNING), "Hotfix + Follow-up",
				"v3.3.3 shipped as a fast hotfix for a severe regression from 3.3.2.\n" +
						"v3.3.2 also continued tester-area iteration and balance tuning."));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
				Messages.get(ChangesScene.class, "bugfixes"),
				"Combined 3.3.2/3.3.3 fixes include:\n" +
						"_-_ resurrection/skeleton-key crash scenario\n" +
						"_-_ class state initialization issues\n" +
						"_-_ launcher/platform edge-case crash fixes\n" +
						"_-_ multiple rare runtime errors"));

		changes = new ChangeInfo("v3.3.1", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
				Messages.get(ChangesScene.class, "bugfixes"),
				"v3.3.1 focused on first-wave stabilization for 3.3.0:\n" +
						"_-_ key/artifact interaction fixes\n" +
						"_-_ tester-area entry safety checks\n" +
						"_-_ visual and textual corrections"));

		changes = new ChangeInfo("v3.3.0", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
				"Released as the next major line after v3.2.x.\n" +
						"3.3.0 establishes the imp-quest tester direction and receives rapid patch support."));

		changes.addButton(new ChangeButton(Icons.get(Icons.NEWS), "Major Additions",
				"v3.3.0 headline additions:\n" +
						"_-_ initial vault tester area for future imp quest overhaul\n" +
						"_-_ new equipment/content additions in the 3.3 line\n" +
						"_-_ randomization-oriented run options and progression hooks"));

		changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "changes"),
				"Core gameplay and UX adjustments:\n" +
						"_-_ challenge/balance tuning in early and mid game\n" +
						"_-_ multiple interface and feedback improvements\n" +
						"_-_ continued cleanup for edge-case mechanics"));
	}

	public static void add_v3_2_Changes( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("v3.2", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.NEWS), "Feature Highlights",
				"v3.2.x delivered a broad feature wave with new systems and content expansion,\n" +
						"followed by iterative patches through v3.2.5."));

		changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS), "Balance & Systems",
				"_-_ multiple class/item balance passes\n" +
						"_-_ progression consistency tuning\n" +
						"_-_ follow-up adjustments driven by patch feedback"));

		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
				Messages.get(ChangesScene.class, "bugfixes"),
				"3.2.x patch cycle addressed crashes, rules inconsistencies,\n" +
						"and several UI/state persistence issues."));
	}

	public static void add_v3_1_Changes( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("v3.1", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.NEWS), "Patchline Summary",
				"v3.1.x focused on post-major stabilization and gameplay tuning,\n" +
						"with content quality improvements and bugfix-heavy maintenance."));
	}

	public static void add_v3_0_Changes( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("v3.0", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Major Release Baseline",
				"v3.0 marks the new-generation ShPD baseline (cleric era),\n" +
						"with large-scale gameplay/content shifts carried forward into v3.1-v3.3."));
	}
}
