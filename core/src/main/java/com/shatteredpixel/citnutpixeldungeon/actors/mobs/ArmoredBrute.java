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

package com.shatteredpixel.citnutpixeldungeon.actors.mobs;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.effects.FloatingText;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.citnutpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.citnutpixeldungeon.sprites.ShieldedSprite;
import com.watabou.utils.Random;

public class ArmoredBrute extends Brute {

	{
		spriteClass = ShieldedSprite.class;
		
		//see rollToDropLoot
		loot = Generator.Category.ARMOR;
		lootChance = 1f;
	}
	
	@Override
	public long cycledDrRoll() {
        switch (Dungeon.cycle){
            case 1: return Dungeon.NormalLongRange(30, 58);
            case 2: return Dungeon.NormalLongRange(160, 280);
            case 3: return Dungeon.NormalLongRange(570, 1000);
            case 4: return Dungeon.NormalLongRange(19000, 30000);
			case 5: return Dungeon.NormalLongRange(1250000, 1750000);
        }
		return Dungeon.NormalLongRange(6, 10);
	}
	
	@Override
	protected void triggerEnrage () {
		Buff.affect(this, ArmoredRage.class).setShield(HT/2 + 1);
		sprite.showStatusWithIcon( CharSprite.POSITIVE, Long.toString(HT/2 + 1), FloatingText.SHIELDING );
		if (Dungeon.level.heroFOV[pos]) {
			sprite.showStatus( CharSprite.WARNING, Messages.get(this, "enraged") );
		}
		spend( TICK );
		hasRaged = true;
	}
	
	@Override
	public Item createLoot() {
		if (Random.Int( 4 ) == 0) {
			return new PlateArmor().random();
		}
		return new ScaleArmor().random();
	}
	
	//similar to regular brute rate, but deteriorates much slower. 60 turns to death total.
	public static class ArmoredRage extends Brute.BruteRage {
		
		@Override
		public boolean act() {
			
			if (target.HP > 0){
				detach();
				return true;
			}
			
			absorbDamage( target.HT / 40 );
			
			if (shielding() <= 0){
				target.die(null);
			}
			
			spend( 3*TICK );
			
			return true;
		}
		
	}
}
