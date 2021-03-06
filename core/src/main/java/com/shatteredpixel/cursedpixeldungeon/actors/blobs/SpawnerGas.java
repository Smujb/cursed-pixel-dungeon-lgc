package com.shatteredpixel.cursedpixeldungeon.actors.blobs;

import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Minion;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class SpawnerGas extends SmokeScreen {
    @Override
    protected void evolve() {
        super.evolve();
        int cell;

        for (int i = area.left; i < area.right; i++){
            for (int j = area.top; j < area.bottom; j++){
                if (Random.Int(600) == 1) {
                    cell = i + j*Dungeon.level.width();
                    Minion mob = new Minion();
                    mob.state = mob.WANDERING;
                    mob.pos = cell;
                    GameScene.add(mob);
                }

            }
        }
    }
}



