package com.shatteredpixel.citnutpixeldungeon.items.weapon.melee;

import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.traits.PreparationAllowed;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.ActionIndicator;

public class StaffOfHoma extends Glaive{
    {
        image = ItemSpriteSheet.STAFFOFHOMA;
        internalTier = tier = 6;
        ACC = 1.8f;
        DLY = 1f;
    }

    public static final String name = "Staff of Homa" ;


    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)){
            ActionIndicator.refresh();
            hero.updateHT( false );
            return true;
        }
        return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            if (this instanceof PreparationAllowed)
                Buff.detach(hero, Preparation.class);
            ActionIndicator.refresh();
            hero.updateHT( false );
            return true;
        }
        return false;
    }

    public static long ATKbonus(Char owner){
        if (owner == null) return 0;
        long HT = owner.HT;
        long boostATK = (long) Math.max(Math.floor(0.16d*HT), 0);
        if (owner.HP <= Math.floor(0.5f*HT)) boostATK += (long) Math.max(Math.floor(0.18d*HT),0);
        boostATK += Math.max(HT - owner.HP, 0);

        return boostATK;
    }

    @Override
    public long damageRoll(Char owner){
        return super.damageRoll(owner) + ATKbonus(owner);
    }

    @Override
    public long max(long lvl) {
        return  Math.round(10d*(tier()+1)) + lvl*Math.round(2d*(tier()+1));
    }

    public static long HTMultiplier(Hero target){
        if (target.belongings.weapon == null) return 0L;
        return target.belongings.weapon.name().equals(name) ? (long) Math.floor(target.HT*0.4d) : 0L;
    }

    @Override
    public String statsInfo(){
        return Messages.get(this, "stats_desc", augment.damageFactor(ATKbonus(Dungeon.hero)));
    }
}
