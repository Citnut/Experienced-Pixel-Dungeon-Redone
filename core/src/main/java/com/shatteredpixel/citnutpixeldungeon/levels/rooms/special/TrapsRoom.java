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

package com.shatteredpixel.citnutpixeldungeon.levels.rooms.special;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Heap;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.citnutpixeldungeon.levels.Level;
import com.shatteredpixel.citnutpixeldungeon.levels.Terrain;
import com.shatteredpixel.citnutpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.citnutpixeldungeon.levels.traps.*;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class TrapsRoom extends SpecialRoom {

	//size is a bit limited to prevent too many or too few traps
	@Override
	public int minWidth() { return 6; }
	public int maxWidth() { return 8; }

	@Override
	public int minHeight() { return 6; }
	public int maxHeight() { return 8; }

	public void paint( Level level ) {
		 
		Painter.fill( level, this, Terrain.WALL );

		Class<? extends Trap> trapClass;
		switch (Random.Int(4)){
			case 0:
				trapClass = null;
				break;
			default:
				trapClass = Random.oneOf(levelTraps[Dungeon.depth/5]);
				break;
		}

		if (trapClass == null){
			Painter.fill(level, this, 1, Terrain.CHASM);
		} else {
			Painter.fill(level, this, 1, Terrain.TRAP);
		}
		
		Door door = entrance();
		door.set( Door.Type.REGULAR );
		
		int lastRow = level.map[left + 1 + (top + 1) * level.width()] == Terrain.CHASM ? Terrain.CHASM : Terrain.EMPTY;

		int x = -1;
		int y = -1;
		if (door.x == left) {
			x = right - 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.x == right) {
			x = left + 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.y == top) {
			x = left + width() / 2;
			y = bottom - 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		} else if (door.y == bottom) {
			x = left + width() / 2;
			y = top + 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		}

		for(Point p : getPoints()) {
			int cell = level.pointToCell(p);
			if (level.map[cell] == Terrain.TRAP){
				level.setTrap(Reflection.newInstance(trapClass).reveal(), cell);
			}
		}
		
		int pos = x + y * level.width();
		if (Random.Int( 3 ) == 0) {
			if (lastRow == Terrain.CHASM) {
				Painter.set( level, pos, Terrain.EMPTY );
			}
			level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		} else {
			Painter.set( level, pos, Terrain.PEDESTAL );
			level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		}
		
		level.addItemToSpawn( new PotionOfLevitation() );
	}
	
	private static Item prize( Level level ) {

		Item prize;

		//67% chance for prize item
		if (Random.Int(3) != 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}
		
		//1 floor set higher in probability, never cursed
		if (Random.Int(2) == 0) {
			prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
			if (((Weapon)prize).hasCurseEnchant()){
				((Weapon) prize).enchant(null);
			}
		} else {
			prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
			if (((Armor)prize).hasCurseGlyph()){
				((Armor) prize).inscribe(null);
			}
		}
		prize.cursed = false;
		prize.cursedKnown = true;

		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}
		
		return prize;
	}

	@SuppressWarnings("unchecked")
	private static Class<?extends Trap>[][] levelTraps = new Class[][]{
			//sewers
			{GrippingTrap.class, TeleportationTrap.class, FlockTrap.class},
			//prison
			{PoisonDartTrap.class, GrippingTrap.class, ExplosiveTrap.class},
			//caves
			{PoisonDartTrap.class, FlashingTrap.class, ExplosiveTrap.class},
			//city
			{WarpingTrap.class, FlashingTrap.class, DisintegrationTrap.class},
			//halls, muahahahaha
			{GrimTrap.class}
	};
}