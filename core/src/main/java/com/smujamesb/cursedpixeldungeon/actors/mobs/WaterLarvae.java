package com.smujamesb.cursedpixeldungeon.actors.mobs;

import com.smujamesb.cursedpixeldungeon.actors.Char;
import com.smujamesb.cursedpixeldungeon.sprites.WaterLarvaSprite;
import com.watabou.utils.Random;

public class WaterLarvae extends Mob {

    {
        spriteClass = WaterLarvaSprite.class;

        HP = HT = 120;
        defenseSkill = 25;

        EXP = 22;
        maxLvl = 30;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill( Char target ) {
        return 30;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 80, 120 );
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

}
