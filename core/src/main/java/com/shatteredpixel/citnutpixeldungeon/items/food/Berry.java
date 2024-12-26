/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

public class Berry extends Food {

	{
		image = ItemSpriteSheet.RATION;
		energy = Hunger.HUNGRY/3f; //100 food value

		bones = false;
	}

	@Override
	protected float eatingTime(){
		if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
				|| Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
				|| Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
				|| Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
				|| Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		SeedCounter counter = Buff.count(hero, SeedCounter.class, 1);
		if (counter.count() >= 2){
			Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.SEED), hero.pos).sprite.drop();
			counter.detach();
		}
	}

	@Override
	public long value() {
		return 5 * quantity;
	}

	public static class SeedCounter extends CounterBuff{{revivePersists = true;}};
}