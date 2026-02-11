package com.shatteredpixel.citnutpixeldungeon.windows;

import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.mod.ModItemDef;
import com.shatteredpixel.citnutpixeldungeon.mod.ModManager;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.citnutpixeldungeon.ui.Icons;
import com.shatteredpixel.citnutpixeldungeon.ui.RedButton;
import com.shatteredpixel.citnutpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.citnutpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;
import java.util.Locale;


public class WndModDetails extends Window {

	private static final int WIDTH = Math.min(170, (int)(PixelScene.uiCamera.width * 0.95f));
	private static final int HEIGHT = Math.min(200, (int)(PixelScene.uiCamera.height * 0.95f));
	private static final int BTN_HEIGHT = 16;
	private static final int GAP = 2;

	public WndModDetails(ModManager.ModInfo info, Runnable onRemoved) {
		super();
		resize(WIDTH, HEIGHT);

		ModManager.ModData data = ModManager.loadModData(info);
		if (data == null || data.info == null) {
			RenderedTextBlock none = PixelScene.renderTextBlock(Messages.get(this, "missing"), 8);
			none.setPos((WIDTH - none.width()) / 2f, GAP);
			add(none);
			layoutButtons(onRemoved, info, null);
			return;
		}

		float pos = GAP;

		Image icon = null;
		if (data.icon != null) {
			icon = new Image(data.icon);
		}
		if (icon == null) {
			icon = Icons.get(Icons.INFO);
		}
		IconTitle title = new IconTitle(icon, data.info.name);
		title.setRect(0, pos, WIDTH, 0);
		add(title);
		pos = title.bottom() + GAP;

		RenderedTextBlock meta = PixelScene.renderTextBlock(metaText(data), 6);
		meta.setPos(0, pos);
		meta.maxWidth(WIDTH);
		add(meta);
		pos = meta.bottom() + GAP;

		if (data.description != null && !data.description.trim().isEmpty()) {
			RenderedTextBlock desc = PixelScene.renderTextBlock(data.description, 6);
			desc.maxWidth(WIDTH);
			desc.setPos(0, pos);
			add(desc);
			pos = desc.bottom() + GAP;
		}

		ScrollPane pane = new ScrollPane(new Component());
		add(pane);
		float bottom = HEIGHT - BTN_HEIGHT - GAP;
		pane.setRect(0, pos, WIDTH, bottom - pos);

		Component content = pane.content();
		float listPos = GAP;

		RenderedTextBlock itemsTitle = PixelScene.renderTextBlock(Messages.get(this, "items"), 7);
		itemsTitle.setPos(0, listPos);
		content.add(itemsTitle);
		listPos = itemsTitle.bottom() + GAP;

		if (data.items == null || data.items.isEmpty()) {
			RenderedTextBlock none = PixelScene.renderTextBlock(Messages.get(this, "no_items"), 6);
			none.setPos(0, listPos);
			content.add(none);
			listPos = none.bottom() + GAP;
		} else {
			for (ModItemDef def : data.items) {
				ModItemEntry entry = new ModItemEntry(def);
				entry.setRect(0, listPos, WIDTH, 0);
				content.add(entry);
				listPos = entry.bottom() + GAP;
			}
		}

		content.setSize(WIDTH, Math.max(listPos, pane.height()));
		layoutButtons(onRemoved, info, data);
	}

	private void layoutButtons(Runnable onRemoved, ModManager.ModInfo info, ModManager.ModData data) {
		float btnWidth = (WIDTH - 2) / 2f;

		RedButton btnRemove = new RedButton(Messages.get(this, "uninstall")) {
			@Override
			protected void onClick() {
				super.onClick();
				WndOptions confirm = new WndOptions(
						Messages.get(WndModDetails.this, "confirm_title"),
						Messages.get(WndModDetails.this, "confirm_body"),
						Messages.get(WndModDetails.this, "confirm_yes"),
						Messages.get(WndModDetails.this, "confirm_no")) {
					@Override
					protected void onSelect(int index) {
						if (index != 0) return;
						int result = ModManager.deleteMod(info);
						if (result == ModManager.DELETE_OK) {
							GLog.p(Messages.get(WndModDetails.this, "delete_ok"));
							ModManager.reload();
							if (onRemoved != null) onRemoved.run();
							hide();
						} else if (result == ModManager.DELETE_CACHE_ONLY) {
							GLog.w(Messages.get(WndModDetails.this, "delete_cache_only"));
							ModManager.reload();
							if (onRemoved != null) onRemoved.run();
							hide();
						} else {
							GLog.w(Messages.get(WndModDetails.this, "delete_fail"));
						}
					}
				};
				add(confirm);
			}
		};
		btnRemove.setRect(0, HEIGHT - BTN_HEIGHT, btnWidth, BTN_HEIGHT);
		add(btnRemove);

		RedButton btnClose = new RedButton(Messages.get(this, "close")) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
			}
		};
		btnClose.setRect(btnRemove.right() + 2, HEIGHT - BTN_HEIGHT, btnWidth, BTN_HEIGHT);
		add(btnClose);
	}

	private static String metaText(ModManager.ModData data) {
		StringBuilder sb = new StringBuilder();
		if (data.info != null) {
			sb.append(data.info.id);
			if (data.info.version != null && !data.info.version.isEmpty()) {
				sb.append("  v").append(data.info.version);
			}
		}
		if (data.author != null && !data.author.trim().isEmpty()) {
			sb.append("\n").append(Messages.get(WndModDetails.class, "author")).append(": ").append(data.author);
		}
		if (data.homepage != null && !data.homepage.trim().isEmpty()) {
			sb.append("\n").append(Messages.get(WndModDetails.class, "homepage")).append(": ").append(data.homepage);
		}
		return sb.toString();
	}

	private static class ModItemEntry extends Component {
		private static final float ICON_SIZE = 16f;

		private final ModItemDef def;
		private final ItemSprite icon;
		private final RenderedTextBlock name;
		private final RenderedTextBlock desc;
		private final RenderedTextBlock details;

		public ModItemEntry(ModItemDef def) {
			this.def = def;
			icon = new ItemSprite();
			Item item = ModManager.createItemPreview(def);
			if (item != null) {
				icon.view(item);
			}
			add(icon);

			name = PixelScene.renderTextBlock(def.name != null ? def.name : def.id, 7);
			add(name);

			desc = PixelScene.renderTextBlock(def.desc != null ? def.desc : "", 6);
			add(desc);

			details = PixelScene.renderTextBlock(buildDetails(def), 6);
			add(details);
		}

		@Override
		protected void layout() {
			float x = this.x;
			float y = this.y;

			icon.x = x;
			icon.y = y;
			float baseSize = Math.max(1f, Math.max(icon.width, icon.height));
			float scale = ICON_SIZE / baseSize;
			icon.scale.set(scale, scale);
			float textX = x + ICON_SIZE + GAP;
			float maxW = width - ICON_SIZE - GAP;

			name.maxWidth((int) maxW);
			name.setPos(textX, y);

			desc.maxWidth((int) maxW);
			desc.setPos(textX, name.bottom() + 1);

			details.maxWidth((int) maxW);
			details.setPos(textX, desc.bottom() + 1);

			height = Math.max(ICON_SIZE, details.bottom() - y);
		}

		private static String buildDetails(ModItemDef def) {
			ArrayList<String> lines = new ArrayList<>();

			StringBuilder main = new StringBuilder();
			appendField(main, Messages.get(WndModDetails.class, "type"), formatEnum(def.itemType));
			if (def.category != null) {
				appendField(main, Messages.get(WndModDetails.class, "category"), def.category.toString());
			}
			if (def.tier > 0 && def.itemType != ModItemDef.ItemType.ITEM) {
				appendField(main, Messages.get(WndModDetails.class, "tier"), String.valueOf(def.tier));
			}
			lines.add(main.toString());

			StringBuilder general = new StringBuilder();
			if (def.value > 0) {
				appendField(general, Messages.get(WndModDetails.class, "value"), String.valueOf(def.value));
			}
			appendField(general, Messages.get(WndModDetails.class, "stackable"), yesNo(def.stackable));
			if (def.stackable && def.quantity > 1) {
				appendField(general, Messages.get(WndModDetails.class, "quantity"), String.valueOf(def.quantity));
			}
			if (def.spawnWeight != 1f) {
				appendField(general, Messages.get(WndModDetails.class, "spawn_weight"), formatFloat(def.spawnWeight));
			}
			if (def.useTime != 1f) {
				appendField(general, Messages.get(WndModDetails.class, "use_time"), formatFloat(def.useTime));
			}
			if (general.length() > 0) {
				lines.add(general.toString());
			}

			if (def.itemType == ModItemDef.ItemType.MELEE_WEAPON || def.itemType == ModItemDef.ItemType.MISSILE_WEAPON) {
				StringBuilder weapon = new StringBuilder();
				if (def.minBase > 0 || def.maxBase > 0) {
					appendField(weapon, Messages.get(WndModDetails.class, "damage"), def.minBase + "-" + def.maxBase);
				}
				if (def.minScale != 0 || def.maxScale != 0) {
					appendField(weapon, Messages.get(WndModDetails.class, "scale"), def.minScale + "-" + def.maxScale);
				}
				if (def.strReq >= 0) {
					appendField(weapon, Messages.get(WndModDetails.class, "str"), String.valueOf(def.strReq));
				}
				if (weapon.length() > 0) {
					lines.add(weapon.toString());
				}

				StringBuilder stats = new StringBuilder();
				if (def.accuracy != 1f) {
					appendField(stats, Messages.get(WndModDetails.class, "accuracy"), formatFloat(def.accuracy));
				}
				if (def.delay != 1f) {
					appendField(stats, Messages.get(WndModDetails.class, "delay"), formatFloat(def.delay));
				}
				if (def.reach != 1) {
					appendField(stats, Messages.get(WndModDetails.class, "reach"), String.valueOf(def.reach));
				}
				if (stats.length() > 0) {
					lines.add(stats.toString());
				}

				if (def.itemType == ModItemDef.ItemType.MISSILE_WEAPON) {
					StringBuilder missile = new StringBuilder();
					if (def.missileBaseUses > 0) {
						appendField(missile, Messages.get(WndModDetails.class, "uses"), formatFloat(def.missileBaseUses));
					}
					if (def.missileDurability > 0) {
						appendField(missile, Messages.get(WndModDetails.class, "durability"), formatFloat(def.missileDurability));
					}
					appendField(missile, Messages.get(WndModDetails.class, "sticky"), yesNo(def.missileSticky));
					lines.add(missile.toString());
				}
			}

			if (def.itemType == ModItemDef.ItemType.ARMOR && def.strReq >= 0) {
				StringBuilder armor = new StringBuilder();
				appendField(armor, Messages.get(WndModDetails.class, "str"), String.valueOf(def.strReq));
				lines.add(armor.toString());
			}

			if (def.use != null && def.use.type != null) {
				StringBuilder use = new StringBuilder();
				String extra = "";
				switch (def.use.type) {
					case HEAL:
					case SATIETY:
						if (def.use.amount > 0f) {
							extra = " (" + formatFloat(def.use.amount) + ")";
						}
						break;
					case BUFF:
						if (def.use.buffClass != null && !def.use.buffClass.isEmpty()) {
							extra = " (" + def.use.buffClass + ")";
						}
						if (def.use.duration > 0f) {
							extra = extra.isEmpty() ? " (" + formatFloat(def.use.duration) + "s)" : extra + " " + formatFloat(def.use.duration) + "s";
						}
						break;
					default:
						break;
				}
				appendField(use, Messages.get(WndModDetails.class, "use"), def.use.type + extra);
				lines.add(use.toString());
			}

			return String.join("\n", lines);
		}

		private static void appendField(StringBuilder sb, String label, String value) {
			if (sb.length() > 0) sb.append(" | ");
			sb.append(label).append(": ").append(value);
		}

		private static String yesNo(boolean value) {
			return Messages.get(WndModDetails.class, value ? "yes" : "no");
		}

		private static String formatEnum(Enum<?> value) {
			String text = value.name().toLowerCase(Locale.US).replace('_', ' ');
			return Character.toUpperCase(text.charAt(0)) + text.substring(1);
		}

		private static String formatFloat(float value) {
			if (Math.abs(value - Math.round(value)) < 0.0001f) {
				return String.valueOf(Math.round(value));
			}
			String text = String.format(Locale.US, "%.2f", value);
			while (text.contains(".") && (text.endsWith("0") || text.endsWith("."))) {
				text = text.substring(0, text.length() - 1);
			}
			return text;
		}
	}
}
