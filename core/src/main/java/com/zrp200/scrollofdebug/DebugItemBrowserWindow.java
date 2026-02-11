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

package com.zrp200.scrollofdebug;

import com.badlogic.gdx.utils.Align;
import com.shatteredpixel.citnutpixeldungeon.Chrome;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Gold;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.mod.ModItemDef;
import com.shatteredpixel.citnutpixeldungeon.mod.ModManager;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.ui.IconButton;
import com.shatteredpixel.citnutpixeldungeon.ui.Icons;
import com.shatteredpixel.citnutpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.citnutpixeldungeon.ui.RedButton;
import com.shatteredpixel.citnutpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.citnutpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DebugItemBrowserWindow extends Window {

	public interface BrowserItemSource {
		Item createInstance();
		String displayName();
		String description();
		String sourceId();
		String menuKey();
		boolean isModItem();
	}

	private interface PreviewBackedSource extends BrowserItemSource {
		Item previewItem();
	}

	private static final String TITLE = "Debug Item Browser";
	private static final String SEARCH_MENU_KEY = "search";
	private static final String SEARCH_MENU_LABEL = "Search";
	private static final Generator.Category[] CATEGORY_ORDER = {
			Generator.Category.TRINKET,
			Generator.Category.WEAPON,
			Generator.Category.ARMOR,
			Generator.Category.MISSILE,
			Generator.Category.WAND,
			Generator.Category.RING,
			Generator.Category.ARTIFACT,
			Generator.Category.FOOD,
			Generator.Category.POTION,
			Generator.Category.SEED,
			Generator.Category.SCROLL,
			Generator.Category.STONE,
			Generator.Category.GOLD
	};

	private static final int MIN_WIDTH = 176;
	private static final int MAX_WIDTH = 250;
	private static final int MIN_HEIGHT = 145;

	private static final int INPUT_HEIGHT = 16;
	private static final int BUTTON_HEIGHT = 16;
	private static final int MENU_BUTTON_SIZE = 18;
	private static final int SLOT_SIZE = 18;
	private static final int SLOT_GAP = 1;
	private static final int GRID_PADDING = 1;
	private static final float PADDING = 1f;

	private final ScrollOfDebug owner;
	private final String initialMenuSelection;

	private RenderedTextBlock title;
	private BrowserSearchInput searchInput;
	private RenderedTextBlock resultText;
	private RenderedTextBlock menuInfoText;

	private final Component menuContent = new Component();
	private final ScrollPane menuPane = new ScrollPane(menuContent);
	private final Component itemContent = new Component();
	private final ScrollPane itemPane = new ScrollPane(itemContent);

	private final ArrayList<MenuDefinition> menus = new ArrayList<>();
	private final ArrayList<MenuButton> menuButtons = new ArrayList<>();
	private final ArrayList<BrowserItemSlot> allSlots = new ArrayList<>();
	private final Map<String, BrowserItemSlot> slotsBySourceId = new LinkedHashMap<>();
	private final ArrayList<BrowserItemSource> visibleSources = new ArrayList<>();

	private MenuDefinition selectedMenu;
	private BrowserItemSlot selectedSlot;
	private int gridColumns = 1;
	private boolean uiReady;

	public DebugItemBrowserWindow(ScrollOfDebug owner) {
		this(owner, null);
	}

	public DebugItemBrowserWindow(ScrollOfDebug owner, String initialMenuSelection) {
		super();
		this.owner = owner;
		this.initialMenuSelection = normalizeMenuSelection(initialMenuSelection);

		int width = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, (int) (PixelScene.uiCamera.width * 0.72f)));
		int height = Math.max(MIN_HEIGHT, (int) (PixelScene.uiCamera.height * 0.76f));
		height = Math.min(height, (int) (PixelScene.uiCamera.height * 0.88f));
		resize(width, height);

		createUi();
		buildSources();
		buildMenuButtons();
		selectInitialMenu();

		uiReady = true;
		relayout();
		applyFilter(false);
		if (isSearchMenu(selectedMenu)) {
			searchInput.requestFocus();
		}
	}

	@Override
	public void resize(int w, int h) {
		super.resize(w, h);
		if (uiReady) {
			relayout();
			applyFilter(false);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		if (uiReady) {
			relayout();
			applyFilter(false);
		}
	}

	private void createUi() {
		title = PixelScene.renderTextBlock(TITLE, 9);
		title.hardlight(TITLE_COLOR);
		add(title);

		searchInput = new BrowserSearchInput() {
			@Override
			public void onChanged() {
				applyFilter(true);
			}
		};
		searchInput.setAlignment(Align.left);
		searchInput.setTextColor(0x000000);
		searchInput.setText("");
		add(searchInput);

		resultText = PixelScene.renderTextBlock("", 6);
		add(resultText);

		menuInfoText = PixelScene.renderTextBlock("", 6);
		menuInfoText.align(RenderedTextBlock.RIGHT_ALIGN);
		add(menuInfoText);

		add(menuPane);
		add(itemPane);

		RedButton commandButton = new RedButton("Commands") {
			@Override
			protected void onPointerDown() {
				super.onPointerDown();
				PointerEvent.clearKeyboardThisPress = false;
			}

			@Override
			protected void onPointerUp() {
				super.onPointerUp();
				PointerEvent.clearKeyboardThisPress = false;
			}

			@Override
			protected void onClick() {
				hide();
				if (owner != null) {
					owner.openCommandConsole();
				}
			}
		};
		commandButton.setRect(0, 0, 0, BUTTON_HEIGHT);
		add(commandButton);

		RedButton closeButton = new RedButton("Close") {
			@Override
			protected void onPointerDown() {
				super.onPointerDown();
				PointerEvent.clearKeyboardThisPress = false;
			}

			@Override
			protected void onPointerUp() {
				super.onPointerUp();
				PointerEvent.clearKeyboardThisPress = false;
			}

			@Override
			protected void onClick() {
				hide();
			}
		};
		closeButton.setRect(0, 0, 0, BUTTON_HEIGHT);
		add(closeButton);
	}

	private void buildSources() {
		LinkedHashMap<String, MenuDefinition> menuMap = new LinkedHashMap<>();
		for (Generator.Category category : CATEGORY_ORDER) {
			String key = categoryMenuKey(category);
			menuMap.put(key, new MenuDefinition(key, categoryLabel(category), iconForCategory(category)));
		}

		for (BrowserItemSource source : buildVanillaSourcesFromClasses(discoverItemClasses(ScrollOfDebug.trie))) {
			MenuDefinition menu = menuMap.get(source.menuKey());
			if (menu != null) {
				menu.sources.add(source);
			}
		}

		for (ModManager.ModInfo info : ModManager.listMods()) {
			if (info == null || !info.enabled) continue;
			ModManager.ModData data = ModManager.loadModData(info);
			if (data == null || data.items == null || data.items.isEmpty()) continue;

			ArrayList<BrowserItemSource> modSources = new ArrayList<>();
			for (ModItemDef def : data.items) {
				BrowserItemSource source = createModSource(info.id, def);
				if (source != null) {
					modSources.add(source);
				}
			}
			if (!modSources.isEmpty()) {
				String modKey = modMenuKey(info.id);
				MenuDefinition modMenu = new MenuDefinition(modKey,
						(info.name == null || info.name.trim().isEmpty()) ? info.id : info.name.trim(),
						Icons.BACKPACK);
				modMenu.sources.addAll(modSources);
				menuMap.put(modKey, modMenu);
			}
		}

		for (MenuDefinition menu : menuMap.values()) {
			menu.sources.sort((a, b) -> {
				int byName = a.displayName().compareToIgnoreCase(b.displayName());
				return byName != 0 ? byName : a.sourceId().compareToIgnoreCase(b.sourceId());
			});
		}

		ArrayList<BrowserItemSource> searchSources = new ArrayList<>();
		for (MenuDefinition menu : menuMap.values()) {
			searchSources.addAll(menu.sources);
		}
		searchSources.sort((a, b) -> {
			int byName = a.displayName().compareToIgnoreCase(b.displayName());
			return byName != 0 ? byName : a.sourceId().compareToIgnoreCase(b.sourceId());
		});
		if (!searchSources.isEmpty()) {
			MenuDefinition searchMenu = new MenuDefinition(SEARCH_MENU_KEY, SEARCH_MENU_LABEL, Icons.MAGNIFY);
			searchMenu.sources.addAll(searchSources);
			menus.add(searchMenu);
		}

		for (MenuDefinition menu : menuMap.values()) {
			menus.add(menu);
		}

		for (MenuDefinition menu : menus) {
			for (BrowserItemSource source : menu.sources) {
				if (slotsBySourceId.containsKey(source.sourceId())) continue;
				BrowserItemSlot slot = new BrowserItemSlot(source);
				slotsBySourceId.put(source.sourceId(), slot);
				allSlots.add(slot);
				itemContent.add(slot);
			}
		}
	}

	private void buildMenuButtons() {
		menuContent.clear();
		menuButtons.clear();

		float x = 0f;
		for (MenuDefinition menu : menus) {
			MenuButton button = new MenuButton(menu);
			button.setRect(x, 0, MENU_BUTTON_SIZE, MENU_BUTTON_SIZE);
			x += MENU_BUTTON_SIZE + 1;
			menuButtons.add(button);
			menuContent.add(button);
		}
		menuContent.setSize(Math.max(menuPane.width(), x), MENU_BUTTON_SIZE + 1);
	}

	private void selectInitialMenu() {
		if (menus.isEmpty()) return;
		if (initialMenuSelection == null) {
			setSelectedMenu(menus.get(0));
			return;
		}
		for (MenuDefinition menu : menus) {
			if (menu.key.equals(initialMenuSelection)) {
				setSelectedMenu(menu);
				return;
			}
		}
		setSelectedMenu(menus.get(0));
	}

	private void setSelectedMenu(MenuDefinition menu) {
		if (menu == null || menu == selectedMenu) return;
		selectedMenu = menu;
		if (selectedSlot != null) {
			selectedSlot.setSelected(false);
			selectedSlot = null;
		}
		for (MenuButton button : menuButtons) {
			button.setSelected(button.menu == selectedMenu);
		}
		relayout();
		applyFilter(false);
		itemPane.scrollTo(0, 0);
		if (isSearchMenu(selectedMenu)) {
			searchInput.requestFocus();
		}
	}

	private void relayout() {
		float w = width;
		float h = height;
		float y = 2f;
		boolean showSearchInput = isSearchMenu(selectedMenu);

		title.setPos((w - title.width()) * 0.5f, y);
		PixelScene.align(title);
		y = title.bottom() + 2f;

		searchInput.visible = showSearchInput;
		searchInput.active = showSearchInput;
		if (showSearchInput) {
			searchInput.setRect(PADDING, y, w - PADDING * 2, INPUT_HEIGHT);
			y = searchInput.bottom() + 1f;
		}

		float infoWidth = (w - PADDING * 2) * 0.5f;
		resultText.maxWidth((int) infoWidth);
		resultText.setPos(PADDING, y);
		menuInfoText.maxWidth((int) infoWidth);
		menuInfoText.setPos(w - PADDING - menuInfoText.width(), y);
		float infoBottom = Math.max(resultText.bottom(), menuInfoText.bottom());
		y = infoBottom + 1f;

		menuPane.setRect(PADDING, y, w - PADDING * 2, MENU_BUTTON_SIZE + 2);
		y = menuPane.bottom() + 1f;

		float buttonTop = h - BUTTON_HEIGHT - PADDING;
		float paneHeight = Math.max(SLOT_SIZE + GRID_PADDING * 2 + 1, buttonTop - y - 1f);
		itemPane.setRect(PADDING, y, w - PADDING * 2, paneHeight);

		float buttonWidth = (w - PADDING * 2 - 1) * 0.5f;
		float buttonsY = h - BUTTON_HEIGHT - PADDING;
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) instanceof RedButton) {
				RedButton button = (RedButton) members.get(i);
				if ("Commands".equals(button.text())) {
					button.setRect(PADDING, buttonsY, buttonWidth, BUTTON_HEIGHT);
				} else if ("Close".equals(button.text())) {
					button.setRect(PADDING + buttonWidth + 1, buttonsY, buttonWidth, BUTTON_HEIGHT);
				}
			}
		}

		layoutMenuButtons();
	}

	private void layoutMenuButtons() {
		float x = 0f;
		for (MenuButton button : menuButtons) {
			button.setRect(x, 0, MENU_BUTTON_SIZE, MENU_BUTTON_SIZE);
			x += MENU_BUTTON_SIZE + 1;
		}
		menuContent.setSize(Math.max(menuPane.width(), x), MENU_BUTTON_SIZE + 1);
	}

	private void applyFilter(boolean keepFocus) {
		if (selectedMenu == null) return;

		String normalizedQuery = normalizeQuery(searchInput.getText());
		visibleSources.clear();
		if (isSearchMenu(selectedMenu)) {
			visibleSources.addAll(filterSources(selectedMenu.sources, normalizedQuery));
		} else {
			visibleSources.addAll(selectedMenu.sources);
		}

		gridColumns = Math.max(1,
				(int) ((itemPane.width() - GRID_PADDING * 2 + SLOT_GAP) / (SLOT_SIZE + SLOT_GAP)));

		for (BrowserItemSlot slot : allSlots) {
			slot.visible = false;
			slot.active = false;
		}

		int visibleCount = 0;
		for (BrowserItemSource source : visibleSources) {
			BrowserItemSlot slot = slotsBySourceId.get(source.sourceId());
			if (slot == null) continue;
			slot.visible = true;
			slot.active = true;
			int col = visibleCount % gridColumns;
			int row = visibleCount / gridColumns;
			float x = GRID_PADDING + col * (SLOT_SIZE + SLOT_GAP);
			float y = GRID_PADDING + row * (SLOT_SIZE + SLOT_GAP);
			slot.setRect(x, y, SLOT_SIZE, SLOT_SIZE);
			visibleCount++;
		}

		float contentHeight = Math.max(
				itemPane.height(),
				contentHeightForItemCount(visibleCount, gridColumns, SLOT_SIZE, SLOT_GAP, GRID_PADDING, GRID_PADDING));
		itemContent.setSize(itemPane.width(), contentHeight);

		resultText.text(visibleCount + " / " + selectedMenu.sources.size() + " items");
		menuInfoText.text(selectedMenu.label);
		menuInfoText.setPos(width - PADDING - menuInfoText.width(), resultText.top());

		if (selectedSlot != null && !selectedSlot.visible) {
			selectedSlot.setSelected(false);
			selectedSlot = null;
		}
		if (keepFocus && isSearchMenu(selectedMenu)) {
			searchInput.requestFocus();
		}
	}

	private void onEntrySelected(BrowserItemSlot slot) {
		if (slot == null) return;
		if (selectedSlot != null && selectedSlot != slot) {
			selectedSlot.setSelected(false);
		}
		selectedSlot = slot;
		slot.setSelected(true);
		GameScene.show(new DebugItemDetailWindow(slot.source));
	}

	static List<Class<? extends Item>> discoverItemClasses(PackageTrie trie) {
		ArrayList<Class<? extends Item>> classes = new ArrayList<>();
		if (trie == null) return classes;

		LinkedHashSet<Class<? extends Item>> unique = new LinkedHashSet<>();
		for (Class<?> cls : trie.getAllClasses()) {
			if (!Item.class.isAssignableFrom(cls) || !ScrollOfDebug.canInstantiate(cls)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Class<? extends Item> itemClass = (Class<? extends Item>) cls;
			if (categoryForClass(itemClass) != null) {
				unique.add(itemClass);
			}
		}

		classes.addAll(unique);
		classes.sort((a, b) -> {
			int bySimpleName = a.getSimpleName().compareToIgnoreCase(b.getSimpleName());
			return bySimpleName != 0 ? bySimpleName : a.getName().compareTo(b.getName());
		});
		return classes;
	}

	static List<BrowserItemSource> buildVanillaSourcesFromClasses(Collection<Class<? extends Item>> itemClasses) {
		ArrayList<BrowserItemSource> sources = new ArrayList<>();
		if (itemClasses == null) return sources;
		for (Class<? extends Item> itemClass : itemClasses) {
			Generator.Category category = categoryForClass(itemClass);
			if (category == null) continue;
			VanillaItemSource source = new VanillaItemSource(itemClass, category);
			if (source.previewItem() != null) {
				sources.add(source);
			}
		}
		return sources;
	}

	static List<BrowserItemSource> filterSources(Collection<? extends BrowserItemSource> sources, String query) {
		ArrayList<BrowserItemSource> filtered = new ArrayList<>();
		if (sources == null) return filtered;
		String normalized = normalizeQuery(query);
		for (BrowserItemSource source : sources) {
			if (matchesQuery(source, normalized)) {
				filtered.add(source);
			}
		}
		return filtered;
	}

	static int rowsForItemCount(int itemCount, int columns) {
		if (itemCount <= 0) return 0;
		if (columns <= 0) return itemCount;
		return (itemCount + columns - 1) / columns;
	}

	static float contentHeightForItemCount(
			int itemCount,
			int columns,
			int slotSize,
			int slotGap,
			int topPadding,
			int bottomPadding) {
		int rows = rowsForItemCount(itemCount, columns);
		if (rows == 0) return topPadding + bottomPadding;
		return topPadding + bottomPadding + rows * slotSize + Math.max(0, rows - 1) * slotGap;
	}

	static boolean needsScroll(
			int itemCount,
			int columns,
			int slotSize,
			int slotGap,
			int topPadding,
			int bottomPadding,
			float viewportHeight) {
		return contentHeightForItemCount(itemCount, columns, slotSize, slotGap, topPadding, bottomPadding) > viewportHeight;
	}

	static Generator.Category categoryForClass(Class<? extends Item> itemClass) {
		if (itemClass == null) return null;
		for (Generator.Category category : CATEGORY_ORDER) {
			if (category.superClass != null && category.superClass.isAssignableFrom(itemClass)) return category;
			if (category.classes == null) continue;
			for (Class<?> cls : category.classes)
				if (cls != null && cls.isAssignableFrom(itemClass)) return category;
		}
		return null;
	}

	static String categoryMenuKeyForClass(Class<? extends Item> itemClass) {
		Generator.Category category = categoryForClass(itemClass);
		return category == null ? null : categoryMenuKey(category);
	}

	static String modMenuKey(String modId) {
		String normalizedId = modId == null ? "" : modId.trim().toLowerCase(Locale.ROOT);
		return normalizedId.isEmpty() ? null : "mod:" + normalizedId;
	}

	static String normalizeMenuSelection(String raw) {
		if (raw == null) return null;
		String normalized = raw.trim();
		if (normalized.isEmpty()) return null;
		if (normalized.regionMatches(true, 0, "mod:", 0, 4)) {
			return modMenuKey(normalized.substring(4));
		}
		try {
			Generator.Category category = Generator.Category.valueOf(normalized.toUpperCase(Locale.ROOT));
			return categoryMenuKey(category);
		} catch (Exception ignored) {
			return normalized.toLowerCase(Locale.ROOT);
		}
	}

	private static String categoryMenuKey(Generator.Category category) {
		return category.name().toLowerCase(Locale.ROOT);
	}

	private static String categoryLabel(Generator.Category category) {
		String value = category.name().toLowerCase(Locale.ROOT);
		return Character.toUpperCase(value.charAt(0)) + value.substring(1);
	}

	private static Icons iconForCategory(Generator.Category category) {
		if (category == Generator.Category.SEED) return Icons.SEED_POUCH;
		if (category == Generator.Category.SCROLL) return Icons.SCROLL_HOLDER;
		if (category == Generator.Category.POTION) return Icons.POTION_BANDOLIER;
		if (category == Generator.Category.WAND
				|| category == Generator.Category.MISSILE
				|| category == Generator.Category.WEAPON) return Icons.WAND_HOLSTER;
		return Icons.BACKPACK;
	}

	private static BrowserItemSource createModSource(String modId, ModItemDef def) {
		if (def == null) return null;
		String key = modMenuKey(modId);
		if (key == null) return null;
		Item preview = safeModPreview(def);
		if (preview == null) return null;
		return new ModBrowserItemSource(def, key, preview);
	}

	private static String normalizeQuery(String query) {
		return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
	}

	private static boolean matchesQuery(BrowserItemSource source, String normalizedQuery) {
		if (source == null) return false;
		if (normalizedQuery == null || normalizedQuery.isEmpty()) return true;
		String searchText = source.displayName().toLowerCase(Locale.ROOT);
		return searchText.contains(normalizedQuery);
	}

	private static boolean isSearchMenu(MenuDefinition menu) {
		return menu != null && SEARCH_MENU_KEY.equals(menu.key);
	}

	private static String safeDisplayName(Item item, String fallback) {
		if (item != null) {
			try {
				String name = item.name();
				if (name != null && !name.isEmpty()) {
					return Messages.titleCase(name);
				}
			} catch (Exception ignored) {
				// fall through to fallback
			}
		}
		return fallback == null || fallback.isEmpty() ? "Unknown Item" : fallback;
	}

	private static String safeDescription(Item item, String fallback) {
		if (item != null) {
			try {
				String desc = item.desc();
				if (desc != null && !desc.isEmpty()) {
					return desc;
				}
			} catch (Exception ignored) {
				// fall through to fallback
			}
		}
		return fallback == null || fallback.isEmpty() ? "No description available." : fallback;
	}

	private static Item safeIdentify(Item item) {
		if (item == null) return null;
		try {
			item.identify();
			return item;
		} catch (Exception ignored) {
			return null;
		}
	}

	private static Item safeVanillaPreview(Class<? extends Item> itemClass) {
		if (itemClass == null) return null;
		try {
			return safeIdentify(Reflection.newInstance(itemClass));
		} catch (Exception ignored) {
			return null;
		}
	}

	private static Item safeModPreview(ModItemDef def) {
		if (def == null) return null;
		try {
			return safeIdentify(ModManager.createItemPreview(def));
		} catch (Exception ignored) {
			return null;
		}
	}

	private static Item initialPreviewFor(BrowserItemSource source) {
		if (source instanceof PreviewBackedSource) {
			Item preview = ((PreviewBackedSource) source).previewItem();
			if (preview != null) return preview;
		}
		Item fallback = source == null ? null : source.createInstance();
		if (fallback != null) return fallback;
		Item safeFallback = new Gold();
		safeFallback.identify();
		return safeFallback;
	}

	private static final class MenuDefinition {
		private final String key;
		private final String label;
		private final Icons icon;
		private final ArrayList<BrowserItemSource> sources = new ArrayList<>();

		private MenuDefinition(String key, String label, Icons icon) {
			this.key = key;
			this.label = label;
			this.icon = icon;
		}
	}

	private static final class VanillaItemSource implements PreviewBackedSource {
		private final Class<? extends Item> itemClass;
		private final String menuKey;
		private final String sourceId;
		private final String displayName;
		private final String description;
		private final Item previewItem;

		private VanillaItemSource(Class<? extends Item> itemClass, Generator.Category category) {
			this.itemClass = itemClass;
			this.menuKey = categoryMenuKey(category);
			this.sourceId = itemClass.getName();
			this.previewItem = safeVanillaPreview(itemClass);
			this.displayName = safeDisplayName(previewItem, itemClass.getSimpleName());
			this.description = safeDescription(previewItem, null);
		}

		@Override
		public Item createInstance() {
			return safeVanillaPreview(itemClass);
		}

		@Override
		public String displayName() {
			return displayName;
		}

		@Override
		public String description() {
			return description;
		}

		@Override
		public String sourceId() {
			return sourceId;
		}

		@Override
		public String menuKey() {
			return menuKey;
		}

		@Override
		public boolean isModItem() {
			return false;
		}

		@Override
		public Item previewItem() {
			return previewItem;
		}
	}

	private static final class ModBrowserItemSource implements PreviewBackedSource {
		private final ModItemDef def;
		private final String menuKey;
		private final String sourceId;
		private final String displayName;
		private final String description;
		private final Item previewItem;

		private ModBrowserItemSource(ModItemDef def, String menuKey, Item preview) {
			this.def = def;
			this.menuKey = menuKey;
			this.previewItem = preview;
			String modId = def.modId == null ? "mod" : def.modId;
			String itemId = def.id == null ? "item" : def.id;
			this.sourceId = modId + ":" + itemId;
			this.displayName = safeDisplayName(preview, def.name == null ? def.id : def.name);
			this.description = safeDescription(preview, def.desc);
		}

		@Override
		public Item createInstance() {
			return safeModPreview(def);
		}

		@Override
		public String displayName() {
			return displayName;
		}

		@Override
		public String description() {
			return description;
		}

		@Override
		public String sourceId() {
			return sourceId;
		}

		@Override
		public String menuKey() {
			return menuKey;
		}

		@Override
		public boolean isModItem() {
			return true;
		}

		@Override
		public Item previewItem() {
			return previewItem;
		}
	}

	private static class BrowserSearchInput extends TextInput {
		private BrowserSearchInput() {
			super(Chrome.get(Chrome.Type.TOAST_WHITE), false, (int) PixelScene.uiCamera.zoom * 9);
			setTextColor(0x000000);
		}

		@Override
		public void onChanged() {
			// hooked in outer window
		}
	}

	private final class MenuButton extends IconButton {
		private static final int HIGHLIGHT_COLOR = 0x66CCFF;
		private static final float SELECT_ALPHA = 0.30f;

		private final MenuDefinition menu;
		private ColorBlock highlight;
		private boolean selected;

		private MenuButton(MenuDefinition menu) {
			super();
			this.menu = menu;
			icon(Icons.get(menu.icon));
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			highlight = new ColorBlock(1, 1, HIGHLIGHT_COLOR);
			highlight.am = 0f;
			addToBack(highlight);
		}

		@Override
		protected void onPointerDown() {
			super.onPointerDown();
			PointerEvent.clearKeyboardThisPress = false;
		}

		@Override
		protected void onPointerUp() {
			super.onPointerUp();
			PointerEvent.clearKeyboardThisPress = false;
		}

		@Override
		protected void onClick() {
			setSelectedMenu(menu);
		}

		@Override
		protected void layout() {
			super.layout();
			if (highlight != null) {
				highlight.x = x;
				highlight.y = y;
				highlight.size(width, height);
				highlight.am = selected ? SELECT_ALPHA : 0f;
			}
		}

		@Override
		protected String hoverText() {
			return menu.label;
		}

		private void setSelected(boolean selected) {
			this.selected = selected;
			layout();
		}
	}

	private final class BrowserItemSlot extends ItemSlot {
		private static final int HOVER_COLOR = 0x66CCFF;
		private static final float HOVER_ALPHA = 0.18f;
		private static final float SELECT_ALPHA = 0.35f;

		private final BrowserItemSource source;
		private ColorBlock highlight;
		private boolean selected;

		private BrowserItemSlot(BrowserItemSource source) {
			super(initialPreviewFor(source));
			this.source = source;
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			highlight = new ColorBlock(1, 1, HOVER_COLOR);
			highlight.am = 0f;
			addToBack(highlight);
		}

		@Override
		protected void layout() {
			super.layout();
			if (highlight != null) {
				highlight.x = x;
				highlight.y = y;
				highlight.size(width, height);
			}
		}

		@Override
		public synchronized void update() {
			super.update();
			boolean hovered = hoverTip != null;
			if (highlight != null) {
				highlight.am = selected ? SELECT_ALPHA : (hovered ? HOVER_ALPHA : 0f);
			}
		}

		@Override
		protected void onClick() {
			onEntrySelected(this);
		}

		@Override
		protected String hoverText() {
			return source.displayName();
		}

		private void setSelected(boolean selected) {
			this.selected = selected;
		}
	}
}
