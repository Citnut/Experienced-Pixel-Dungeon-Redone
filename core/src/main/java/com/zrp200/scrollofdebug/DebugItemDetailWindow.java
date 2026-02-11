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
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.bags.Bag;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.citnutpixeldungeon.ui.RedButton;
import com.shatteredpixel.citnutpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.citnutpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

public class DebugItemDetailWindow extends Window {

	private static final int MIN_WIDTH = 160;
	private static final int MAX_WIDTH = 240;
	private static final int MIN_HEIGHT = 135;

	private static final int INPUT_HEIGHT = 16;
	private static final int BUTTON_HEIGHT = 16;
	private static final int MAX_NON_STACKABLE_BATCH = 999;

	private final DebugItemBrowserWindow.BrowserItemSource source;
	private final TextInput quantityInput;
	private final ScrollPane detailsPane;

	public DebugItemDetailWindow(Class<? extends Item> itemClass) {
		this(sourceFromClass(itemClass));
	}

	public DebugItemDetailWindow(DebugItemBrowserWindow.BrowserItemSource source) {
		super();

		this.source = source;
		Item preview = createItem(source);

		int width = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, (int) (PixelScene.uiCamera.width * 0.62f)));
		int height = Math.max(MIN_HEIGHT, (int) (PixelScene.uiCamera.height * 0.65f));
		height = Math.min(height, (int) (PixelScene.uiCamera.height * 0.85f));
		resize(width, height);

		float pos = 2f;

		ItemSlot icon = new ItemSlot(preview);
		icon.setRect(1, pos, 18, 18);
		add(icon);

		RenderedTextBlock name = PixelScene.renderTextBlock(
				source == null ? "Unknown Item" : source.displayName(), 8);
		name.hardlight(TITLE_COLOR);
		name.maxWidth(width - 22);
		name.setPos(icon.right() + 2, pos + Math.max(0f, (18 - name.height()) * 0.5f));
		PixelScene.align(name);
		add(name);

		pos = Math.max(icon.bottom(), name.bottom()) + 2f;

		float buttonTop = height - BUTTON_HEIGHT - 1;
		float inputTop = buttonTop - INPUT_HEIGHT - 2;

		RenderedTextBlock quantityLabel = PixelScene.renderTextBlock("Quantity", 6);
		quantityLabel.setPos(1, inputTop - quantityLabel.height() - 1);
		PixelScene.align(quantityLabel);
		add(quantityLabel);

		RenderedTextBlock detailsText = PixelScene.renderTextBlock(6);
		detailsText.text(buildDetailsText(preview, source), width - 3);
		detailsText.setPos(1, 1);
		Component content = new Component();
		content.add(detailsText);
		content.setSize(width, detailsText.bottom() + 1);

		detailsPane = new ScrollPane(content);
		add(detailsPane);
		float paneHeight = Math.max(12, quantityLabel.top() - pos - 1);
		detailsPane.setRect(0, pos, width, paneHeight);

		quantityInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false,
				(int) PixelScene.uiCamera.zoom * 9) {
			@Override
			public void enterPressed() {
				onTakeItem();
			}
		};
		quantityInput.setAlignment(Align.left);
		quantityInput.setTextColor(0x000000);
		quantityInput.setText("1");
		quantityInput.setMaxLength(12);
		add(quantityInput);
		quantityInput.setRect(1, inputTop, width - 2, INPUT_HEIGHT);

		float buttonWidth = (width - 3) * 0.5f;

		RedButton takeItemButton = new RedButton("Take Item") {
			@Override
			protected void onClick() {
				onTakeItem();
			}
		};
		takeItemButton.setRect(1, buttonTop, buttonWidth, BUTTON_HEIGHT);
		add(takeItemButton);

		RedButton closeButton = new RedButton("Close") {
			@Override
			protected void onClick() {
				hide();
			}
		};
		closeButton.setRect(takeItemButton.right() + 1, buttonTop, buttonWidth, BUTTON_HEIGHT);
		add(closeButton);
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		// TextInput and ScrollPane both use their own cameras; force a relayout when window offset changes.
		if (detailsPane != null) {
			detailsPane.setPos(detailsPane.left(), detailsPane.top());
		}
		if (quantityInput != null) {
			quantityInput.setPos(quantityInput.left(), quantityInput.top());
		}
	}

	private void onTakeItem() {
		long requestedQuantity = parseQuantity(quantityInput.getText());
		AcquireResult result = acquireToHero(source, requestedQuantity);
		if (result.success) {
			GLog.p(result.message);
		} else {
			GLog.w(result.message);
		}
	}

	private static String safeDisplayName(Item item, Class<? extends Item> cls) {
		if (item != null) {
			try {
				String name = item.name();
				if (name != null && !name.isEmpty()) {
					return Messages.titleCase(name);
				}
			} catch (Exception ignored) {
				// Keep fallback behavior below.
			}
		}
		return cls == null ? "Unknown Item" : cls.getSimpleName();
	}

	private static String safeDescription(Item item) {
		if (item != null) {
			try {
				String description = item.desc();
				if (description != null && !description.isEmpty()) {
					return description;
				}
			} catch (Exception ignored) {
				// Keep fallback behavior below.
			}
		}
		return "No description available.";
	}

	static String buildDetailsText(Item item) {
		return buildDetailsText(item, null);
	}

	static String buildDetailsText(Item item, DebugItemBrowserWindow.BrowserItemSource source) {
		if (item == null) {
			return "Unable to instantiate this item class.";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Name: ").append(source == null ? safeDisplayName(item, item.getClass()) : source.displayName());
		builder.append("\nType: ").append(item.getClass().getName());
		if (source != null) {
			builder.append("\nSource: ").append(source.sourceId());
		}
		builder.append("\nStackable: ").append(item.stackable ? "Yes" : "No");
		builder.append("\nUnique: ").append(item.unique ? "Yes" : "No");
		builder.append("\nCursed: ").append(item.cursed ? "Yes" : "No");
		builder.append("\nBase Quantity: ").append(item.quantity());
		builder.append("\nUpgrade Level: ").append(item.level());
		builder.append("\nValue: ").append(item.value());
		builder.append("\nEnergy Value: ").append(item.energyVal());
		builder.append("\n\nDescription:\n").append(safeDescription(item));
		return builder.toString();
	}

	static long parseQuantity(String text) {
		if (text == null || text.trim().isEmpty()) return 1;
		try {
			return Long.parseLong(text.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	static AcquireResult acquireToHero(Class<? extends Item> itemClass, long requestedQuantity) {
		return acquireToHero(sourceFromClass(itemClass), requestedQuantity);
	}

	static AcquireResult acquireToHero(DebugItemBrowserWindow.BrowserItemSource source, long requestedQuantity) {
		if (Dungeon.hero == null || Dungeon.hero.belongings == null || Dungeon.hero.belongings.backpack == null) {
			return AcquireResult.failure(requestedQuantity, "No active hero inventory was found.");
		}
		return acquireToBag(Dungeon.hero.belongings.backpack, source, requestedQuantity);
	}

	static AcquireResult acquireToBag(Bag bag, Class<? extends Item> itemClass, long requestedQuantity) {
		return acquireToBag(bag, sourceFromClass(itemClass), requestedQuantity);
	}

	static AcquireResult acquireToBag(Bag bag, DebugItemBrowserWindow.BrowserItemSource source, long requestedQuantity) {
		if (bag == null) {
			return AcquireResult.failure(requestedQuantity, "No inventory container was supplied.");
		}
		if (source == null) {
			return AcquireResult.failure(requestedQuantity, "No item type selected.");
		}
		if (requestedQuantity < 1) {
			return AcquireResult.failure(requestedQuantity, "Quantity must be at least 1.");
		}

		Item sample = createItem(source);
		if (sample == null) {
			return AcquireResult.failure(requestedQuantity, "Failed to instantiate " + source.displayName() + ".");
		}

		if (sample.stackable) {
			long grantedQuantity = clampRequestedQuantity(sample, requestedQuantity);
			if (grantedQuantity < 1) {
				return AcquireResult.failure(requestedQuantity, "This item cannot be created with that quantity.");
			}

			if (!sample.collect(bag)) {
				return AcquireResult.failure(requestedQuantity, "Inventory is full. Could not add " + source.displayName() + ".");
			}

			String message = "Added " + source.displayName() + " x" + grantedQuantity + ".";
			if (requestedQuantity != grantedQuantity) {
				message += " (requested x" + requestedQuantity + ", clamped by stack limits)";
			}
			return AcquireResult.success(requestedQuantity, grantedQuantity, message);
		}

		long maxToCreate = Math.min(requestedQuantity, MAX_NON_STACKABLE_BATCH);
		long created = 0;
		for (long i = 0; i < maxToCreate; i++) {
			Item item = createItem(source);
			if (item == null || !item.collect(bag)) {
				break;
			}
			created++;
		}

		if (created < 1) {
			return AcquireResult.failure(requestedQuantity, "Inventory is full. Could not add " + source.displayName() + ".");
		}

		String message = "Added " + source.displayName() + " x" + created + ".";
		if (requestedQuantity > MAX_NON_STACKABLE_BATCH) {
			message += " (batch capped at " + MAX_NON_STACKABLE_BATCH + " per action)";
		}
		return AcquireResult.success(requestedQuantity, created, message);
	}

	private static Item createItem(DebugItemBrowserWindow.BrowserItemSource source) {
		if (source == null) return null;
		Item item;
		try {
			item = source.createInstance();
		} catch (Exception ignored) {
			return null;
		}
		if (item != null) {
			try {
				item.identify();
			} catch (Exception ignored) {
				return null;
			}
		}
		return item;
	}

	private static DebugItemBrowserWindow.BrowserItemSource sourceFromClass(final Class<? extends Item> itemClass) {
		if (itemClass == null) return null;
		return new DebugItemBrowserWindow.BrowserItemSource() {
			@Override
			public Item createInstance() {
				try {
					return Reflection.newInstance(itemClass);
				} catch (Exception ignored) {
					return null;
				}
			}

			@Override
			public String displayName() {
				Item preview = createInstance();
				return safeDisplayName(preview, itemClass);
			}

			@Override
			public String description() {
				Item preview = createInstance();
				return safeDescription(preview);
			}

			@Override
			public String sourceId() {
				return itemClass.getName();
			}

			@Override
			public String menuKey() {
				return DebugItemBrowserWindow.categoryMenuKeyForClass(itemClass);
			}

			@Override
			public boolean isModItem() {
				return false;
			}
		};
	}

	static long clampRequestedQuantity(Item item, long requestedQuantity) {
		item.quantity(requestedQuantity);
		return item.quantity();
	}

	public static class AcquireResult {
		public final boolean success;
		public final long requestedQuantity;
		public final long grantedQuantity;
		public final String message;

		private AcquireResult(boolean success, long requestedQuantity, long grantedQuantity, String message) {
			this.success = success;
			this.requestedQuantity = requestedQuantity;
			this.grantedQuantity = grantedQuantity;
			this.message = message;
		}

		private static AcquireResult success(long requestedQuantity, long grantedQuantity, String message) {
			return new AcquireResult(true, requestedQuantity, grantedQuantity, message);
		}

		private static AcquireResult failure(long requestedQuantity, String message) {
			return new AcquireResult(false, requestedQuantity, 0, message);
		}
	}
}
