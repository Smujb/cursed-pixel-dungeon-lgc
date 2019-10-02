package com.shatteredpixel.shatteredpixeldungeon.items.allies;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WaterDragonSprite;
import com.watabou.noosa.tweeners.AlphaTweener;

public class WaterDragon extends DragonItem {

    public Dragon dragon = new Dragon();
    private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x4CC5ED );

    public static class Dragon extends DragonMob {
        {
            spriteClass = WaterDragonSprite.class;
            immunities.add(Chill.class);//immune to chill
            baseSpeed = 1.5f;
        }

        @Override
        public boolean act() {

            if (Dungeon.level.water[pos] && HP < HT) {
                sprite.emitter().burst( Speck.factory( Speck.STEAM ), 1 );

                HP+= Math.min(HT/5,missingHP());
            }

            return super.act();
        }
        @Override
        public int HPcalc(int level) {
            return  16 + 8 * level;
        }

        @Override
        public int defenseSkill(Char enemy) {
            return (SpawnerLevel+2);
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
        return BLUE;
    }
}
