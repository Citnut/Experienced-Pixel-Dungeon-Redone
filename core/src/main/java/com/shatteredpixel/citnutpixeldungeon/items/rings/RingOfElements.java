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

package com.shatteredpixel.citnutpixeldungeon.items.rings;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.citnutpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;
import java.util.HashSet;

public class RingOfElements extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ELEMENTS;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					new DecimalFormat("#.###").format(100f * (1f - (0.02f))));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (1f - (0.02f))));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.###").format(98f));
		}
	}

	public String upgradeStat1(long level){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (1f - (0.02f))) + "%";
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Resistance();
	}

	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( Burning.class );
		RESISTS.add( Chill.class );
		RESISTS.add( Frost.class );
		RESISTS.add( Ooze.class );
		RESISTS.add( Paralysis.class );
		RESISTS.add( Poison.class );
		RESISTS.add( Corrosion.class );

		RESISTS.add( ToxicGas.class );
		RESISTS.add( Electricity.class );

		RESISTS.addAll( AntiMagic.RESISTS );
	}
	
	public static float resist( Char target, Class effect ){
		
		for (Class c : RESISTS){
			if (c.isAssignableFrom(effect)){
                float multiplier = 1f;
                if (getBonus(target, Resistance.class) > 0) multiplier = 0.02f;
                return multiplier;
			}
		}
		
		return 1f;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public class Resistance extends RingBuff {
	
	}
}
