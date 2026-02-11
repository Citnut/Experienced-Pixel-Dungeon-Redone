package com.shatteredpixel.citnutpixeldungeon.items;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.shatteredpixel.citnutpixeldungeon.messages.Languages;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.watabou.utils.Reflection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemSanityTest {

    private static HeadlessApplication app;

    @BeforeClass
    public static void setupGdx() {
        if (Gdx.app == null) {
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            app = new HeadlessApplication(new ApplicationAdapter() {}, config);
        }
        Gdx.app.setLogLevel(Application.LOG_NONE);
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
    public void generatorItemsInstantiateAndDescribe() {
        List<Class<?>> all = new ArrayList<>();
        for (Generator.Category cat : Generator.Category.values()) {
            Class<?>[] classes = cat.classes;
            if (classes == null || classes.length == 0) {
                continue;
            }
            Set<Class<?>> seenInCategory = new HashSet<>();
            for (Class<?> cls : classes) {
                Assert.assertNotNull("Null class in category " + cat, cls);
                Assert.assertTrue("Duplicate class in " + cat + ": " + cls.getName(), seenInCategory.add(cls));
                all.add(cls);
            }
        }

        for (Class<?> cls : all) {
            Assert.assertTrue("Not an Item: " + cls.getName(), Item.class.isAssignableFrom(cls));
            @SuppressWarnings("unchecked")
            Class<? extends Item> itemClass = (Class<? extends Item>) cls;
            Item item = Reflection.newInstance(itemClass);
            Assert.assertNotNull("Failed to instantiate: " + cls.getName(), item);

            String name;
            String desc;
            try {
                name = item.name();
            } catch (Exception e) {
                throw new AssertionError("name() threw for " + cls.getName(), e);
            }
            try {
                desc = item.desc();
            } catch (Exception e) {
                throw new AssertionError("desc() threw for " + cls.getName(), e);
            }
            Assert.assertNotNull("Null name: " + cls.getName(), name);
            Assert.assertNotNull("Null desc: " + cls.getName(), desc);

            item.image();
            item.value();
        }
    }
}
