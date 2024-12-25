package com.shatteredpixel.citnutpixeldungeon.items.weapon.melee;

import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.traits.PreparationAllowed;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.ActionIndicator;

public class StaffOfHoma extends Glaive{
    {
        image = ItemSpriteSheet.STAFFOFHOMA;
        internalTier = tier = 6;
        ACC = 1.9f;
        DLY = 1f;
    }

    private long HTbonus = 0L;
    private boolean isBuffed = false;

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)){
            ActionIndicator.refresh();
            if(!isBuffed){
                HTbonus = HTbuff(hero);
                hero.HT+= HTbonus;
                isBuffed = !isBuffed;
            }
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
            if(HTbonus != 0) isBuffed = !isBuffed;
            hero.HT-= HTbonus;
            HTbonus = 0;
            return true;
        }
        return false;
    }

    private static long HTbuff(Hero hero){
        return (long) Math.max(Math.floor(hero.HT*0.4), 0);
    }

    @Override
    public long damageRoll(Char owner){
        long basedmg = super.damageRoll(owner);
        
        long HT = owner.HT;
        long boostATK = (long) Math.max(Math.floor(0.16*HT), 0);
        if (owner.HP <= Math.floor(0.5*HT)) boostATK += (long) Math.max(Math.floor(0.18*HT),0);
        System.out.println(basedmg);
        return basedmg + boostATK;
    }
}
