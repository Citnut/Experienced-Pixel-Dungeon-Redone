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

package com.shatteredpixel.citnutpixeldungeon.items.wands;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.Statistics;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.citnutpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.citnutpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.HashMap;

public class WandOfCorruption extends Wand {

	{
		image = ItemSpriteSheet.WAND_CORRUPTION;
	}
	
	//Note that some debuffs here have a 0% chance to be applied.
	// This is because the wand of corruption considers them to be a certain level of harmful
	// for the purposes of reducing resistance, but does not actually apply them itself
	
	private static final float MINOR_DEBUFF_WEAKEN = 1/4f;
	private static final HashMap<Class<? extends Buff>, Float> MINOR_DEBUFFS = new HashMap<>();
	static{
		MINOR_DEBUFFS.put(Weakness.class,       2f);
		MINOR_DEBUFFS.put(Vulnerable.class,     2f);
		MINOR_DEBUFFS.put(Cripple.class,        1f);
		MINOR_DEBUFFS.put(Blindness.class,      1f);
		MINOR_DEBUFFS.put(Terror.class,         1f);

		MINOR_DEBUFFS.put(Chill.class,          0f);
		MINOR_DEBUFFS.put(Ooze.class,           0f);
		MINOR_DEBUFFS.put(Roots.class,          0f);
		MINOR_DEBUFFS.put(Vertigo.class,        0f);
		MINOR_DEBUFFS.put(Drowsy.class,         0f);
		MINOR_DEBUFFS.put(Bleeding.class,       0f);
		MINOR_DEBUFFS.put(Burning.class,        0f);
		MINOR_DEBUFFS.put(Poison.class,         0f);
	}

	private static final float MAJOR_DEBUFF_WEAKEN = 1/2f;
	private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS = new HashMap<>();
	static{
		MAJOR_DEBUFFS.put(Amok.class,           3f);
		MAJOR_DEBUFFS.put(Slow.class,           2f);
		MAJOR_DEBUFFS.put(Hex.class,            2f);
		MAJOR_DEBUFFS.put(Paralysis.class,      1f);

		MAJOR_DEBUFFS.put(Daze.class,           0f);
		MAJOR_DEBUFFS.put(Dread.class,          0f);
		MAJOR_DEBUFFS.put(Charm.class,          0f);
		MAJOR_DEBUFFS.put(MagicalSleep.class,   0f);
		MAJOR_DEBUFFS.put(SoulMark.class,       0f);
		MAJOR_DEBUFFS.put(Corrosion.class,      0f);
		MAJOR_DEBUFFS.put(Frost.class,          0f);
		MAJOR_DEBUFFS.put(Doom.class,           0f);
	}
	
	@Override
	public void onZap(Ballistica bolt) {
		Char ch = Actor.findChar(bolt.collisionPos);

		if (ch != null){
			
			if (!(ch instanceof Mob)){
				return;
			}

			Mob enemy = (Mob) ch;

			if (enemy instanceof DwarfKing){
				Statistics.qualifiedForBossChallengeBadge = false;
			}

			double corruptingPower = (3 + buffedLvl()/3d)*(1+ Dungeon.hero.lvl/150f);
			
			//base enemy resistance is usually based on their exp, but in special cases it is based on other criteria
			double enemyResist;
			if (ch instanceof Mimic || ch instanceof Statue){
				enemyResist = 1 + Dungeon.escalatingDepth();
			} else if (ch instanceof Piranha || ch instanceof Bee) {
				enemyResist = 1 + Dungeon.escalatingDepth()/2f;
			} else if (ch instanceof Wraith) {
				//divide by 5 as wraiths are always at full HP and are therefore ~5x harder to corrupt
				enemyResist = (1f + Dungeon.scalingDepth()/4f) / 5f;
			} else if (ch instanceof Swarm){
				//child swarms don't give exp, so we force this here.
				enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
				if (enemyResist == 1) enemyResist = 1 + 3;
			} else {
				enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
			}
			
			//100% health: 5x resist   75%: 3.25x resist   50%: 2x resist   25%: 1.25x resist
			enemyResist *= 1 + 4*Math.pow(enemy.HP/(float)enemy.HT, 2);
			
			//debuffs placed on the enemy reduce their resistance
			for (Buff buff : enemy.buffs()){
				if (MAJOR_DEBUFFS.containsKey(buff.getClass()))         enemyResist *= (1f-MAJOR_DEBUFF_WEAKEN);
				else if (MINOR_DEBUFFS.containsKey(buff.getClass()))    enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
				else if (buff.type == Buff.buffType.NEGATIVE)           enemyResist *= (1f-MINOR_DEBUFF_WEAKEN);
			}
			
			//cannot re-corrupt or doom an enemy, so give them a major debuff instead
			if(enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null){
				corruptingPower = enemyResist - 0.001f;
			}
			
			if (corruptingPower > enemyResist){
				corruptEnemy( enemy );
			} else {
				float debuffChance = (float) (corruptingPower / enemyResist);
				if (Dungeon.Float() < debuffChance){
					debuffEnemy( enemy, MAJOR_DEBUFFS);
				} else {
					debuffEnemy( enemy, MINOR_DEBUFFS);
				}
			}

			wandProc(ch, chargesPerCast());
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, 0.8f * Dungeon.Float(0.87f, 1.15f) );
			
		} else {
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}
	
	private void debuffEnemy( Mob enemy, HashMap<Class<? extends Buff>, Float> category ){
		
		//do not consider buffs which are already assigned, or that the enemy is immune to.
		HashMap<Class<? extends Buff>, Float> debuffs = new HashMap<>(category);
		for (Buff existing : enemy.buffs()){
			if (debuffs.containsKey(existing.getClass())) {
				debuffs.put(existing.getClass(), 0f);
			}
		}
		for (Class<?extends Buff> toAssign : debuffs.keySet()){
			 if (debuffs.get(toAssign) > 0 && enemy.isImmune(toAssign)){
			 	debuffs.put(toAssign, 0f);
			 }
		}
		
		//all buffs with a > 0 chance are flavor buffs
		Class<?extends FlavourBuff> debuffCls = (Class<? extends FlavourBuff>) Dungeon.chances(debuffs);
		
		if (debuffCls != null){
			Buff.append(enemy, debuffCls, 6 + buffedLvl()*3);
		} else {
			//if no debuff can be applied (all are present), then go up one tier
			if (category == MINOR_DEBUFFS)          debuffEnemy( enemy, MAJOR_DEBUFFS);
			else if (category == MAJOR_DEBUFFS)     corruptEnemy( enemy );
		}
	}
	
	private void corruptEnemy( Mob enemy ){
		//cannot re-corrupt or doom an enemy, so give them a major debuff instead
		if(enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null){
			GLog.w( Messages.get(this, "already_corrupted") );
			return;
		}
		
		if (!enemy.isImmune(Corruption.class)){
			Corruption.corruptionHeal(enemy);

			AllyBuff.affectAndLoot(enemy, curUser, Corruption.class);
		} else {
			Buff.affect(enemy, Doom.class);
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, long damage) {
		long level = Math.max( 0, buffedLvl() );

		// lvl 0 - 16%
		// lvl 1 - 28.5%
		// lvl 2 - 37.5%
		float procChance = (level+1f)/(level+6f) * procChanceMultiplier(attacker);
		if (Dungeon.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			Buff.prolong( defender, Amok.class, Math.round((4+level*2) * powerMulti));
		}
	}

	@Override
	public String upgradeStat1(long level) {
		return Messages.decimalFormat("#.##", 3f + level/3f);
	}

	@Override
	public String upgradeStat2(long level) {
		return Long.toString(6 + 3*level);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.SHADOW,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0 );
		particle.am = 0.6f;
		particle.setLifespan(2f);
		particle.speed.set(0, 5);
		particle.setSize( 0.5f, 2f);
		particle.shuffleXY(1f);
	}

}
