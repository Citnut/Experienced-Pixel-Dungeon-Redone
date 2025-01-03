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

package com.shatteredpixel.citnutpixeldungeon.items.scrolls;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ScrollOfRetribution extends Scroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_RETRIB;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		GameScene.flash( 0x80FFFFFF );
		
		//scales from 0x to 1x power, maxing at ~10% HP
		double hpPercent = (curUser.HT - curUser.HP)/(double)(curUser.HT);
		double power = Math.min( 4f, 4.45f*hpPercent);
		
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
		GLog.i(Messages.get(this, "blast"));

		ArrayList<Mob> targets = new ArrayList<>();

		//calculate targets first, in case damaging/blinding a target affects hero vision
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Dungeon.level.heroFOV[mob.pos]) {
				targets.add(mob);
			}
		}

		for (Mob mob : targets){
			//deals 10%HT, plus 0-90%HP based on scaling
			mob.damage(Math.round(mob.HT/10d + (mob.HP * power * 0.225d)), this);
			if (mob.isAlive()) {
				Buff.prolong(mob, Blindness.class, Blindness.DURATION);
			}
		}
		
		Buff.prolong(curUser, Weakness.class, Weakness.DURATION);
		Buff.prolong(curUser, Blindness.class, Blindness.DURATION);
		Dungeon.observe();

		identify();
		
		readAnimation();
		
	}
	
	@Override
	public long value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
