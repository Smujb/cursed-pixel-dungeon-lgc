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

package com.shatteredpixel.cursedpixeldungeon.actors.blobs;

import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.cursedpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class EvilGas extends CorrosiveGas {

    //FIXME should have strength per-cell
    private int strength = 0;

    @Override
    protected void evolve() {
        super.evolve();

        if (volume == 0){
            strength = 0;
        } else {
            Char ch;
            int cell;

            for (int i = area.left; i < area.right; i++){
                for (int j = area.top; j < area.bottom; j++){
                    cell = i + j*Dungeon.level.width();
                    if (cur[cell] > 0 && (ch = Actor.findChar( cell )) != null) {
                        if (!ch.isImmune(this.getClass())) {
                            Buff.affect(ch, Corrosion.class).set(4f, strength);
                            Buff.prolong(ch, Vertigo.class, 2);
                            //Cripple.prolong(ch, Cripple.class, 4f);
                        }
                    }
                }
            }
        }
    }

    public CorrosiveGas setStrength(int str){
        if (str > strength) {
            strength = str;
        }
        return this;
    }

    private static final String STRENGTH = "strength";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        strength = bundle.getInt( STRENGTH );
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( STRENGTH, strength );
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );

        emitter.pour( Speck.factory(Speck.EVIL_GAS), 0.4f );
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}