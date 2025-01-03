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

package com.shatteredpixel.citnutpixeldungeon.items.spells;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.citnutpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.citnutpixeldungeon.items.EquipableItem;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.citnutpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.citnutpixeldungeon.items.wands.Wand;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.citnutpixeldungeon.journal.Catalog;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class CurseInfusion extends InventorySpell {
	
	{
		image = ItemSpriteSheet.CURSE_INFUSE;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ((item instanceof EquipableItem && !(item instanceof MissileWeapon)) || item instanceof Wand);
	}

	@Override
	protected void onItemSelected(Item item) {
		
		CellEmitter.get(curUser.pos).burst(ShadowParticle.UP, 5);
		Sample.INSTANCE.play(Assets.Sounds.CURSED);
		
		item.cursed = true;
		if (item instanceof MeleeWeapon || item instanceof SpiritBow) {
			Weapon w = (Weapon) item;
			if (w.enchantment != null) {
				//if we are freshly applying curse infusion, don't replace an existing curse
				if (w.hasGoodEnchant() || w.curseInfusionBonus) {
					w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
				}
			} else {
				w.enchant(Weapon.Enchantment.randomCurse());
			}
			w.curseInfusionBonus = true;
			if (w instanceof MagesStaff){
				((MagesStaff) w).updateWand(true);
			}
		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			if (a.glyph != null){
				//if we are freshly applying curse infusion, don't replace an existing curse
				if (a.hasGoodGlyph() || a.curseInfusionBonus) {
					a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
				}
			} else {
				a.inscribe(Armor.Glyph.randomCurse());
			}
			a.curseInfusionBonus = true;
		} else if (item instanceof Wand){
			((Wand) item).curseInfusionBonus = true;
			((Wand) item).updateLevel();
		} else if (item instanceof RingOfMight){
			curUser.updateHT(false);
		}
		Badges.validateItemLevelAquired(item);
		updateQuickslot();
	}
	
	@Override
	public long value() {
		return (long)(60 * (quantity/(float)Recipe.OUT_QUANTITY));
	}

	@Override
	public long energyVal() {
		return (long)(12 * (quantity/(float)Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends com.shatteredpixel.citnutpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 4;

		{
			inputs =  new Class[]{ScrollOfRemoveCurse.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 6;
			
			output = CurseInfusion.class;
			outQuantity = OUT_QUANTITY;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(MetalShard.class);
			return super.brew(ingredients);
		}
	}
}
