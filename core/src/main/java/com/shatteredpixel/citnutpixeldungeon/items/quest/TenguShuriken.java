/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.items.quest;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;

public class TenguShuriken extends MissileWeapon {

    {
        image = ItemSpriteSheet.SHURIKEN;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.4f;

        internalTier = tier = 2;
        baseUses = 2500; //is unbreakable
    }

    @Override
    public long max(long lvl) {
        return  10 * tier +                      //8 base, down from 10
                (tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
    }

    @Override
    public float baseDelay(Char owner) {
        return 0;
    }

    @Override
    public long proc(Char attacker, Char defender, long damage) {
        Buff.affect(defender, Sai.DefenseDebuff.class, 5f).stack = (int) (max() / 3);
        return super.proc(attacker, defender, damage);
    }
}

