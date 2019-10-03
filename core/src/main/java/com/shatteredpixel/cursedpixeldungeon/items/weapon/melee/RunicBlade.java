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

package com.shatteredpixel.cursedpixeldungeon.items.weapon.melee;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RunicBlade extends MeleeWeapon {

	{
		image = ItemSpriteSheet.RUNIC_BLADE;

		tier = 2;
	}

	//Does 1 - 10 base damage, scales exponentially max dmg (+2, +3, +4...) and not at all min damage
	@Override
	public int min(int lvl) {
		return  1;

	}
	@Override
	public int max(int lvl) {
		return  6*(tier) +                	//15 base
				Math.round((level()+1)*(level()+1)/2);	//+level() +1 scaling

	}

	public int damageRoll( Char owner ) {
		return Random.IntRange( min(), max() );
	}//Runic Blade doesn't use normal distribution
}