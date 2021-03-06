package com.shatteredpixel.cursedpixeldungeon.items.powers;

import com.shatteredpixel.cursedpixeldungeon.Challenges;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.Statistics;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.items.Generator;
import com.shatteredpixel.cursedpixeldungeon.items.Gold;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.armor.Armor;
import com.shatteredpixel.cursedpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.cursedpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.cursedpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.cursedpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.cursedpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.cursedpixeldungeon.windows.WndItem;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class LuckyBadge extends Power {
    {
        image = ItemSpriteSheet.LUCKYBADGE;
        defaultAction = AC_INFO;
        passiveBuff = passiveBuff();
    }
    public static final String AC_INFO = "INFO_WINDOW";
    public static final String AC_GRIND = "grind";
    public static final String AC_HOME = "home";
    public static final String AC_RETURN = "return";

    public static final int NONE = 0;
    public static final int GRIND = 1;
    public static final int SPEED = 2;

    public int type = NONE;
    private float triesToDrop = Float.MIN_VALUE;
    private int dropsToRare = Integer.MIN_VALUE;
    private static float dropsToUpgrade = 20;
    private static final float dropsIncreases = 26;
    public static final int GrindDepth = 27;
    public static final int HomeDepth = 28;
    private int returnDepth = -1;
    private int returnPos   = -1;
    private static boolean latestDropWasRare = false;


    @Override
    public int price() {
        return 82;
    }

    public boolean isUpgradable() {
        return false;
    }

    public LuckyBadge() {
        super();
        dropsToUpgrade = 20;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        switch (action) {
            case AC_INFO:
                GameScene.show(new WndItem(null, this, true));
                break;
            case AC_GRIND:
                InterlevelScene.mode = InterlevelScene.Mode.GRIND;
                if (Dungeon.depth != GrindDepth & Dungeon.depth != HomeDepth) {
                    returnDepth = Dungeon.depth;
                    returnPos = hero.pos;
                }
                Game.switchScene(InterlevelScene.class);
                break;
            case AC_HOME:
                InterlevelScene.mode = InterlevelScene.Mode.HOME;
                if (Dungeon.depth != GrindDepth & Dungeon.depth != HomeDepth) {
                    returnDepth = Dungeon.depth;
                    returnPos = hero.pos;
                }
                Game.switchScene(InterlevelScene.class);
                break;
            case AC_RETURN:
                if (returnDepth < 0) {
                    returnDepth = Statistics.deepestFloor;
                }
                if (returnPos < 0) {
                    InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                } else {
                    InterlevelScene.mode = InterlevelScene.Mode.RETURNTO;
                }
                InterlevelScene.returnPos = this.returnPos;
                InterlevelScene.returnDepth = this.returnDepth;
                Game.switchScene(InterlevelScene.class);
                break;
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!(Dungeon.depth==GrindDepth | Dungeon.depth == HomeDepth)) {
            if (type == GRIND) {
                actions.add(AC_GRIND);
            }
            actions.add(AC_HOME);
        } else {
            actions.add(AC_RETURN);
        }
        return actions;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc") + "\n\n" + statsInfo();
    }

    public String statsInfo() {
        if (isIdentified()){
            float dropChance = 100f*(1/dropsToUpgrade);
            if (dropsToUpgrade <= 0) {//Display 100% chance if the value goes negative, as a scroll is guaranteed
                dropChance = 100f;
            }
            return Messages.get(this, "stats", new DecimalFormat("#.##").format(dropChance));
        } else {
            return Messages.get(this, "typical_stats");
        }
    }

    private static final String TRIES_TO_DROP = "tries_to_drop";
    private static final String DROPS_TO_RARE = "drops_to_rare";
    private static final String DROPS_TO_UPGRADE = "drops_to_upgrade";
    private static final String RETURN_DEPTH = "return_depth";
    private static final String RETURN_POS = "return_pos";
    private static final String TYPE = "type";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRIES_TO_DROP, triesToDrop);
        bundle.put(DROPS_TO_RARE, dropsToRare);
        bundle.put(DROPS_TO_UPGRADE, dropsToUpgrade);
        bundle.put( RETURN_DEPTH, returnDepth );
        bundle.put( RETURN_POS, returnPos );
        bundle.put( TYPE, type );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        triesToDrop = bundle.getFloat(TRIES_TO_DROP);
        dropsToRare = bundle.getInt(DROPS_TO_RARE);
        dropsToUpgrade = bundle.getInt(DROPS_TO_UPGRADE);
        returnDepth = bundle.getInt( RETURN_DEPTH );
        returnPos = bundle.getInt( RETURN_POS );
        type = bundle.getInt(TYPE);
    }

    @Override
    protected PowerBuff passiveBuff() {
        return new LuckyBuff();
    }

    public static Item tryForBonusDrop(Char target){

        HashSet<LuckyBadge.LuckyBuff> buffs = target.buffs(LuckyBadge.LuckyBuff.class);
        if (buffs == null) {
            return null;
        }
        Item item;
        if ((dropsToUpgrade < 1) || (Random.Int( (int) dropsToUpgrade) == 0)) {
                item = new ScrollOfUpgrade();
                dropsToUpgrade += dropsIncreases;
            } else {
            if (Random.Int(10) == 0 & !latestDropWasRare) {// 1/10 chance
                Item i;
                do {
                    i = genRareDrop();
                } while (Challenges.isItemBlocked(i));
                item = i;
                latestDropWasRare = true;
                dropsToUpgrade -= 3;
            } else {
                Item i;
                do {
                    i = genStandardDrop();
                } while (Challenges.isItemBlocked(i));
                item = i;
                dropsToUpgrade--;
            }

        }

        return item;
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
            } while (scroll == null || scroll instanceof ScrollOfUpgrade);
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
            } while (scroll == null);
            return scroll;
        } else { //50% chance
            return Random.Int(2) == 0 ? new SmallRation() : new MysteryMeat();
        }
    }

    public static Item genRareDrop(){
        float roll = Random.Float();
        if (roll < 0.3f){ //30% chance
            Item result = new Gold().random();
            result.quantity(Math.round(result.quantity() * Random.NormalFloat(3f, 6f)));
            return result;
        } else if (roll < 0.6f){ //30% chance
            return genHighValueConsumable();
        } else if (roll < 0.9f){ //30% chance
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

    private class LuckyBuff extends PowerBuff {
        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }
    }
}
