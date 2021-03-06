package com.shatteredpixel.cursedpixeldungeon.items.potions;

import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class MegaExperiencePotion extends Item {
    {
        name = "MEGA Potion of Experience";

        bones = false;

        image = ItemSpriteSheet.POTION_AMBER;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public static final String AC_DRINK = "DRINK";
    public void apply(Hero hero) {
        for (int i = 0; i < 50; i++) {
            hero.earnExp(hero.maxExp() - hero.exp, PotionOfExperience.class);
        }
        detach(hero.belongings.backpack);
    }

    @Override
    public String desc() {
        return "Drink this for cool stuff like much experience. It is good.";
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        if (action.equals(AC_DRINK)) {
            apply(hero);
        } else {
            super.execute(hero,action);
        }
    }
}
