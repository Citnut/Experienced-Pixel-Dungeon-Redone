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

package com.shatteredpixel.citnutpixeldungeon.items.weapon.melee;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.effects.Lightning;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Gauntlet extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.GAUNTLETS;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;
		
		internalTier = tier = 5;
		DLY = 0.5f; //2x speed
	}
	
	@Override
	public long max(long lvl) {
		return  Math.round(3d*(tier()+1)) +     //18 base, down from 36
				lvl*Math.round(0.5d*(tier()+2));  //+3.5 per level, down from +7
	}

	private ArrayList<Char> affected = new ArrayList<>();

	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override
	public long proc(Char attacker, Char defender, long damage) {
		affected.clear();
		arcs.clear();

		Shocking.arc(attacker, defender, 3, affected, arcs);

		affected.remove(defender); //defender isn't hurt by lightning
		for (Char ch : affected) {
			if (ch.alignment != attacker.alignment) {
				ch.damage(Math.round(damage * 0.75f), Shocking.class);
			}
		}

		attacker.sprite.parent.addToFront( new Lightning( arcs, null ) );
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_bad_position"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);

				affected.clear();
				arcs.clear();

				Shocking.arc(hero, enemy, 10, affected, arcs);

				for (Char ch : affected) {
					if (ch.alignment != hero.alignment) {
						ch.damage(Math.round(damageRoll(hero) * 0.6f), Shocking.class);
						ch.spend(1.5f);
					}
				}

				hero.sprite.parent.addToFront(new Lightning(arcs, null));
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);

				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				if (!enemy.isAlive()){
					onAbilityKill(hero, enemy);
				}
				afterAbilityUsed(hero);
			}
		});
    }

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(Math.round(min()*0.6d)), augment.damageFactor(Math.round(max()*0.6d)));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(Math.round(min(0)*0.6d)), augment.damageFactor(Math.round(max(0)*0.6d)));
		}
	}

	public String upgradeAbilityStat(long level){
		return "+" + augment.damageFactor(4 + Math.round(0.75f*level));
	}

}
