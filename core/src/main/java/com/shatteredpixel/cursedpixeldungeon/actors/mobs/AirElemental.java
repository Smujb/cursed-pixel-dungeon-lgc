package com.shatteredpixel.cursedpixeldungeon.actors.mobs;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.cursedpixeldungeon.sprites.AirElementalSprite;
import com.watabou.utils.Random;

public class AirElemental extends Mob {
    {
        HP = HT = 150;
        defenseSkill = 35;
        spriteClass = AirElementalSprite.class;
        immunities.add(Vertigo.class);
        immunities.add(Levitation.class);
        immunities.add(Blindness.class);
        immunities.add(ToxicGas.class);
        immunities.add(ParalyticGas.class);
        EXP = 20;
        maxLvl = 30;
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(30,90);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if ( Random.Int(2) == 0) {
            Buff.affect(enemy, Vertigo.class, Vertigo.DURATION);
        } else if (Random.Int(3) == 0) {
            Buff.affect(enemy, Blindness.class, 4f);
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public int drRoll() {
        return 0;
    }
}
