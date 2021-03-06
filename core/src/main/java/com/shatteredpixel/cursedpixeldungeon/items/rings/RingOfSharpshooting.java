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

package com.shatteredpixel.cursedpixeldungeon.items.rings;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;

import java.text.DecimalFormat;

public class RingOfSharpshooting extends Ring {
	
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", soloBonus(), new DecimalFormat("#.##").format(100f * (1.25 * soloBonus())));
		} else {
			return Messages.get(this, "typical_stats", 1, new DecimalFormat("#.##").format(20f));
		}
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Aim();
	}
	
	public static int levelDamageBonus( Char target ){
		int bonus = Math.min(16, getBonus(target, RingOfSharpshooting.Aim.class));
		return bonus/3;
	}
	
	public static float durabilityMultiplier( Char target ){
		int bonus = Math.min(16, getBonus(target, RingOfSharpshooting.Aim.class));
		return (float)(1 + 1.25 * bonus);
	}

	public class Aim extends RingBuff {
	}
}
