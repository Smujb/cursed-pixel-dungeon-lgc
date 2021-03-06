package com.shatteredpixel.cursedpixeldungeon.items.powers;

import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.cursedpixeldungeon.levels.WaterChallengeLevel;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WaterPump extends ActivatedPower {

    {
        image = ItemSpriteSheet.WATERPUMP;
        mp_cost = 7;
    }

    @Override
    public boolean usesTargeting() {
        return false;
    }

    @Override
    public void affectCell(int pos) {
        boolean[] passable = Dungeon.level.passable.clone();
        PathFinder.buildDistanceMap(pos, passable, 3);
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (PathFinder.distance[mob.pos] < Integer.MAX_VALUE) {
                mob.damage(Random.IntRange(4 + Dungeon.scaleWithDepth()*2, 10 + Dungeon.scaleWithDepth()*6), this);
                mob.sprite.emitter().burst(WaterChallengeLevel.WaterParticle.FACTORY, 30);
            }
        }
        Camera.main.shake(3, 1f);
    }
}
