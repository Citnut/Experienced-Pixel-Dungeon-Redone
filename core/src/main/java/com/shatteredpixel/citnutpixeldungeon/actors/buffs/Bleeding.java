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

package com.shatteredpixel.citnutpixeldungeon.actors.buffs;

import com.shatteredpixel.citnutpixeldungeon.Badges;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.effects.Splash;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.citnutpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

public class Bleeding extends Buff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	protected double level;

	//used in specific cases where the source of the bleed is important for death logic
	private Class source;

	public double level(){
		return level;
	}
	
	private static final String LEVEL	= "level";
	private static final String SOURCE	= "source";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		bundle.put( SOURCE, source );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getDouble( LEVEL );
		source = bundle.getClass( SOURCE );
	}
	
	public void set( double level ) {
		set( level, null );
	}

	public void set( double level, Class source ){
		if (this.level < level) {
			this.level = Math.max(this.level, level);
			this.source = source;
		}
	}
	
	@Override
	public int icon() {
		return BuffIndicator.BLEEDING;
	}

	@Override
	public String iconTextDisplay() {
		return Long.toString(Math.round(level));
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			level = Dungeon.NormalDouble(level / 2f, level);
			long dmg = Math.round(level);
			
			if (dmg > 0) {
				
				target.damage( dmg, this );
				if (target.sprite.visible) {
					Splash.at( target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
							target.sprite.blood(), (int) Math.min(Integer.MAX_VALUE, Math.min( 10 * dmg / target.HT, 10 )));
				}
				
				if (target == Dungeon.hero && !target.isAlive()) {
					if (source == Chasm.class){
						Badges.validateDeathFromFalling();
					} else if (source == Sacrificial.class){
						Badges.validateDeathFromFriendlyMagic();
					}
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "ondeath") );
				}

				if (source == Sickle.HarvestBleedTracker.class && !target.isAlive()){
					MeleeWeapon.onAbilityKill(Dungeon.hero, target);
				}
				
				spend( TICK );
			} else {
				detach();
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.round(level));
	}
}
