package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.allies.PoisonDragon;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class VampiricDragonSprite extends MobSprite {
    public VampiricDragonSprite() {
        super();

        texture( Assets.PETDRAGON_EXTRA );

        TextureFilm frames = new TextureFilm( texture, 16, 16 );

        idle = new MovieClip.Animation( 2, true );
        idle.frames( frames, 32, 32, 32, 33 );

        run = new MovieClip.Animation( 10, true );
        run.frames( frames, 38, 39, 40, 41, 42 );

        attack = new MovieClip.Animation( 15, false );
        attack.frames( frames, 34, 35, 36, 37, 32 );

        die = new MovieClip.Animation( 10, false );
        die.frames( frames, 43, 44, 45, 46 );

        play( idle );
    }
}