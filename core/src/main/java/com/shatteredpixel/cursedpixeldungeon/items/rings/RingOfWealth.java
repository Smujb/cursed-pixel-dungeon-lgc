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

import com.shatteredpixel.cursedpixeldungeon.Challenges;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.items.Generator;
import com.shatteredpixel.cursedpixeldungeon.items.Gold;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.armor.Armor;
import com.shatteredpixel.cursedpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.cursedpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.cursedpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.cursedpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.cursedpixeldungeon.items.spells.ArcaneCatalyst;
import com.shatteredpixel.cursedpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class RingOfWealth extends Ring {
	@Override
	public int price() {
		return 820;
	}
	public boolean isUpgradable() {
		return level() <= 19;
	}
	private float triesToDrop = Float.MIN_VALUE;
	private int dropsToRare = Integer.MIN_VALUE;
	private static float dropsToUpgrade = 30;
	private static final float dropsIncreases = 15;

	public static boolean latestDropWasRare = false;
	
	public String statsInfo() {
		if (isIdentified()){
			float dropChance = 100f*(1/dropsToUpgrade);
			if (Dungeon.isChallenged(Challenges.NO_SCROLLS)) {//Show 0% on Forbidden Runes
				dropChance = 0f;
			}
			if (dropsToUpgrade <= 0) {//Display 100% chance if the value goes negative, as a scroll is guaranteed
				dropChance = 100f;
			}
			return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (Math.pow(1.2f, soloBonus()) - 1f)), new DecimalFormat("#.##").format(dropChance));
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(20f));
		}
	}

	private static final String TRIES_TO_DROP = "tries_to_drop";
	private static final String DROPS_TO_RARE = "drops_to_rare";
	private static final String DROPS_TO_UPGRADE = "drops_to_upgrade";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TRIES_TO_DROP, triesToDrop);
		bundle.put(DROPS_TO_RARE, dropsToRare);
		bundle.put(DROPS_TO_UPGRADE, dropsToUpgrade);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		triesToDrop = bundle.getFloat(TRIES_TO_DROP);
		dropsToRare = bundle.getInt(DROPS_TO_RARE);
		dropsToUpgrade = bundle.getInt(DROPS_TO_UPGRADE);
	}

	@Override
	protected RingBuff buff( ) {
		return new Wealth();
	}
	
	public static float dropChanceMultiplier( Char target ){
		return (float)Math.pow(1.2, getBonus(target, Wealth.class));
	}
	
	public static ArrayList<Item> tryForBonusDrop(Char target, int tries ){
		if (getBonus(target, Wealth.class) <= 0) return null;
		
		HashSet<Wealth> buffs = target.buffs(Wealth.class);
		float triesToDrop = Float.MIN_VALUE;
		int dropsToRare = Integer.MIN_VALUE;
		
		//find the largest count (if they aren't synced yet)
		for (Wealth w : buffs){
			if (w.triesToDrop() > triesToDrop){
				triesToDrop = w.triesToDrop();
				dropsToRare = w.dropsToRare();
			}
		}

		//set (if needed), decrement, and store counts
		if (triesToDrop == Float.MIN_VALUE) {
			triesToDrop = Random.NormalIntRange(0, 60);
			dropsToRare = Random.NormalIntRange(0, 20);
		}

		//now handle reward logic
		ArrayList<Item> drops = new ArrayList<>();

		triesToDrop -= dropProgression(target, tries);

		while ( triesToDrop <= 0 ){
			if ((dropsToUpgrade < 2) || (Random.Int( (int) dropsToUpgrade) == 1) & !Dungeon.isChallenged(Challenges.NO_SCROLLS)) {
				drops.add(new ScrollOfUpgrade());
				dropsToUpgrade += dropsIncreases;
				dropsToRare--;
			} else {
				if (dropsToRare <= 0){
					Item i;
					do {
						i = genRareDrop();
					} while (Challenges.isItemBlocked(i));
					drops.add(i);
					latestDropWasRare = true;
					dropsToUpgrade-=3;
					dropsToRare = Random.NormalIntRange(0, 20);
				} else {
					Item i;
					do {
						i = genStandardDrop();
					} while (Challenges.isItemBlocked(i));
					drops.add(i);
					dropsToRare--;
					dropsToUpgrade--;
				}

			}

			triesToDrop += Random.NormalIntRange(0, 60);
		}

		//store values back into rings
		for (Wealth w : buffs){
			w.triesToDrop(triesToDrop);
			w.dropsToRare(dropsToRare);
		}
		
		return drops;
	}
	
	public static Item genStandardDrop(){
		float roll = Random.Float();
		if (roll < 0.3f){ //30% chance
			Item result = new Gold().random();
			result.quantity(Math.round(result.quantity() * Random.NormalFloat(0.33f, 1f)));
			return result;
		} else if (roll < 0.8f){ //50% chance
			return genBasicConsumable();
		} else { //20% chance
			return genExoticConsumable();
		}

	}
	
	private static Item genBasicConsumable(){
		float roll = Random.Float();
		if (roll < 0.4f){ //40% chance
			//
			Scroll scroll;
			do {
				scroll = (Scroll) Generator.random(Generator.Category.SCROLL);
			} while (scroll == null || ((scroll instanceof ScrollOfUpgrade) & !Dungeon.isChallenged(Challenges.NO_SCROLLS)));
			return scroll;
		} else if (roll < 0.6f){ //20% chance to drop a minor food item
			return Random.Int(2) == 0 ? new SmallRation() : new MysteryMeat();
		} else { //40% chance
			return Generator.random(Generator.Category.POTION);
		}
	}
	
	private static Item genExoticConsumable(){
		float roll = Random.Float();
		if (roll < 0.2f){ //20% chance
			return Generator.random(Generator.Category.POTION_EXOTIC);
		} else if (roll < 0.5f) { //30% chance
			Scroll scroll;
			do {
				scroll = (Scroll) Generator.random(Generator.Category.SCROLL_EXOTIC);
			} while (scroll == null || ((scroll instanceof ScrollOfEnchantment) & !Dungeon.isChallenged(Challenges.NO_SCROLLS)));
			return scroll;
		} else { //50% chance
			return Random.Int(2) == 0 ? new ArcaneCatalyst() : new AlchemicalCatalyst();
		}
	}
	
	public static Item genRareDrop(){
		float roll = Random.Float();
		if (roll < 0.3f){ //30% chance
			Item result = new Gold().random();
			result.quantity(Math.round(result.quantity() * Random.NormalFloat(3f, 6f)));
			return result;
		} else if (roll < 0.5f){ //30% chance
			return genHighValueConsumable();
		} else if (roll < 0.7f){ //30% chance
			Item result;
			int random = Random.Int(3);
			switch (random){
				default:
					result = Generator.random(Generator.Category.ARTIFACT);
					break;
				case 1:
					result = Generator.random(Generator.Category.RING);
					break;
				case 2:
					result = Generator.random(Generator.Category.WAND);
					break;
				case 3:
					result = Generator.random(Generator.Category.ALLIES);
			}
			result.cursed = false;
			result.cursedKnown = true;
			return result;
		} else { //10% chance
			if (Random.Int(3) != 0){
				Weapon weapon = Generator.randomWeapon((Dungeon.scaleWithDepth() / 5) + 1);
				weapon.upgrade(1);
				weapon.enchant(Weapon.Enchantment.random());
				weapon.cursed = false;
				weapon.cursedKnown = true;
				return weapon;
			} else {
				Armor armor = Generator.randomArmor((Dungeon.scaleWithDepth() / 5) + 1);
				armor.upgrade();
				armor.inscribe(Armor.Glyph.random());
				armor.cursed = false;
				armor.cursedKnown = true;
				return armor;
			}
		}
	}
	
	private static Item genHighValueConsumable(){
		switch( Random.Int(4) ){ //25% chance each
			case 0: default:
				return Generator.random(Generator.Category.SPELL);
			case 1:
				return new StoneOfEnchantment().quantity(3);
			case 2:
				return Generator.random(Generator.Category.ELXIR);
			case 3:
				return new MeatPie();
		}
	}
	
	private static float dropProgression( Char target, int tries ){
		return tries * (float)Math.pow(1.2f, getBonus(target, Wealth.class) );
	}

	public class Wealth extends RingBuff {
		
		private void triesToDrop( float val ){
			triesToDrop = val;
		}
		
		private float triesToDrop(){
			return triesToDrop;
		}

		private void dropsToRare( int val ) {
			dropsToRare = val;
		}

		private int dropsToRare(){
			return dropsToRare;
		}
		
	}
}
