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

package com.shatteredpixel.cursedpixeldungeon.items.stones;

import com.shatteredpixel.cursedpixeldungeon.Assets;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.cursedpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class StoneOfDeepenedSleep extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_SLEEP;
	}
	
	@Override
	protected void activate(int cell) {
		
		for (int i : PathFinder.NEIGHBOURS9){
			
			CellEmitter.get(cell + i).start( Speck.factory( Speck.NOTE ), 0.1f, 2 );
			
			if (Actor.findChar(cell + i) != null) {
				
				Char c = Actor.findChar(cell + i);
				
				if ((c instanceof Mob && ((Mob) c).state == ((Mob) c).SLEEPING)){
					
					Buff.affect(c, MagicalSleep.class);
					
				}
				
			}
		}
		
		Sample.INSTANCE.play( Assets.SND_LULLABY );
		
	}
}
