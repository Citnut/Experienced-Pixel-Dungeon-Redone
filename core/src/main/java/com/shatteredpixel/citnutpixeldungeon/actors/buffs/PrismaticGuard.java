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

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class PrismaticGuard extends Buff {
	
	{
		type = buffType.POSITIVE;
	}
	
	private double HP;
	
	@Override
	public boolean act() {
		
		Hero hero = (Hero)target;
		
		Mob closest = null;
		int v = hero.visibleEnemies();
		for (int i=0; i < v; i++) {
			Mob mob = hero.visibleEnemy( i );
			if ( mob.isAlive() && !mob.isInvulnerable(PrismaticImage.class)
					&& mob.state != mob.PASSIVE && mob.state != mob.WANDERING && mob.state != mob.SLEEPING && !hero.mindVisionEnemies.contains(mob)
					&& (closest == null || Dungeon.level.distance(hero.pos, mob.pos) < Dungeon.level.distance(hero.pos, closest.pos))) {
				closest = mob;
			}
		}
		
		if (closest != null && Dungeon.level.distance(hero.pos, closest.pos) < 5){
			//spawn guardian
			int bestPos = -1;
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
					if (bestPos == -1 || Dungeon.level.trueDistance(p, closest.pos) < Dungeon.level.trueDistance(bestPos, closest.pos)){
						bestPos = p;
					}
				}
			}
			if (bestPos != -1) {
				PrismaticImage pris = new PrismaticImage();
				pris.duplicate(hero, (long)Math.floor(HP) );
				pris.state = pris.HUNTING;
				GameScene.add(pris, 1);
				ScrollOfTeleportation.appear(pris, bestPos);
				
				detach();
			} else {
				spend( TICK );
			}
			
			
		} else {
			spend(TICK);
		}
		
		LockedFloor lock = target.buff(LockedFloor.class);
		if (HP < maxHP() && Regeneration.regenOn()){
			HP += 0.1f;
		}
		
		return true;
	}
	
	public void set( long HP ){
		this.HP = HP;
	}
	
	public long maxHP(){
		return maxHP((Hero)target);
	}
	
	public static long maxHP( Hero hero ){
		return 10 + (long)Math.floor(hero.lvl * 2.5d); //half of hero's HP
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 1f, 2f);
	}

	@Override
	public float iconFadePercent() {
		return (float) (1f - HP/(double) maxHP());
	}

	@Override
	public String iconTextDisplay() {
		return Long.toString((long) HP);
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", (long)HP, maxHP());
	}
	
	private static final String HEALTH = "hp";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HEALTH, HP);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		HP = bundle.getLong(HEALTH);
	}
}
