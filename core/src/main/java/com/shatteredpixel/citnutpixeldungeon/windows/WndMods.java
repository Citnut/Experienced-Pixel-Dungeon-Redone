package com.shatteredpixel.citnutpixeldungeon.windows;

import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.mod.ModManager;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.ui.CheckBox;
import com.shatteredpixel.citnutpixeldungeon.ui.IconButton;
import com.shatteredpixel.citnutpixeldungeon.ui.Icons;
import com.shatteredpixel.citnutpixeldungeon.ui.RedButton;
import com.shatteredpixel.citnutpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.citnutpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class WndMods extends Window {

	private static final int WIDTH = Math.min(160, (int)(PixelScene.uiCamera.width * 0.9f));
	private static final int HEIGHT = Math.min(180, (int)(PixelScene.uiCamera.height * 0.9f));
	private static final int BTN_HEIGHT = 16;
	private static final int GAP = 2;

	private final ArrayList<ModEntry> entries = new ArrayList<>();

	public WndMods() {
		super();

		resize(WIDTH, HEIGHT);

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width()) / 2, GAP);
		PixelScene.align(title);
		add(title);

		ScrollPane pane = new ScrollPane(new Component());
		add(pane);
		float top = title.bottom() + GAP;
		float btnArea = BTN_HEIGHT * 2 + GAP;
		float bottom = HEIGHT - btnArea - GAP;
		pane.setRect(0, top, WIDTH, bottom - top);

		Component content = pane.content();
		float pos = GAP;

		ArrayList<ModManager.ModInfo> mods = ModManager.listMods();
		if (mods.isEmpty()) {
			RenderedTextBlock none = PixelScene.renderTextBlock(Messages.get(this, "none"), 7);
			none.setPos((WIDTH - none.width()) / 2, pos + 10);
			content.add(none);
			pos = none.bottom() + GAP;
		} else {
			for (ModManager.ModInfo info : mods) {
				ModEntry entry = new ModEntry(info);
				entry.setRect(0, pos, WIDTH, BTN_HEIGHT);
				content.add(entry);
				entries.add(entry);
				pos = entry.bottom() + GAP;

				RenderedTextBlock meta = PixelScene.renderTextBlock(info.meta(), 6);
				meta.setPos(GAP, pos);
				content.add(meta);
				pos = meta.bottom() + GAP;
			}
		}

		content.setSize(WIDTH, Math.max(pos, pane.height()));

		float btnWidth = (WIDTH - GAP) / 2f;
		float row1 = HEIGHT - (BTN_HEIGHT * 2 + GAP);
		float row2 = HEIGHT - BTN_HEIGHT;

		RedButton btnImport = new RedButton(Messages.get(this, "import")) {
			@Override
			protected void onClick() {
				super.onClick();
				promptImport();
			}
		};
		btnImport.setRect(0, row1, btnWidth, BTN_HEIGHT);
		add(btnImport);

		RedButton btnReload = new RedButton(Messages.get(this, "reload")) {
			@Override
			protected void onClick() {
				super.onClick();
				ModManager.reload();
			}
		};
		btnReload.setRect(btnImport.right() + GAP, row1, btnWidth, BTN_HEIGHT);
		add(btnReload);

		RedButton btnClear = new RedButton(Messages.get(this, "clear_cache")) {
			@Override
			protected void onClick() {
				super.onClick();
				ModManager.clearCache();
				ModManager.reload();
			}
		};
		btnClear.setRect(0, row2, btnWidth, BTN_HEIGHT);
		add(btnClear);

		RedButton btnClose = new RedButton(Messages.get(this, "close")) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
			}
		};
		btnClose.setRect(btnClear.right() + GAP, row2, btnWidth, BTN_HEIGHT);
		add(btnClose);

	}

	private class ModEntry extends Component {
		private final ModManager.ModInfo info;
		private final CheckBox toggle;
		private final IconButton details;

		public ModEntry(ModManager.ModInfo info) {
			this.info = info;
			toggle = new CheckBox(info.name) {
				@Override
				protected void onClick() {
					super.onClick();
					if (ModManager.setEnabled(info, checked())) {
						info.enabled = checked();
					}
				}
			};
			toggle.checked(info.enabled);
			add(toggle);

			details = new IconButton(Icons.get(Icons.INFO)) {
				@Override
				protected void onClick() {
					super.onClick();
					showDetails(info);
				}
			};
			add(details);
		}

		@Override
		protected void layout() {
			float btn = BTN_HEIGHT;
			float checkboxWidth = width - btn - GAP;
			toggle.setRect(x, y, checkboxWidth, BTN_HEIGHT);
			details.setRect(toggle.right() + GAP, y, btn, btn);
			height = BTN_HEIGHT;
		}
	}

	private void showDetails(ModManager.ModInfo info) {
		WndModDetails details = new WndModDetails(info, new Runnable() {
			@Override
			public void run() {
				hide();
			}
		});
		add(details);
	}

	private void promptImport() {
		if (Game.platform != null && Game.platform.hasFilePicker()) {
			Game.platform.selectFile(new String[]{"zip"}, path -> {
				if (path == null || path.trim().isEmpty()) return;
				boolean ok = ModManager.importPath(path.trim());
				if (ok) {
					GLog.p(Messages.get(WndMods.this, "import_success"));
					ModManager.reload();
					hide();
				} else {
					GLog.w(Messages.get(WndMods.this, "import_fail"));
				}
			});
			return;
		}

		String title = Messages.get(this, "import_title");
		String body = Messages.get(this, "import_body");
		WndTextInput input = new WndTextInput(title, body, "", 512, false,
				Messages.get(this, "import_ok"), Messages.get(this, "import_cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (!positive) return;
				if (text == null || text.trim().isEmpty()) return;
				boolean ok = ModManager.importPath(text.trim());
				if (ok) {
					GLog.p(Messages.get(WndMods.this, "import_success"));
					ModManager.reload();
					hide();
				} else {
					GLog.w(Messages.get(WndMods.this, "import_fail"));
				}
			}
		};
		add(input);
	}
}
