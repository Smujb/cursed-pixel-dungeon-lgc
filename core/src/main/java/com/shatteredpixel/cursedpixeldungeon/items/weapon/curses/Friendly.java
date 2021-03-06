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

package com.shatteredpixel.cursedpixeldungeon.items.weapon.curses;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Friendly extends Weapon.Enchantment {
	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Item weapon, Char attacker, Char defender, int damage ) {
		
		if (Random.Int(10) == 0){
			
			int base = Random.IntRange(3, 5);
			
			Buff.affect( attacker, Charm.class, base + 5 ).object = defender.id();
			attacker.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
			
			//5 turns will be reduced by the attack, so effectively lasts for base turns
			Buff.affect( defender, Charm.class, base + 15 ).object = attacker.id();//Charms enemy for longer
			defender.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
			
		}
		
		return damage;
	}
	
	@Override
	public boolean curse() {
		return true;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
