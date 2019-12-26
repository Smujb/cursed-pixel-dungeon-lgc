package com.shatteredpixel.cursedpixeldungeon.actors.mobs;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.cursedpixeldungeon.sprites.SonicBatSprite;
import com.watabou.utils.Random;

public class SonicBat extends Mob {
    {
        spriteClass = SonicBatSprite.class;
        EXP = 25;
        maxLvl = 30;
        HP = HT = 200;
        defenseSkill = 30;
        baseSpeed = 4f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(50,100);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0,10);
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }
}
