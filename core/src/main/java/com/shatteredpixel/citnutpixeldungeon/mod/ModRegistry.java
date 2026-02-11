package com.shatteredpixel.citnutpixeldungeon.mod;

import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.watabou.utils.Reflection;

public class ModRegistry {
	private final String modId;

	public ModRegistry(String modId) {
		this.modId = modId;
	}

	public String modId() {
		return modId;
	}

	public ModItemDef newItem(String id, Generator.Category category) {
		ModItemDef def = new ModItemDef();
		def.modId = modId;
		def.id = id;
		def.category = category;
		return def;
	}

	public ModItemDef newMeleeWeapon(String id, Generator.Category category, int tier) {
		ModItemDef def = newItem(id, category);
		def.itemType = ModItemDef.ItemType.MELEE_WEAPON;
		def.tier = tier > 0 ? tier : 1;
		def.internalTier = def.tier;
		return def;
	}

	public ModItemDef newArmor(String id, Generator.Category category, int tier) {
		ModItemDef def = newItem(id, category);
		def.itemType = ModItemDef.ItemType.ARMOR;
		def.tier = tier > 0 ? tier : 1;
		def.internalTier = def.tier;
		return def;
	}

	public ModItemDef newMissileWeapon(String id, Generator.Category category, int tier) {
		ModItemDef def = newItem(id, category);
		def.itemType = ModItemDef.ItemType.MISSILE_WEAPON;
		def.tier = tier > 0 ? tier : 1;
		def.internalTier = def.tier;
		def.stackable = true;
		return def;
	}

	public boolean registerFactory(String id, Generator.Category category, float weight, ModItemFactory factory) {
		if (id == null || id.trim().isEmpty()) return false;
		String finalId = id.contains(":") ? id : modId + ":" + id;
		return ModManager.registerFactory(finalId, category, weight, factory);
	}

	public boolean registerClass(String id, Generator.Category category, float weight, Class<? extends Item> cls) {
		if (cls == null) return false;
		return registerFactory(id, category, weight, () -> Reflection.newInstance(cls));
	}

	public boolean register(ModItemDef def) {
		if (def == null) return false;
		if (def.modId == null) def.modId = modId;
		return ModManager.registerItem(def);
	}

	public int registerAll(ModItemDef... defs) {
		if (defs == null) return 0;
		int count = 0;
		for (ModItemDef def : defs) {
			if (register(def)) {
				count++;
			}
		}
		return count;
	}
}
