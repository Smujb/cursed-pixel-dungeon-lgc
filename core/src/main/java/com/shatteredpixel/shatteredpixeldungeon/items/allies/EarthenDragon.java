package com.shatteredpixel.shatteredpixeldungeon.items.allies;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EarthenDragonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PoisonDragonSprite;
import com.watabou.noosa.tweeners.AlphaTweener;

public class EarthenDragon extends DragonItem {
    public Dragon dragon = new Dragon();
    private static ItemSprite.Glowing BROWN = new ItemSprite.Glowing( 0x5C4E29);

    public static class Dragon extends DragonMob {
        {
            spriteClass = EarthenDragonSprite.class;
            immunities.add(Paralysis.class);//immune to paralysis
        }

        public int attackProc(Char enemy, int damage ) {
            damage = super.attackProc( enemy, damage );
            Buff.affect( enemy, Roots.class, 3);

            return damage;

        }

        @Override
        public int defenseProc( Char enemy, int damage ) {
            Buff.affect( this, Roots.class, 3);
            Buff.affect( this, Earthroot.Armor.class ).level( 5 + 4 * SpawnerLevel );
            return super.defenseProc(enemy, damage);
        }

        @Override
        public int defenseSkill(Char enemy) {
            return super.defenseSkill(enemy)/2;
        }
    }

    @Override
    public void SpawnDragon(int newPos, int pos) {
        dragon = new Dragon();
        dragon.SpawnerLevel = level();
        dragon.HP = dragon.HT = dragon.HPcalc(dragon.SpawnerLevel);
        dragon.alignment = Char.Alignment.ALLY;
        dragon.pos = newPos;
        dragon.setLevel(level());

        GameScene.add( dragon );
        Actor.addDelayed( new Pushing( dragon, pos, newPos ), -1f );

        dragon.sprite.alpha( 0 );
        dragon.sprite.parent.add( new AlphaTweener( dragon.sprite, 1, 0.15f ) );
        dragon.notice();
    }
    @Override
    public ItemSprite.Glowing glowing() {
        return BROWN;
    }

}