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

package com.shatteredpixel.cursedpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.cursedpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Shielding extends Weapon.Enchantment {

	private static ItemSprite.Glowing SILVER = new ItemSprite.Glowing( 0x909396 );
	
	@Override
	public int proc(Item weapon, Char attacker, Char defender, int damage ) {
		
		//chance to heal scales from 50%-100% based on missing HP
		float missingPercent = (attacker.HT - attacker.HP) / (float)attacker.HT;
		float healChance = 0.50f + .75f*missingPercent;
		
		if (Random.Float() < healChance){
			
			//heals for 25% of damage dealt
			int healAmt = Math.round(damage * 0.35f);
			healAmt = Math.min( healAmt, attacker.HT - attacker.HP );

			if (healAmt > 0 && attacker.isAlive()) {

				Buff.affect(attacker, Barrier.class).setShield(healAmt);;
				attacker.sprite.emitter().start( Speck.factory( Speck.DISCOVER ), 0.4f, 1 );
				attacker.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healAmt ) );
				
			}
		}

		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return SILVER;
	}
}