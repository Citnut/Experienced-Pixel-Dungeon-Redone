package com.zrp200.scrollofdebug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.shatteredpixel.citnutpixeldungeon.items.Dewdrop;
import com.shatteredpixel.citnutpixeldungeon.items.Gold;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.bags.Bag;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.citnutpixeldungeon.messages.Languages;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DebugItemBrowserFeatureTest {

	private static HeadlessApplication app;

	@BeforeClass
	public static void setupGdx() {
		if (Gdx.app == null) {
			HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
			app = new HeadlessApplication(new ApplicationAdapter() {}, config);
		}
		Gdx.app.setLogLevel(Application.LOG_NONE);
		Game.version = "TEST";
		Messages.setup(Languages.ENGLISH);
	}

	@AfterClass
	public static void teardownGdx() {
		if (app != null) {
			app.exit();
			app = null;
		}
	}

	@Test
	public void scrollReadWindowIsCommandInputAfterRefactor() {
		ScrollOfDebug scroll = new ScrollOfDebug();
		Assert.assertEquals(WndTextInput.class, scroll.readWindowClass());
	}

	@Test
	public void browserCommandParsesOptionalMenuTarget() {
		Assert.assertNull(ScrollOfDebug.parseBrowserInitialMenuKey(new String[]{"browser"}));
		Assert.assertEquals("weapon", ScrollOfDebug.parseBrowserInitialMenuKey(new String[]{"browser", "weapon"}));
		Assert.assertEquals("mod:testpack", ScrollOfDebug.parseBrowserInitialMenuKey(new String[]{"browser", "mod:testpack"}));
	}

	@Test
	public void vanillaGroupingMapsClassToCategoryMenuKey() {
		Assert.assertEquals("weapon", DebugItemBrowserWindow.categoryMenuKeyForClass(Dagger.class));
		Assert.assertEquals("gold", DebugItemBrowserWindow.categoryMenuKeyForClass(Gold.class));
	}

	@Test
	public void modGroupingBuildsExpectedMenuKey() {
		Assert.assertEquals("mod:example_mod", DebugItemBrowserWindow.modMenuKey("Example_Mod"));
	}

	@Test
	public void searchFilterIsCaseInsensitiveAndPartialWithinMenuList() {
		List<DebugItemBrowserWindow.BrowserItemSource> sources = Arrays.asList(
				new FakeSource("gold", "com.foo.Gold", Gold.class),
				new FakeSource("gold", "com.foo.Dewdrop", Dewdrop.class));

		List<DebugItemBrowserWindow.BrowserItemSource> filtered =
				DebugItemBrowserWindow.filterSources(sources, "gol");

		Assert.assertEquals(1, filtered.size());
		Assert.assertEquals("com.foo.Gold", filtered.get(0).sourceId());
	}

	@Test
	public void scrollMathReportsOverflowForLargeCollections() {
		Assert.assertTrue(DebugItemBrowserWindow.needsScroll(
				200, 6, 18, 1, 1, 1, 90));
		Assert.assertFalse(DebugItemBrowserWindow.needsScroll(
				6, 6, 18, 1, 1, 1, 90));
	}

	@Test
	public void detailTextContainsItemInfoAndType() {
		Item item = new Gold();
		item.identify();

		String detail = DebugItemDetailWindow.buildDetailsText(item);
		Assert.assertTrue(detail.contains(item.getClass().getName()));
		Assert.assertTrue(detail.contains("Description:"));
		Assert.assertTrue(detail.contains("Stackable:"));
	}

	@Test
	public void acquireAddsCorrectQuantitiesAndRespectsStackLimits() {
		Bag bag = new Bag();

		DebugItemDetailWindow.AcquireResult goldResult =
				DebugItemDetailWindow.acquireToBag(bag, Gold.class, 25);
		Assert.assertTrue(goldResult.success);
		Assert.assertEquals(25, goldResult.grantedQuantity);
		Assert.assertEquals(25, findFirst(bag, Gold.class).quantity());

		DebugItemDetailWindow.AcquireResult dewResult =
				DebugItemDetailWindow.acquireToBag(bag, Dewdrop.class, 5);
		Assert.assertTrue(dewResult.success);
		Assert.assertEquals(1, dewResult.grantedQuantity);
		Assert.assertEquals(1, findFirst(bag, Dewdrop.class).quantity());
	}

	private static Item findFirst(Bag bag, Class<? extends Item> itemClass) {
		for (Item item : bag.items) {
			if (itemClass.isInstance(item)) {
				return item;
			}
		}
		Assert.fail("Missing item " + itemClass.getSimpleName());
		return null;
	}

	private static class FakeSource implements DebugItemBrowserWindow.BrowserItemSource {
		private final String menuKey;
		private final String sourceId;
		private final Class<? extends Item> cls;

		private FakeSource(String menuKey, String sourceId, Class<? extends Item> cls) {
			this.menuKey = menuKey;
			this.sourceId = sourceId;
			this.cls = cls;
		}

		@Override
		public Item createInstance() {
			Item item = com.watabou.utils.Reflection.newInstance(cls);
			if (item != null) item.identify();
			return item;
		}

		@Override
		public String displayName() {
			return sourceId;
		}

		@Override
		public String description() {
			return "";
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
	}
}
