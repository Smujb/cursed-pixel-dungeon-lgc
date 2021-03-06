package com.shatteredpixel.cursedpixeldungeon.items.weapon.melee;

import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.cursedpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.cursedpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.cursedpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.cursedpixeldungeon.items.allies.DragonCrystal;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.cursedpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.cursedpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class InscribedKnife extends MeleeWeapon {

    {
        image = ItemSpriteSheet.INSCRIBED_KINFE;

        tier = 1;

        bones = false;

        defaultAction = AC_CURSE;

        usesTargeting = true;

        unique = true;
        bonusSubClass = HeroSubClass.MEDIC;
    }
    private float charge = 0;
    private int maxCharge = 40;

    private static final String AC_CURSE = "CURSE";
    private static final String AC_SUMMON = "SUMMON";
    public static final String CHARGE = "charge";
    private static final int CURSE_AMT = 30;
    private static final int SUMMON_AMT = 40;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(CHARGE, charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        charge = bundle.getInt(CHARGE);
        super.restoreFromBundle(bundle);
    }

    private void Charge(float amount) {
        charge += amount;
        charge = Math.min(charge,maxCharge);
    }

    private void loseCharge(float amount) {
        charge -= amount;
        charge = Math.max(charge,0);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.MEDIC) {
            ((Hero)attacker).sprite.centerEmitter().burst( EnergyParticle.FACTORY, 15 );
            Belongings b = ((Hero) attacker).belongings;
            if (b.misc1 instanceof DragonCrystal) {
                ((DragonCrystal)b.misc1).Charge(1);
            }
            if (b.misc2 instanceof DragonCrystal) {
                ((DragonCrystal)b.misc2).Charge(1);
            }
            if (b.misc3 instanceof DragonCrystal) {
                ((DragonCrystal)b.misc3).Charge(1);
            }
            if (b.misc4 instanceof DragonCrystal) {
                ((DragonCrystal)b.misc4).Charge(1);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_CURSE);
        actions.add(AC_SUMMON);
        return actions;
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        if (this.isEquipped(Dungeon.hero)) {
            super.onHeroGainExp(levelPercent, hero);
            Charge(1f + 0.1f*level());//Charge gained scales slowly with level
        }
    }

    @Override
    public String desc() {
        return super.desc() + "\n\n" + Messages.get(this, "charge_desc", (int)charge, maxCharge);
    }

    protected static CellSelector.Listener curse = new  CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            Char enemy;
            KindOfWeapon Knife = Dungeon.hero.belongings.weapon;
            if (target != null && Knife instanceof InscribedKnife) {
                int cell = target;
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));
                enemy = Actor.findChar(cell);
                if (enemy != null) {
                    Buff.affect(enemy, Doom.class);
                    enemy.sprite.emitter().burst(ShadowParticle.CURSE, 6);
                    GLog.i( Messages.get(InscribedKnife.class, "curse_message") );
                    ((InscribedKnife)Knife).loseCharge(InscribedKnife.CURSE_AMT);
                } else {
                    GLog.w( Messages.get(InscribedKnife.class, "curse_fail") );
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(this, "prompt_curse");
        }
    };

    private static CellSelector.Listener summon = new  CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            KindOfWeapon Knife = Dungeon.hero.belongings.weapon;
            if (target != null && Knife instanceof InscribedKnife) {
                int cell = target;
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));
                if (Wraith.spawnAt(cell) != null) {
                    GLog.i( Messages.get(InscribedKnife.class, "summon_message") );
                    ((InscribedKnife)Knife).loseCharge(InscribedKnife.SUMMON_AMT);
                } else {
                    GLog.w( Messages.get(InscribedKnife.class, "summon_fail") );
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(this, "prompt_summon");
        }
    };

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_CURSE) && isEquipped(hero)) {
            if (charge >= CURSE_AMT) {
                GameScene.selectCell(curse);
            } else {
                GLog.i( Messages.get(this, "no_charge") );
            }
        } else if (action.equals(AC_SUMMON) && isEquipped(hero)) {
            if (charge >= SUMMON_AMT) {
                GameScene.selectCell(summon);
            } else {
                GLog.i( Messages.get(this, "no_charge") );
            }
        }
    }
}
