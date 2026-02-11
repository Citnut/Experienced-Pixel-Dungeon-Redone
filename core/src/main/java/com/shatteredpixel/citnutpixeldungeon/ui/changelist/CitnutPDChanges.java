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

import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.citnutpixeldungeon.ui.Icons;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;

import java.util.ArrayList;

public class CitnutPDChanges {

	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ) {
		add_2026_02(changeInfos);
		add_2025_x(changeInfos);
		add_template(changeInfos);
	}

	private static void add_2026_02( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("CitnutPD-2026.02", true, "");
		changes.hardlight(0x66CCFF);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "Mod Features",
				"Added/expanded CitnutPD mod-facing systems:\n" +
						"_-_ dedicated mod UI flow (mod list/detail windows)\n" +
						"_-_ baseline hooks for mod package loading and registration\n" +
						"_-_ initial documentation/templates for mod authors"));

		changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "ScrollOfDebug",
				"ScrollOfDebug maintenance and feature work:\n" +
						"_-_ compatibility updates for newer engine/runtime APIs\n" +
						"_-_ command help/output formatting cleanup\n" +
						"_-_ package/member discovery quality improvements"));

		changes.addButton(new ChangeButton(Icons.get(Icons.CHECKED), "Debug Item Browser",
				"Refined ScrollOfDebug browser workflow and UX:\n" +
						"_-_ debug-browser-command: browser moved to `browser` command; reading scroll now opens command console\n" +
						"_-_ debug-browser-groups: category-based menu + per-modpack menu for enabled mods with items\n" +
						"_-_ debug-browser-layout: item grid now relayouts in content-local coordinates and stays bound to window pane\n" +
						"_-_ debug-browser-search-input: fixed visible typed text on white field and preserved focus while filtering\n" +
						"_-_ debug-browser-tests: expanded unit coverage for command/grouping/filter/layout math"));

		changes.addButton(new ChangeButton(Icons.get(Icons.LANGS), "Translations",
				"Translation pass and message maintenance:\n" +
						"_-_ updated many journal/window message files\n" +
						"_-_ synced strings after upstream merges\n" +
						"_-_ where translation is missing, text intentionally stays in English"));

		changes.addButton(new ChangeButton(Icons.get(Icons.NEWS), "Upstream Sync",
				"Changelog scope synced to upstream ShPD v3.3.5.\n" +
						"ShPD tab now includes detailed 3.X patchline notes."));
	}

	private static void add_2025_x( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("CitnutPD-2025.x", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
				"This section is reserved for your custom gameplay/balance changelog entries.\n" +
						"If a note is still incomplete, keep it in English for now."));
	}

	private static void add_template( ArrayList<ChangeInfo> changeInfos ) {
		ChangeInfo changes = new ChangeInfo("How to Add Entries", false, null);
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);

		changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Editing Guide",
				"Edit: core/src/main/java/com/shatteredpixel/citnutpixeldungeon/ui/changelist/CitnutPDChanges.java\n" +
						"\n" +
						"Suggested format:\n" +
						"- Create a new major ChangeInfo with your version/date.\n" +
						"- Add grouped buttons: New, Changes, Buffs/Nerfs, Bugfixes.\n" +
						"- Keep each entry short and searchable."));
	}
}
