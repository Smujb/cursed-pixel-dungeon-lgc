package com.shatteredpixel.cursedpixeldungeon.actors.mobs;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.EvilGas;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.cursedpixeldungeon.sprites.KupuaSprite;
import com.watabou.utils.Random;

public class Kupua extends Mob {
    {
        spriteClass = KupuaSprite.class;
        HP = HT = 250;
        EXP = 10;
        maxLvl = 30;
        immunities.add(Grim.class);
        immunities.add(ToxicGas.class);
        immunities.add(EvilGas.class);
    }

    @Override
    public int damageRoll() {
        return Random.IntRange(30,70);
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int drRoll() {
        return Random.IntRange(30,50);
    }
}
