/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.cursedpixeldungeon.levels;

import com.shatteredpixel.cursedpixeldungeon.Assets;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.effects.Ripple;
import com.shatteredpixel.cursedpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.cursedpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.cursedpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WaterChallengeLevel extends RegularLevel {

    {
        color1 = 0x48763c;
        color2 = 0x59994a;
    }

    @Override
    protected int standardRooms() {
        //5 to 7, average 5.57
        return 5+Random.chances(new float[]{4, 2, 1});
    }

    @Override
    protected int specialRooms() {
        //1 to 3, average 1.67
        return 1+Random.chances(new float[]{4, 4, 2});
    }

    @Override
    protected Painter painter() {
        return new SewerPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.75f, 5)
                .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_WATER_CHALLENGE;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_SEWERS;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return Dungeon.depth == 1 ?
                new Class<?>[]{ WornDartTrap.class } :
                new Class<?>[]{ ChillingTrap.class, ShockingTrap.class, OozeTrap.class, PoisonDartTrap.class,
                        AlarmTrap.class, OozeTrap.class,
                        ConfusionTrap.class, FlockTrap.class, GrimTrap.class, WarpingTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return Dungeon.depth == 1 ?
                new float[]{1} :
                new float[]{8, 8, 8, 8,
                        4, 4,
                        2, 2, 2, 2};
    }

    @Override
    protected void createItems() {

        super.createItems();
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addSewerVisuals(this, visuals);
        return visuals;
    }

    public static void addSewerVisuals( Level level, Group group ) {
        for (int i=0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add( new Sink( i ) );
            }
        }
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(SewerLevel.class, "water_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(SewerLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SewerLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    private static class Sink extends Emitter {

        private int pos;
        private float rippleDelay = 0;

        private static final Emitter.Factory factory = new Factory() {

            @Override
            public void emit( Emitter emitter, int index, float x, float y ) {
                WaterParticle p = (WaterParticle)emitter.recycle( WaterParticle.class );
                p.reset( x, y );
            }
        };

        public Sink( int pos ) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.tileCenterToWorld( pos );
            pos( p.x - 2, p.y + 3, 4, 0 );

            pour( factory, 0.1f );
        }

        @Override
        public void update() {
            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {

                super.update();

                if ((rippleDelay -= Game.elapsed) <= 0) {
                    Ripple ripple = GameScene.ripple( pos + Dungeon.level.width() );
                    if (ripple != null) {
                        ripple.y -= DungeonTilemap.SIZE / 2;
                        rippleDelay = Random.Float(0.4f, 0.6f);
                    }
                }
            }
        }
    }

    public static final class WaterParticle extends PixelParticle {

        public WaterParticle() {
            super();

            acc.y = 50;
            am = 0.5f;

            color( ColorMath.random( 0xb6ccc2, 0x3b6653 ) );
            size( 2 );
        }

        public void reset( float x, float y ) {
            revive();

            this.x = x;
            this.y = y;

            speed.set( Random.Float( -2, +2 ), 0 );

            left = lifespan = 0.4f;
        }
    }
}