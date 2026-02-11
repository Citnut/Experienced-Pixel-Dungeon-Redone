package com.shatteredpixel.citnutpixeldungeon.mod;

import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class ModArmor extends Armor implements ModSpriteItem {
	private static final String MOD_ID = "mod_id";

	private String defId;
	private transient ModItemDef def;
	private transient boolean missing;

	public ModArmor() {
		super(1);
	}

	public ModArmor(ModItemDef def) {
		super(Math.max(1, def.tier));
		applyDef(def);
	}

	@Override
	public String name() {
		ModItemDef d = def();
		return d != null ? d.name : "Missing Mod Armor";
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
	public long STRReq(long lvl) {
		ModItemDef d = def();
		if (d != null && d.strReq >= 0) {
			return d.strReq;
		}
		return super.STRReq(lvl);
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
		this.stackable = false;
		this.tier = Math.max(1, def.tier);
		this.visibleTier = this.tier;
	}

	private ModItemDef def() {
		if (def == null && defId != null) {
			def = ModManager.getItem(defId);
			if (def != null) {
				applyDef(def);
			} else {
				missing = true;
				image = ItemSpriteSheet.SOMETHING;
				tier = 1;
				visibleTier = 1;
			}
		}
		return def;
	}
}
