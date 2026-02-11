package com.shatteredpixel.citnutpixeldungeon.mod;

import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class ModMissileWeapon extends MissileWeapon implements ModSpriteItem {
	private static final String MOD_ID = "mod_id";

	private String defId;
	private transient ModItemDef def;
	private transient boolean missing;

	public ModMissileWeapon() {
		super();
	}

	public ModMissileWeapon(ModItemDef def) {
		applyDef(def);
	}

	@Override
	public String name() {
		ModItemDef d = def();
		return d != null ? d.name : "Missing Mod Missile";
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
	public long min(long lvl) {
		ModItemDef d = def();
		if (d != null && d.minBase > 0 && d.maxBase > 0) {
			long min = d.minBase + d.minScale * lvl;
			return Math.max(0, min);
		}
		return super.min(lvl);
	}

	@Override
	public long max(long lvl) {
		ModItemDef d = def();
		if (d != null && d.minBase > 0 && d.maxBase > 0) {
			long max = d.maxBase + d.maxScale * lvl;
			long min = d.minBase + d.minScale * lvl;
			return Math.max(min, max);
		}
		return super.max(lvl);
	}

	@Override
	public int STRReq(long lvl) {
		ModItemDef d = def();
		if (d != null && d.strReq >= 0) {
			return (int) d.strReq;
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
		this.stackable = def.stackable;
		this.tier = def.tier;
		this.internalTier = def.internalTier;
		this.ACC = def.accuracy;
		this.DLY = def.delay;
		this.RCH = def.reach;

		if (def.missileBaseUses > 0) {
			this.baseUses = def.missileBaseUses;
		}
		if (def.missileDurability > 0) {
			this.durability = def.missileDurability;
		}
		this.sticky = def.missileSticky;

		if (def.stackable && def.quantity > 0) {
			quantity(def.quantity);
		}
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
				internalTier = 1;
			}
		}
		return def;
	}
}
