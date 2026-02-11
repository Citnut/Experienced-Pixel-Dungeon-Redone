package com.shatteredpixel.citnutpixeldungeon.mod;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;

public class ModItemDef {
	public enum ItemType {
		ITEM,
		MELEE_WEAPON,
		MISSILE_WEAPON,
		ARMOR
	}

	public String id;
	public String modId;
	public String name;
	public String desc;
	public Generator.Category category;
	public int sprite;
	public FileHandle spriteFile;
	public int spriteX;
	public int spriteY;
	public int spriteW;
	public int spriteH;
	public boolean stackable;
	public long value;
	public float spawnWeight = 1f;
	public float useTime = 1f;
	public long quantity = 1;
	public UseEffect use;

	public ItemType itemType = ItemType.ITEM;

	//weapon stats (for MELEE_WEAPON)
	public int tier = 1;
	public int internalTier = 1;
	public float accuracy = 1f;
	public float delay = 1f;
	public int reach = 1;
	public long minBase = 0;
	public long maxBase = 0;
	public long minScale = 0;
	public long maxScale = 0;
	public long strReq = -1;

	//missile weapon stats (for MISSILE_WEAPON)
	public float missileBaseUses = -1f;
	public float missileDurability = -1f;
	public boolean missileSticky = true;

	public static class UseEffect {
		public enum Type {
			HEAL,
			SATIETY,
			BUFF
		}

		public Type type;
		public float amount;
		public String buffClass;
		public float duration;
		public String message;
	}
}
