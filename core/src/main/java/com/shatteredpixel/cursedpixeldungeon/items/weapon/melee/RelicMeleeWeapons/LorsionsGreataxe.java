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

package com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons;

import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons.RelicEnchantments.Damning;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons.RelicEnchantments.RelicEnchantment;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;

public class LorsionsGreataxe extends RelicMeleeWeapon {

	{
		image = ItemSpriteSheet.GREATAXE;

		tier = 6;
		ACC = 1.33f;
		damageMultiplier = 1.2f;
		chargeToAdd = 1f;
	}

	private boolean heavyAttack = false;

	@Override
	public int damageRoll(Char owner) {
		float multiplier = 1f;
		if (heavyAttack) {
			multiplier = 1f + 5f * owner.missingHPPercent();
		}
		heavyAttack = false;
		return (int) (super.damageRoll(owner) * multiplier);
	}

	public void prepare() {
		heavyAttack = true;
	}

	@Override
	public RelicEnchantment enchantment() {
		return new Damning();
	}

	@Override
	public int STRReq(int lvl) {
		return 20;
	}

}
