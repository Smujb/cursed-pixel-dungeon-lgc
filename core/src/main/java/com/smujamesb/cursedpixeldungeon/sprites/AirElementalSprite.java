package com.smujamesb.cursedpixeldungeon.sprites;

import com.smujamesb.cursedpixeldungeon.Assets;
import com.smujamesb.cursedpixeldungeon.actors.Char;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class AirElementalSprite extends MobSprite {

    public AirElementalSprite() {
        super();

        texture(Assets.ELEMENTAL);

        int ofs = 42;

        TextureFilm frames = new TextureFilm(texture, 12, 14);

        idle = new MovieClip.Animation(10, true);
        idle.frames(frames, ofs + 0, ofs + 1, ofs + 2);

        run = new MovieClip.Animation(12, true);
        run.frames(frames, ofs + 0, ofs + 1, ofs + 3);

        attack = new MovieClip.Animation(15, false);
        attack.frames(frames, ofs + 4, ofs + 5, ofs + 6);

        die = new MovieClip.Animation(15, false);
        die.frames(frames, ofs + 7, ofs + 8, ofs + 9, ofs + 10, ofs + 11, ofs + 12, ofs + 13, ofs + 12);

        play(idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        add(State.LEVITATING);
    }

    @Override
    public void die() {
        super.die();
        remove(State.LEVITATING);
    }

    @Override
    public int blood() {
        return 0xFFFF7D13;
    }


}

