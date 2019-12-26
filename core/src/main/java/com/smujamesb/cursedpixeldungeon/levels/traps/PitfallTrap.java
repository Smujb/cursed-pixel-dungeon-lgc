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

package com.smujamesb.cursedpixeldungeon.levels.traps;

import com.smujamesb.cursedpixeldungeon.Dungeon;
import com.smujamesb.cursedpixeldungeon.actors.Actor;
import com.smujamesb.cursedpixeldungeon.actors.Char;
import com.smujamesb.cursedpixeldungeon.actors.mobs.Mob;
import com.smujamesb.cursedpixeldungeon.items.Heap;
import com.smujamesb.cursedpixeldungeon.items.Item;
import com.smujamesb.cursedpixeldungeon.levels.features.Chasm;
import com.smujamesb.cursedpixeldungeon.messages.Messages;
import com.smujamesb.cursedpixeldungeon.scenes.GameScene;
import com.smujamesb.cursedpixeldungeon.utils.GLog;

public class PitfallTrap extends Trap {

	{
		color = RED;
		shape = DIAMOND;
	}

	@Override
	public void activate() {
		
		if( Dungeon.bossLevel() || Dungeon.depth == 26){
			GLog.w(Messages.get(this, "no_pit"));
			return;
		}
		
		Heap heap = Dungeon.level.heaps.get( pos );

		if (heap != null){
			for (Item item : heap.items){
				Dungeon.dropToChasm(item);
			}
			heap.sprite.kill();
			GameScene.discard(heap);
			Dungeon.level.heaps.remove( pos );
		}

		Char ch = Actor.findChar( pos );

		if (ch != null && !ch.flying) {
			if (ch == Dungeon.hero) {
				Chasm.heroFall(pos);
			} else {
				Chasm.mobFall((Mob) ch);
			}
		}
	}

	//TODO these used to become chasms when disarmed, but the functionality was problematic
	//because it could block routes, perhaps some way to make this work elegantly?
}
