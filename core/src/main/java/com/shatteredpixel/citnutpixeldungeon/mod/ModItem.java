package com.shatteredpixel.citnutpixeldungeon.mod;

import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class ModItem extends Item implements ModSpriteItem {
	public static final String AC_USE = "USE";
	private static final String MOD_ID = "mod_id";

	private String defId;
	private transient ModItemDef def;
	private transient boolean missing;

	public ModItem() {
	}

	public ModItem(ModItemDef def) {
		applyDef(def);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		ModItemDef d = def();
		if (d != null && d.use != null) {
			actions.add(AC_USE);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			use(hero);
			return;
		}
		super.execute(hero, action);
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_USE.equals(action)) {
			String text = Messages.get(Item.class, "ac_use");
			return Messages.NO_TEXT_FOUND.equals(text) ? "USE" : text;
		}
		return super.actionName(action, hero);
	}

	@Override
	public String name() {
		ModItemDef d = def();
		return d != null ? d.name : "Missing Mod Item";
	}

	@Override
	public String desc() {
		ModItemDef d = def();
		if (d != null) return d.desc;
		return missing ? "Missing mod item: " + defId : "";
	}

	@Override
	public int image() {
		ModItemDef d = def();
		return d != null ? d.sprite : ItemSpriteSheet.SOMETHING;
	}

	@Override
	public long value() {
		ModItemDef d = def();
		return d != null ? d.value : 0;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public ModItemDef modDef() {
		return def();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MOD_ID, defId);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		defId = bundle.getString(MOD_ID);
		def();
	}

	private void applyDef(ModItemDef def) {
		this.def = def;
		this.defId = def.id;
		this.image = def.sprite;
		this.stackable = def.stackable;
		if (def.stackable && def.quantity > 0) {
			quantity(def.quantity);
		}
		this.defaultAction = def.use != null ? AC_USE : null;
		this.levelKnown = true;
		this.cursedKnown = true;
	}

	private ModItemDef def() {
		if (def == null && defId != null) {
			def = ModManager.getItem(defId);
			if (def != null) {
				applyDef(def);
			} else {
				missing = true;
				image = ItemSpriteSheet.SOMETHING;
				defaultAction = null;
			}
		}
		return def;
	}

	private void use(Hero hero) {
		ModItemDef d = def();
		if (d == null || d.use == null) {
			return;
		}

		GameScene.cancel();
		curUser = hero;
		curItem = this;

		detach(hero.belongings.backpack);

		switch (d.use.type) {
			case HEAL:
				Healing healing = Buff.affect(hero, Healing.class);
				healing.setHeal((long) d.use.amount, 0f, (long) d.use.amount);
				break;
			case SATIETY:
				Buff.affect(hero, Hunger.class).satisfy(d.use.amount);
				break;
			case BUFF:
				applyBuff(hero, d.use);
				break;
			default:
				break;
		}

		if (d.use.message != null && !d.use.message.isEmpty()) {
			GLog.i(d.use.message);
		}

		hero.sprite.operate(hero.pos);
		hero.busy();
		hero.spend(d.useTime);
		Sample.INSTANCE.play(com.shatteredpixel.citnutpixeldungeon.Assets.Sounds.ITEM);
		Item.updateQuickslot();
	}

	private void applyBuff(Hero hero, ModItemDef.UseEffect effect) {
		if (effect.buffClass == null || effect.buffClass.isEmpty()) return;

		Class<?> cls = Reflection.forName(effect.buffClass);
		if (cls == null || !Buff.class.isAssignableFrom(cls)) return;

		@SuppressWarnings("unchecked")
		Class<? extends Buff> buffClass = (Class<? extends Buff>) cls;

		if (effect.duration > 0f && FlavourBuff.class.isAssignableFrom(buffClass)) {
			@SuppressWarnings("unchecked")
			Class<? extends FlavourBuff> flavour = (Class<? extends FlavourBuff>) buffClass;
			Buff.affect(hero, flavour, effect.duration);
		} else {
			Buff.affect(hero, buffClass);
		}
	}
}
