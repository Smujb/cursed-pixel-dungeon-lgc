package com.shatteredpixel.cursedpixeldungeon.items.allies;

import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.cursedpixeldungeon.sprites.PoisonDragonSprite;

public class PoisonDragon extends DragonCrystal {
    public Dragon dragon = new Dragon();
    {
        image  = ItemSpriteSheet.ADORNEDDRAGONCRYSTAL;
    }

    @Override
    public DragonCrystal.Dragon GetDragonTypeToSpawn() {
        return new Dragon();
    }

    public static class Dragon extends DragonCrystal.Dragon {
        {
            spriteClass = PoisonDragonSprite.class;
            properties.add(Property.ACIDIC);
            immunities.add(Poison.class);//immune to poison
        }

        public int attackProc(Char enemy, int damage ) {
            damage = (int)(super.attackProc( enemy, damage )*0.75f);
            Buff.affect( enemy, Poison.class ).set( 2 + Dungeon.depth / 6 );

            return damage;

        }

        @Override
        public Class CrystalType() {
            return PoisonDragon.class;
        }

        @Override
        public int defenseProc( Char enemy, int damage ) {
            enemy.damage(Math.round(damage/2),this);//damages enemies who attack
            return super.defenseProc(enemy, damage);
        }

        @Override
        public int drRoll() {
            return (int)(super.drRoll()*0.75);
        }
    }

}
