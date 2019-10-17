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

package com.shatteredpixel.cursedpixeldungeon.items.allies;

import com.shatteredpixel.cursedpixeldungeon.Assets;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.cursedpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.KindofMisc;
import com.shatteredpixel.cursedpixeldungeon.items.armor.Armor;
import com.shatteredpixel.cursedpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.cursedpixeldungeon.sprites.PoisonDragonSprite;
import com.shatteredpixel.cursedpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DragonCrystal extends KindofMisc {

	{
		image = ItemSpriteSheet.ADORNEDDRAGONCRYSTAL;



		defaultAction = AC_SUMMON;
	}

	@Override
	public int price() {
		return 75*level();
	}

	@Override
	public int UpgradeLimit() {
		if (Dungeon.hero.heroClass == HeroClass.PRIESTESS) {
			return 20;
		} else {
			return super.UpgradeLimit();
		}
	}

	int levelCap = 10;

	int charge = 100;
	int chargeCap = 100;
	float partialCharge = 0;

	protected Buff passiveBuff;
	protected Buff activeBuff;

	private boolean talkedTo = false;
	private boolean firstSummon = false;
	
	private Dragon Dragon = null;
	private int DragonID = 0;
	
	private MeleeWeapon weapon = null;
	private Armor armor = null;

	public int droppedPetals = 0;

	public static final String AC_SUMMON = "SUMMON";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge == chargeCap && !cursed) {
			actions.add(AC_SUMMON);
		}

		return actions;
	}

	public DragonCrystal.Dragon GetDragonTypeToSpawn() {//This ensures that subclasses spawn their own type of Dragon, not the general one.
		return new DragonCrystal.Dragon(this);
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals(AC_SUMMON)) {
			if (Dragon != null)         GLog.i( Messages.get(this, "spawned") );
			else if (!isEquipped( hero ))   GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge != chargeCap)   GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else {
				ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {
					Dragon = GetDragonTypeToSpawn();
					DragonID = Dragon.id();
					Dragon.pos = Random.element(spawnPoints);

					GameScene.add(Dragon, 1f);
					CellEmitter.get(Dragon.pos).start( ShaftParticle.FACTORY, 0.3f, 4 );
					CellEmitter.get(Dragon.pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );

					hero.spend(1f);
					hero.busy();
					hero.sprite.operate(hero.pos);
					charge = 0;
					updateQuickslot();

				} else
					GLog.i( Messages.get(this, "no_space") );
			}

		}
	}
	
	public int ghostStrength(){
		return 13 + level()/2;
	}

	@Override
	public String desc() {
		
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (Dragon != null){

				desc += "\n\n" + this.Dragon.description();
			} else
				desc += "\n\n" + Messages.get(this, "desc_no_dragon");
		}

		return desc;
	}

	@Override
	public boolean doEquip( final Hero hero ) {

		if (super.doEquip(hero)) {

			identify();
			return true;

		} else {

			return false;

		}

	}

	public void activate( Char ch ) {
		passiveBuff = passiveBuff();
		passiveBuff.attachTo(ch);
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			passiveBuff.detach();
			passiveBuff = null;

			if (activeBuff != null){
				activeBuff.detach();
				activeBuff = null;
			}

			return true;

		} else {

			return false;

		}
	}

	public class CrystalBuff extends Buff {

		public int itemLevel() {
			return level();
		}

		public boolean isCursed() {
			return cursed;
		}

	}

	protected CrystalBuff passiveBuff() {
		return new recharge();
	}

	public class recharge extends CrystalBuff {
		@Override
		public boolean act() {
			spend( TICK );

            if (Dragon == null && DragonID != 0){
                Actor a = Actor.findById(DragonID);
                if (a != null){
                    Dragon = (Dragon) a;
                } else {
                    DragonID = 0;
                }
            }

            //rose does not charge while ghost hero is alive
            if (Dragon != null){
                return true;
            }

			LockedFloor lock = target.buff(LockedFloor.class);
			if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())&& Dragon == null) {
				partialCharge += 0.1;

				if (partialCharge >= 1) {
					partialCharge --;
					charge ++;

					if (charge == chargeCap){
						partialCharge = 0;
					}
				}
			}

			updateQuickslot();



			return true;
		}
	}
	
	@Override
	public Item upgrade() {
		if (Dragon != null){
			Dragon.updateCrystal();
		}

		return super.upgrade();
	}

	@Override
	public String status() {

		//if the artifact isn't IDed, or is cursed, don't display anything
		if (!isIdentified() || cursed){
			return null;
		} else {
			return Messages.format("%d%%", charge);
		}
	}

	private static final String TALKEDTO =      "talkedto";
	private static final String FIRSTSUMMON =   "firstsummon";
	private static final String GHOSTID =       "DragonID";
	private static final String PETALS =        "petals";
	
	private static final String WEAPON =        "weapon";
	private static final String ARMOR =         "armor";
	private static final String CHARGE  =         "charge";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);

		bundle.put( TALKEDTO, talkedTo );
		bundle.put( FIRSTSUMMON, firstSummon );
		bundle.put( GHOSTID, DragonID);
		bundle.put( PETALS, droppedPetals );
		bundle.put( CHARGE, charge);
		
		if (weapon != null) bundle.put( WEAPON, weapon );
		if (armor != null)  bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		talkedTo = bundle.getBoolean( TALKEDTO );
		firstSummon = bundle.getBoolean( FIRSTSUMMON );
		DragonID = bundle.getInt( GHOSTID );
		droppedPetals = bundle.getInt( PETALS );
		charge = bundle.getInt(CHARGE);

		if (bundle.contains(WEAPON)) weapon = (MeleeWeapon)bundle.get( WEAPON );
		if (bundle.contains(ARMOR))  armor = (Armor)bundle.get( ARMOR );
	}

	public static class Dragon extends NPC {
		int regenDelay = 0;
		boolean PassiveRegen = true;

		{
			spriteClass = PoisonDragonSprite.class;

			flying = true;

			alignment = Alignment.ALLY;
			
			WANDERING = new Following();
			
			state = HUNTING;
			
			//before other mobs
			actPriority = MOB_PRIO + 1;
		}

		public DragonCrystal Crystal = null;

		public Dragon(){
			super();
		}

		public int min() {
			return  1 + Crystal.level();    //level scaling
		}

		public int max() {
			return  8 + Crystal.level() * 3;   //level scaling
		}

		@Override
		public String description() {
			return super.description() + "\n\n" + Messages.get(DragonCrystal.class,"stats_desc", HT, min(),max(),attackSkill(this),defenseSkill(this));
		}

		public Dragon(DragonCrystal rose){
			super();
			this.Crystal = rose;
			updateCrystal();
			HP = HT;
		}

		public Class CrystalType() {
			return DragonCrystal.class;
		}
		
		private void updateCrystal(){
			if (Crystal == null) {
				Crystal = (DragonCrystal) Dungeon.hero.belongings.getItem(CrystalType());
			}

			if (Crystal == null) return;
			HT = HPCalc();
		}

		public int HPCalc() {
			return  30 + 10* Crystal.level();
		}

		public void sayDefeated(){
		}

		@Override
		protected boolean act() {
			updateCrystal();
			if (Crystal == null || !Crystal.isEquipped(Dungeon.hero)){
				damage(1, this);
			} else if (PassiveRegen) {
				regenDelay += 1;
				if (regenDelay > 9) {
					regenDelay = 0;
					HP += Math.min(HT / 10, this.missingHP());
				}
			}
			
			if (!isAlive())
				return true;
			if (!Dungeon.hero.isAlive()){
				sprite.die();
				destroy();
				return true;
			}
			return super.act();
		}
		
		@Override
		protected Char chooseEnemy() {
			Char enemy = super.chooseEnemy();
			
			//will never attack something far from the player
			if (enemy != null && Dungeon.level.mobs.contains(enemy)
					&& Dungeon.level.distance(enemy.pos, Dungeon.hero.pos) <= 8) {
				return enemy;
			} else if (!(state instanceof Following)){//If not following, should attack whatever enemy is present.
				return enemy;
			} else {
				return null;
			}
		}

		@Override
		public int attackSkill( Char target ) {
			return Dungeon.hero.lvl + 9;
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(min(), max());
		}//base of 1-8 (Worn Shortsword), scales by 1-3 (Sword)

		@Override
		public int defenseSkill(Char enemy) {
			return 5 + Dungeon.hero.lvl;
		}//2 Evasion, scaling at 2/level

		@Override
		public int drRoll() {
			return Random.NormalIntRange(1 + Crystal.level() * 2, 2 + Crystal.level() * 5);
		}//base of 1-2 (Cloth Armour), scales by 2-5 (Plate Armour)

		@Override
		public boolean interact() {
			updateCrystal();
			if (Dungeon.level.passable[pos] || Dungeon.hero.flying) {
				int curPos = pos;

				moveSprite( pos, Dungeon.hero.pos );
				move( Dungeon.hero.pos );

				Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
				Dungeon.hero.move( curPos );

				Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
				Dungeon.hero.busy();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void die(Object cause) {
			sayDefeated();
			super.die(cause);
		}

		@Override
		public void destroy() {
			updateCrystal();
			if (Crystal != null) {
				Crystal.Dragon = null;
				Crystal.DragonID = -1;
			}
			super.destroy();
		}
		
		{
			immunities.add( ToxicGas.class );
			immunities.add( CorrosiveGas.class );
			immunities.add( Burning.class );
			immunities.add( ScrollOfRetribution.class );
			immunities.add( ScrollOfPsionicBlast.class );
			immunities.add( Corruption.class );
		}
		
		private class Wandering extends Mob.Wandering {
			
			@Override
			public boolean act( boolean enemyInFOV, boolean justAlerted ) {
				if ( enemyInFOV ) {
					
					enemySeen = true;
					
					notice();
					alerted = true;
					state = HUNTING;
					target = enemy.pos;
					
				} else {
					
					enemySeen = false;
					
					int oldPos = pos;
					//always move towards the hero when wandering
					if (getCloser( target = Dungeon.hero.pos )) {
						//moves 2 tiles at a time when returning to the hero from a distance
						if (!Dungeon.level.adjacent(Dungeon.hero.pos, pos)){
							getCloser( target = Dungeon.hero.pos );
						}
						spend( 1 / speed() );
						return moveSprite( oldPos, pos );
					} else {
						spend( TICK );
					}
					
				}
				return true;
			}
			
		}

	}
}
