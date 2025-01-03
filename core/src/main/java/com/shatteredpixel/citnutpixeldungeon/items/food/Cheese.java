/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.items.food;

import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.*;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.Heap;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;

public class Cheese extends Food {
	
	{
		image = ItemSpriteSheet.CHEESE;
		energy = Hunger.STARVING*10f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy( hero );
		Buff.affect(hero, WellFed.class).reset();
		Buff.affect(hero, Invisibility.class, Invisibility.DURATION * 0.66f);
		Buff.affect(hero, Haste.class, Haste.DURATION * 0.66f);
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION * 0.66f);
		Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION * 0.66f);
		Buff.affect(hero, MindVision.class, MindVision.DURATION * 0.66f);
		Buff.affect(hero, Bless.class, Bless.DURATION * 0.66f);
		Buff.affect(hero, AdrenalineSurge.class).reset(2, 666);
		Buff.affect(hero, Adrenaline.class, Adrenaline.DURATION * 0.66f);
		Buff.affect(hero, BlobImmunity.class, BlobImmunity.DURATION * 0.66f);
		Buff.affect(hero, Recharging.class, Recharging.DURATION * 0.66f);
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION * 0.66f);
		for (Heap h : Dungeon.level.heaps.valueList()){
			if (h.type == Heap.Type.HEAP) {
				Item item = h.peek();
				if (item.doPickUp(hero, h.pos)) {
					h.pickUp();
					hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
					GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );
				} else {
					GLog.w(Messages.get(this, "cant_grab"));
					h.sprite.drop();
				}
			}
		}
	}
	
	@Override
	public long value() {
		return 40 * quantity;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos, float time) {
		if (super.doPickUp(hero, pos, time)){
			Badges.validateCheese();
			return true;
		}
		return false;
	}
}
