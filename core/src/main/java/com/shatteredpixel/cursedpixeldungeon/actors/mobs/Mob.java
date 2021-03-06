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

package com.shatteredpixel.cursedpixeldungeon.actors.mobs;

import com.shatteredpixel.cursedpixeldungeon.Badges;
import com.shatteredpixel.cursedpixeldungeon.Challenges;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.cursedpixeldungeon.Statistics;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.EvilGas;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.LuckyBadgeBuff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Resurrection;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.cursedpixeldungeon.effects.Flare;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.effects.Surprise;
import com.shatteredpixel.cursedpixeldungeon.effects.Wound;
import com.shatteredpixel.cursedpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.cursedpixeldungeon.items.Generator;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.Soul;
import com.shatteredpixel.cursedpixeldungeon.items.allies.DragonCrystal;
import com.shatteredpixel.cursedpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.cursedpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.cursedpixeldungeon.items.powers.LuckyBadge;
import com.shatteredpixel.cursedpixeldungeon.items.rings.Ring;
import com.shatteredpixel.cursedpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.cursedpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.cursedpixeldungeon.items.wands.Wand;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.cursedpixeldungeon.levels.Level;
import com.shatteredpixel.cursedpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.cursedpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static com.shatteredpixel.cursedpixeldungeon.actors.Char.Alignment.ALLY;

public abstract class Mob extends Char {

	{
		name = Messages.get(this, "name");
		actPriority = MOB_PRIO;
		
		alignment = Alignment.ENEMY;
		immunities.add(EvilGas.class);
	}
	private static final String	TXT_DIED	= "You hear something died in the distance";
	
	protected static final String TXT_NOTICE1	= "?!";
	protected static final String TXT_RAGE		= "#$%^";
	protected static final String TXT_EXP		= "%+dEXP";

	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState state = SLEEPING;
	
	public Class<? extends CharSprite> spriteClass;
	
	protected int target = -1;
	
	protected int defenseSkill = 0;
	
	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL;
	
	protected Char enemy;
	protected boolean enemySeen;
	protected boolean alerted = false;

	protected static final float TIME_TO_WAKE_UP = 1f;
	
	private static final String STATE	= "state";
	private static final String SEEN	= "seen";
	private static final String TARGET	= "target";

	public float multiplier() {
		return (Dungeon.hero.lvl/(float)Dungeon.depth);
	}

	public int missingHP() {
		return this.HT - this.HP;
	}
	public AiState defaultState() {return WANDERING;}
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );

		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}
		bundle.put( SEEN, enemySeen );
		bundle.put( TARGET, target );
	}

	public void wanderRandom() {
		WANDERING = new Wandering();
		state = HUNTING;
	}

	public void followHero() {
		WANDERING = new Following();
		state = HUNTING;
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );

		String state = bundle.getString( STATE );
		if (state.equals( Sleeping.TAG )) {
			this.state = SLEEPING;
		} else if (state.equals( Wandering.TAG )) {
			this.state = WANDERING;
		} else if (state.equals( Hunting.TAG )) {
			this.state = HUNTING;
		} else if (state.equals( Fleeing.TAG )) {
			this.state = FLEEING;
		} else if (state.equals( Passive.TAG )) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean( SEEN );
		target = bundle.getInt( TARGET );
	}
	
	public CharSprite sprite() {
		CharSprite sprite = null;
		try {
			sprite = spriteClass.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return sprite;
	}
	
	@Override
	protected boolean act() {
		
		super.act();
		
		boolean justAlerted = alerted;
		alerted = false;
		
		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}
		
		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}
		
		enemy = chooseEnemy();
		
		boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

		return state.act( enemyInFOV, justAlerted );
	}
	
	protected Char chooseEnemy() {

		Terror terror = buff( Terror.class );
		if (terror != null) {
			Char source = (Char)Actor.findById( terror.object );
			if (source != null) {
				return source;
			}
		}
		
		//if we are an enemy, and have no target or current target isn't affected by aggression
		//then auto-prioritize a target that is affected by aggression, even another enemy
		if (alignment == Alignment.ENEMY
				&& (enemy == null || enemy.buff(StoneOfAggression.Aggression.class) == null)) {
			for (Char ch : Actor.chars()) {
				if (ch != this && fieldOfView[ch.pos] &&
						ch.buff(StoneOfAggression.Aggression.class) != null) {
					return ch;
				}
			}
		}

		//find a new enemy if..
		boolean newEnemy = false;
		//we have no enemy, or the current one is dead
		if ( enemy == null || !enemy.isAlive() || state == WANDERING)
			newEnemy = true;
		//We are an ally, and current enemy is another ally.
		else if (alignment == ALLY && enemy.alignment == ALLY)
			newEnemy = true;
		//We are amoked and current enemy is the hero
		else if (buff( Amok.class ) != null && enemy == Dungeon.hero)
			newEnemy = true;
		//We are charmed and current enemy is what charmed us
		else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id())
			newEnemy = true;

		if ( newEnemy ) {

			HashSet<Char> enemies = new HashSet<>();

			//if the mob is amoked...
			if ( buff(Amok.class) != null) {
				//try to find an enemy mob to attack first.
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY && mob != this && fieldOfView[mob.pos])
							enemies.add(mob);
				
				if (enemies.isEmpty()) {
					//try to find ally mobs to attack second.
					for (Mob mob : Dungeon.level.mobs)
						if (mob.alignment == ALLY && mob != this && fieldOfView[mob.pos])
							enemies.add(mob);
					
					if (enemies.isEmpty()) {
						//try to find the hero third
						if (fieldOfView[Dungeon.hero.pos]) {
							enemies.add(Dungeon.hero);
						}
					}
				}
				
			//if the mob is an ally...
			} else if ( alignment == ALLY ) {
				//look for hostile mobs that are not passive to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY
							&& fieldOfView[mob.pos]
							&& mob.state != mob.PASSIVE)
						enemies.add(mob);
				
			//if the mob is an enemy...
			} else if (alignment == Alignment.ENEMY) {
				//look for ally mobs to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == ALLY && fieldOfView[mob.pos])
						enemies.add(mob);

				//and look for the hero
				if (fieldOfView[Dungeon.hero.pos]) {
					enemies.add(Dungeon.hero);
				}

			} else if (alignment == Alignment.WRAITH) {
				for (Mob mob : Dungeon.level.mobs){
					if (!(mob instanceof Wraith)) {
						enemies.add(mob);
					}
				}
				if (fieldOfView[Dungeon.hero.pos]) {
					enemies.add(Dungeon.hero);
				}
			}
			
			Charm charm = buff( Charm.class );
			if (charm != null){
				Char source = (Char)Actor.findById( charm.object );
				if (source != null && enemies.contains(source) && enemies.size() > 1){
					enemies.remove(source);
				}
			}
			
			//neutral characters in particular do not choose enemies.
			if (enemies.isEmpty()){
				return null;
			} else {
				//go after the closest potential enemy, preferring the hero if two are equidistant
				Char closest = null;
				for (Char curr : enemies){
					if (closest == null
							|| Dungeon.level.distance(pos, curr.pos) < Dungeon.level.distance(pos, closest.pos)
							|| Dungeon.level.distance(pos, curr.pos) == Dungeon.level.distance(pos, closest.pos) && curr == Dungeon.hero){
						closest = curr;
					}
				}
				return closest;
			}

		} else
			return enemy;
	}

	protected boolean moveSprite( int from, int to ) {

		if (sprite.isVisible() && (Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to])) {
			sprite.move( from, to );
			return true;
		} else {
			sprite.turnTo(from, to);
			sprite.place( to );
			return true;
		}
	}
	
	@Override
	public void add( Buff buff ) {
		super.add( buff );
		if (buff instanceof Amok || buff instanceof Corruption) {
			state = HUNTING;
		} else if (buff instanceof Terror) {
			state = FLEEING;
		} else if (buff instanceof Sleep) {
			state = SLEEPING;
			postpone( Sleep.SWS );
		}
	}
	
	@Override
	public void remove( Buff buff ) {
		super.remove( buff );
		if (buff instanceof Terror) {
			sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "rage") );
			state = HUNTING;
		}
	}

	public boolean following(Char follow) {
		if (alignment == follow.alignment) {
			Char targetChar = Actor.findChar(this.target);
			return targetChar == follow;
		}
		return false;
	}
	
	protected boolean canAttack( Char enemy ) {
		return Dungeon.level.adjacent( pos, enemy.pos );
	}
	
	protected boolean getCloser( int target ) {
		
		if (rooted || target == pos) {
			return false;
		}

		int step = -1;

		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null && Dungeon.level.passable[target]) {
				step = target;
			}

		} else {

			boolean newPath = false;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's extremely inefficient and checking again may result in a much better path
			if (path == null || path.isEmpty()
					|| !Dungeon.level.adjacent(pos, path.getFirst())
					|| path.size() > 2*Dungeon.level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (Dungeon.level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (Dungeon.level.adjacent(target, pos)) {
							path.add(target);
							//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else if (!path.isEmpty()) {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

							//if the new target is closer/same, need to modify end of path
						} else if (Dungeon.level.adjacent(target, path.getLast())) {
							path.add(target);

							//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}


			if (!newPath) {
				//looks ahead for path validity, up to length-1 or 4, but always at least 1.
				int lookAhead = (int)GameMath.gate(1, path.size()-1, 4);
				for (int i = 0; i < lookAhead; i++) {
					int cell = path.get(i);
					if (!Dungeon.level.passable[cell] || ( fieldOfView[cell] && Actor.findChar(cell) != null)) {
						newPath = true;
						break;
					}
				}
			}

			if (newPath) {
				path = Dungeon.findPath(this, pos, target,
						Dungeon.level.passable,
						fieldOfView);
			}

			//if hunting something, don't follow a path that is extremely inefficient
			//FIXME this is fairly brittle, primarily it assumes that hunting mobs can't see through
			// permanent terrain, such that if their path is inefficient it's always because
			// of a temporary blockage, and therefore waiting for it to clear is the best option.
			if (path == null ||
					(state == HUNTING && path.size() > Math.max(9, 2*Dungeon.level.distance(pos, target)))) {
				return false;
			}

			step = path.removeFirst();
		}
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean getFurther( int target ) {
		if (rooted || target == pos) {
			return false;
		}
		
		int step = Dungeon.flee( this, pos, target,
			Dungeon.level.passable,
			fieldOfView );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
				|| Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null)
			sprite.add( CharSprite.State.PARALYSED );
	}
	
	protected float attackDelay() {
		float delay = 1f;
		if ( buff(Adrenaline.class) != null) delay /= 1.5f;
		return delay;
	}
	
	protected boolean doAttack( Char enemy ) {
		
		boolean visible = Dungeon.level.heroFOV[pos];
		
		if (visible) {
			sprite.attack( enemy.pos );
		} else {
			attack( enemy );
		}
				
		spend( attackDelay() );
		
		return !visible;
	}
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		super.onAttackComplete();
	}
	
	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (buff(Weakness.class) != null){
			damage *= 0.67f;
		}
		if (enemy instanceof Mob && ((Mob)enemy).alignment == Alignment.ENEMY && Dungeon.hero.subClass == HeroSubClass.MEDIC) ((Mob)enemy).aggro(this);
		return damage;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		boolean seen = (enemySeen && enemy.invisible == 0);
		if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack()) seen = true;
		if ( seen
				&& paralysed == 0
				&& !(alignment == ALLY && enemy == Dungeon.hero)) {
			return this.defenseSkill;
		} else {
			return 0;
		}
	}
	
	protected boolean hitWithRanged = false;
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		
		if (enemy instanceof Hero && ((Hero) enemy).belongings.weapon instanceof MissileWeapon){
			hitWithRanged = true;
		}
		
		if ((!enemySeen || enemy.invisible > 0)
				&& enemy == Dungeon.hero && Dungeon.hero.canSurpriseAttack()) {
			Statistics.sneakAttacks++;
			Badges.validateRogueUnlock();
			if (enemy.buff(Preparation.class) != null) {
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//if attacked by something else than current target, and that thing is closer, switch targets
		if (this.enemy == null
				|| (enemy != this.enemy && (Dungeon.level.distance(pos, enemy.pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
			aggro(enemy);
			target = enemy.pos;
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP);
			Buff.affect(Dungeon.hero, Hunger.class).satisfy(restoration);
			Dungeon.hero.HP = (int)Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP+(restoration*0.33f)));
			Dungeon.hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
		}

		return damage;
	}

	public boolean surprisedBy( Char enemy ){
		return !enemySeen && enemy == Dungeon.hero;
	}

	public void aggro( Char ch ) {
		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
	}
	
	public boolean isTargeting( Char ch){
		return enemy == ch;
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == SLEEPING) {
			state = defaultState();
		}
		if (state != HUNTING) {
			alerted = true;
		}
		if (Dungeon.isChallenged(Challenges.EVIL_GAS) | this instanceof Kupua) {
			int amount = Math.min( (int) (100*((float)dmg/(float)HT)), 100);
			GameScene.add(Blob.seed(this.pos, amount, EvilGas.class).setStrength(1+Dungeon.depth/4));
		}
		super.damage( dmg, src );
	}
	
	
	@Override
	public void destroy() {
		
		super.destroy();
		
		Dungeon.level.mobs.remove( this );
		
		if (Dungeon.hero.isAlive()) {
			
			if (alignment == Alignment.ENEMY) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;
				
				int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;
				if (exp > 0) {
					Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
				}
				Dungeon.hero.earnExp(exp, getClass());
			}
		}
	}
	
	@Override
	public void die( Object cause ) {

		if (buff(Resurrection.class) != null && Dungeon.hero.HP > this.HT/2 && Dungeon.hero.subClass == HeroSubClass.NECROMANCER) {
			HP = HT;
			this.sprite.emitter().burst(ShadowParticle.CURSE, 6);
			new Flare(8, 16).color(0xFFFF66, true).show(sprite, 2f);
			Dungeon.hero.damage(this.HT/2, this);
			GLog.p(Messages.get(this, "resurrect", name));
			return;
		}
		
		if (hitWithRanged){
			Statistics.thrownAssists++;
			Badges.validateHuntressUnlock();
		}
		
		if (cause == Chasm.class){
			//50% chance to round up, 50% to round down
			if (EXP % 2 == 1) EXP += Random.Int(2);
			EXP /= 2;
		}

		if (alignment == Alignment.ENEMY){
			rollToDropLoot();
		}
		
		if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
			GLog.i( Messages.get(this, "died") );
		}
		if (cause instanceof DragonCrystal.Dragon) {
			Statistics.allyAssists += 1;
			Badges.validatePriestessUnlock();
		}
		super.die( cause );
		boolean chance;
		if (cause instanceof Wand) {
			chance = Random.Int(3) == 0;
		} else {
			chance = Random.Int(5) == 0;
		}
		if (Dungeon.hero.subClass == HeroSubClass.NECROMANCER && chance && (cause instanceof Item | cause instanceof Hero)) {//Must be killed by the hero (by an item)
			if (Wraith.spawnAt( pos ) != null) {
				Dungeon.hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
			}
		}
	}

	public static void spawnAround4(Class<? extends Mob> type, int pos) {
		spawnAtList(type, pos, PathFinder.NEIGHBOURS4);
	}

	public static void spawnAround8(Class<? extends Mob> type, int pos) {
		spawnAtList(type, pos, PathFinder.NEIGHBOURS8);
	}

	public static void spawnAround9(Class<? extends Mob> type, int pos) {
		spawnAtList(type, pos, PathFinder.NEIGHBOURS9);
	}

	public static void spawnAtList(Class<? extends Mob> type, int pos, int[] relativePositions) {
		for (int n : relativePositions) {
			int cell = pos + n;
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
				Mob.spawnAt(type, cell);
			}
		}
	}

	public static Mob spawnAt(Class<? extends Mob> type, int pos) {
		if (Dungeon.level.passable[pos] && Actor.findChar( pos ) == null) {

			Mob mob = null;
			do {
				try {
					mob = type.newInstance();
				} catch (IllegalAccessException e) {
					ShatteredPixelDungeon.reportException(e);
				} catch (InstantiationException e) {
					ShatteredPixelDungeon.reportException(e);
				}
			} while (mob == null);
			mob.pos = pos;
			mob.state = mob.HUNTING;
			GameScene.add( mob, 1f );

			return mob;
		} else {
			return null;
		}
	}
	
	public void rollToDropLoot(){
		if (Dungeon.hero.lvl > maxLvl + 5) return;
		
		float lootChance = this.lootChance;
		lootChance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
		
		if (Random.Float() < lootChance) {
			Item loot = createLoot();
			if (loot != null) {
				Dungeon.level.drop(loot, pos).sprite.drop();
			}
		}
		
		//ring of wealth logic
		if (Ring.getBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
			int rolls = 1;
			if (properties.contains(Property.BOSS)) rolls = 15;
			else if (properties.contains(Property.MINIBOSS)) rolls = 5;
			ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
			if (bonus != null && !bonus.isEmpty()) {
				for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
				if (RingOfWealth.latestDropWasRare){
					new Flare(8, 48).color(0xAA00FF, true).show(sprite, 3f);
					RingOfWealth.latestDropWasRare = false;
				} else {
					new Flare(8, 24).color(0xFFFFFF, true).show(sprite, 3f);
				}
			}
		}
		
		//lucky enchant logic
		if (Dungeon.hero.lvl <= maxLvl && buff(Lucky.LuckProc.class) != null){
			new Flare(8, 24).color(0x00FF00, true).show(sprite, 3f);
			Dungeon.level.drop(LuckyBadge.tryForBonusDrop(Dungeon.hero), pos).sprite.drop();
		}

		//
		LuckyBadgeBuff buff = Dungeon.hero.buff(LuckyBadgeBuff.class);
		if (buff != null) {
			for (int i = 0; i < buff.lootPerMob(); i++) {
				Item luckybadgedrop = LuckyBadge.tryForBonusDrop(Dungeon.hero);
				if (luckybadgedrop != null) {
					Dungeon.level.drop(luckybadgedrop, Dungeon.hero.pos).sprite.drop();
				}
			}
		}
		//Cultist subclass logic
		if (Dungeon.hero.subClass == HeroSubClass.CULTIST && Random.Int(3) == 0 && !(this.properties().contains(Property.BOSS) || this.properties().contains(Property.INORGANIC))) {
			Dungeon.level.drop(new Soul().setMob(this), pos).sprite.drop();
		}
	}
	
	protected Object loot = null;
	protected float lootChance = 0;
	
	@SuppressWarnings("unchecked")
	protected Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.random( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {
		
		notice();
		
		if (state != HUNTING) {
			state = defaultState();
		}
		target = cell;
	}
	
	public String description() {
		return Messages.get(this, "desc");
	}
	
	public void notice() {
		sprite.showAlert();
	}
	
	public void yell( String str ) {
		GLog.n( "%s: \"%s\" ", Messages.titleCase(name), str );
	}

	//returns true when a mob sees the hero, and is currently targeting them.
	public boolean focusingHero() {
		return enemySeen && (target == Dungeon.hero.pos);
	}

	public interface AiState {
		boolean act( boolean enemyInFOV, boolean justAlerted );
	}

	protected class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && Random.Float( distance( enemy ) + enemy.stealth() + (enemy.flying ? 2 : 0) ) < 1) {

				enemySeen = true;

				notice();
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
						if (Dungeon.level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
							mob.beckon( target );
						}
					}
				}

				spend( TIME_TO_WAKE_UP );

			} else {

				enemySeen = false;

				spend( TICK );

			}
			return true;
		}
	}


	public class Following extends Wandering implements AiState {

		private Char toFollow(Char start) {
			Char toFollow = start;
			boolean[] passable = Dungeon.level.passable.clone();
			PathFinder.buildDistanceMap(pos, passable, Integer.MAX_VALUE);//No limit on distance
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.alignment == alignment && PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] && mob.following(toFollow)) {
					toFollow = toFollow(mob);//If we find a mob already following the target, ensure there is not a mob already following them. This allows even massive chains of allies to traverse corridors correctly.
				}
			}
			return toFollow;
		}

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
				Char toFollow = toFollow(Dungeon.hero);
				int oldPos = pos;
				//always move towards the target when wandering
				if (getCloser( target = toFollow.pos )) {
					if (!Dungeon.level.adjacent(toFollow.pos, pos) && Actor.findChar(pos) == null) {
						getCloser( target = toFollow.pos );
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

	public class Wandering implements AiState {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Float( distance( enemy ) / 2f + enemy.stealth() ) < 1)) {

				enemySeen = true;

				notice();
				alerted = true;
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
						if (Dungeon.level.distance(pos, mob.pos) <= 8 && mob.state != mob.HUNTING) {
							mob.beckon( target );
						}
					}
				}

			} else {

				enemySeen = false;

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					target = Dungeon.level.randomDestination();
					spend( TICK );
				}

			}
			return true;
		}
	}

	protected class Hunting implements AiState {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					state = defaultState();
					target = Dungeon.level.randomDestination();
					return true;
				}
				
				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {
					spend( TICK );
					if (!enemyInFOV) {
						sprite.showLost();
						state = defaultState();
						target = Dungeon.level.randomDestination();
					}
					return true;
				}
			}
		}
	}

	protected class Fleeing implements AiState {

		public static final String TAG	= "FLEEING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			//loses target when 0-dist rolls a 6 or greater.
			if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6){
				target = -1;
			
			//if enemy isn't in FOV, keep running from their previous position.
			} else if (enemyInFOV) {
				target = enemy.pos;
			}

			int oldPos = pos;
			if (target != -1 && getFurther( target )) {

				spend( 1 / speed() );
				return moveSprite( oldPos, pos );

			} else {

				spend( TICK );
				nowhereToRun();

				return true;
			}
		}

		protected void nowhereToRun() {
		}
	}
	private static ArrayList<Mob> heldAllies = new ArrayList<>();

	public static void holdAllies( Level level ){
		heldAllies.clear();
		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			//preserve the ghost no matter where they are
			if (mob instanceof DriedRose.GhostHero) {
				level.mobs.remove( mob );
				heldAllies.add(mob);

				//preserve intelligent allies if they are near the hero
			} else if (mob.alignment == ALLY
					&& Dungeon.level.distance(Dungeon.hero.pos, mob.pos) <= 3){
				level.mobs.remove( mob );
				heldAllies.add(mob);
			}
		}
	}

	public static void restoreAllies( Level level, int pos ){
		if (!heldAllies.isEmpty()){

			ArrayList<Integer> candidatePositions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8) {
				if (!Dungeon.level.solid[i+pos] && level.findMob(i+pos) == null){
					candidatePositions.add(i+pos);
				}
			}
			Collections.shuffle(candidatePositions);

			for (Mob ally : heldAllies) {
				level.mobs.add(ally);
				ally.state = ally.HUNTING;

				if (!candidatePositions.isEmpty()){
					ally.pos = candidatePositions.remove(0);
				} else {
					ally.pos = pos;
				}

			}
		}
		heldAllies.clear();
	}

	public static void clearHeldAllies(){
		heldAllies.clear();
	}

	protected class Passive implements AiState {

		public static final String TAG	= "PASSIVE";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = false;
			spend( TICK );
			return true;
		}
	}
}

