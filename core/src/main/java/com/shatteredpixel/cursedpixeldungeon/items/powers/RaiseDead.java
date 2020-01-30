package com.shatteredpixel.cursedpixeldungeon.items.powers;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;

public class RaiseDead extends ActivatedPower {

    {
        image = ItemSpriteSheet.RAISEDEAD;
        mp_cost = 8;
    }

    @Override
    public boolean usesTargeting() {
        return false;
    }

    @Override
    public void affectCell(int pos) {
        Mob.spawnAround4(Dead.class, pos);
    }

    public static class Dead extends Wraith {
        {
            alignment = Alignment.ALLY;
        }

        @Override
        public int damageRoll() {
            return super.damageRoll()*2;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            new FlavourBuff(){
                {actPriority = VFX_PRIO;}
                public boolean act() {
                    target.die(new Dead());
                    return super.act();
                }
            }.attachTo(this);
            return super.attackProc(enemy, damage);
        }
    }
}
