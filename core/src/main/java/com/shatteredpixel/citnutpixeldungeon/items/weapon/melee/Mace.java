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
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.citnutpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Mace extends MeleeWeapon {

	{
		image = ItemSpriteSheet.MACE;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;

		internalTier = tier = 3;
		ACC = 1.28f; //28% boost to accuracy
	}

	@Override
	public long max(long lvl) {
		return  5L*(tier()+1) +    //20 base, down from 24
				lvl*(tier()+2);   //+5
	}

    @Override
    public long proc(Char attacker, Char defender, long damage) {
	    if (attacker == Dungeon.hero && defender != null) {
			WandOfDisintegration wand = ((WandOfDisintegration)(new WandOfDisintegration().upgrade(Math.round(level()*Math.pow(3, Dungeon.cycle)))));
			curUser = Dungeon.hero;
	    	wand.fx(
	    		new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_SOLID),
				new Callback() {
					public void call() {
						wand.onZap(new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_SOLID));
						wand.wandUsed();
					}
				}

		);
	    }
        return super.proc(attacker, defender, damage);
    }
	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(4+1.5*lvl) damage, roughly +55% base dmg, +60% scaling
		long dmgBoost = augment.damageFactor(5 + Math.round(1.5d*buffedLvl()));
		Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 5 + Math.round(1.5f*buffedLvl()) : 5;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(long level){
		int dmgBoost = 5 + Math.round(1.5f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

	public static void heavyBlowAbility(Hero hero, Integer target, float dmgMulti, long dmgBoost, MeleeWeapon wep){
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(wep, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = wep;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(wep, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		//no bonus damage if attack isn't a surprise
		if (enemy instanceof Mob && !((Mob) enemy).surprisedBy(hero)){
			dmgMulti = Math.min(1, dmgMulti);
			dmgBoost = 0;
		}

		float finalDmgMulti = dmgMulti;
		long finalDmgBoost = dmgBoost;
		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				wep.beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				if (hero.attack(enemy, finalDmgMulti, finalDmgBoost, Char.INFINITE_ACCURACY)) {
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					if (enemy.isAlive()){
						Buff.affect(enemy, Daze.class, Daze.DURATION);
					} else {
						wep.onAbilityKill(hero, enemy);
					}
				}
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				wep.afterAbilityUsed(hero);
			}
		});
	}

}
