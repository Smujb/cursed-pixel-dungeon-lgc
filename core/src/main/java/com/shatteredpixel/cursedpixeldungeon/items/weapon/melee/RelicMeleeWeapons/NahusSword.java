package com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons;

import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons.RelicEnchantments.Drawing;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.RelicMeleeWeapons.RelicEnchantments.RelicEnchantment;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;

public class NahusSword extends RelicMeleeWeapon {
    {
        image = ItemSpriteSheet.NAHUSSWORD;
        chargeToAdd = 1f;
    }

    @Override
    public RelicEnchantment enchantment() {
        return new Drawing();
    }
}
