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

package com.shatteredpixel.cursedpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.cursedpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.cursedpixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfIcyTouch extends Elixir {
	
	{
		//TODO finish visuals
		image = ItemSpriteSheet.ELIXIR_ICY;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
		hero.sprite.emitter().burst(SnowParticle.FACTORY, 5);
	}
	
	@Override
	protected int splashColor() {
		return 0xFF18C3E6;
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (50 + 40);
	}
	
	public static class Recipe extends com.shatteredpixel.cursedpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfSnapFreeze.class, AlchemicalCatalyst.class};
			inQuantity = new int[]{1, 1};
			
			cost = 6;
			
			output = ElixirOfIcyTouch.class;
			outQuantity = 1;
		}
		
	}
}
