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

import com.shatteredpixel.cursedpixeldungeon.Assets;
import com.shatteredpixel.cursedpixeldungeon.Badges;
import com.shatteredpixel.cursedpixeldungeon.Dungeon;
import com.shatteredpixel.cursedpixeldungeon.actors.Actor;
import com.shatteredpixel.cursedpixeldungeon.actors.Char;
import com.shatteredpixel.cursedpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.cursedpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.cursedpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.cursedpixeldungeon.effects.Flare;
import com.shatteredpixel.cursedpixeldungeon.effects.Speck;
import com.shatteredpixel.cursedpixeldungeon.items.ArmorKit;
import com.shatteredpixel.cursedpixeldungeon.items.Item;
import com.shatteredpixel.cursedpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.cursedpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.cursedpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.cursedpixeldungeon.items.powers.HeroicLeap;
import com.shatteredpixel.cursedpixeldungeon.items.powers.MoltenEarth;
import com.shatteredpixel.cursedpixeldungeon.items.powers.RaiseDead;
import com.shatteredpixel.cursedpixeldungeon.items.powers.SmokeBomb;
import com.shatteredpixel.cursedpixeldungeon.items.powers.SpectralBlades;
import com.shatteredpixel.cursedpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.cursedpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.cursedpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.cursedpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.cursedpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.cursedpixeldungeon.levels.CityLevel;
import com.shatteredpixel.cursedpixeldungeon.messages.Messages;
import com.shatteredpixel.cursedpixeldungeon.scenes.GameScene;
import com.shatteredpixel.cursedpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.cursedpixeldungeon.sprites.UndeadSprite;
import com.shatteredpixel.cursedpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class King extends Mob {
	
	private static final int MAX_ARMY_SIZE	= 5;
	
	{
		spriteClass = KingSprite.class;
		
		HP = HT = 800;
		EXP = 40;
		defenseSkill = 25;
		
		Undead.count = 0;

		properties.add(Property.BOSS);
		properties.add(Property.UNDEAD);
	}
	
	private boolean nextPedestal = true;
	
	private static final String PEDESTAL = "pedestal";
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc(enemy, damage);
		if (Random.Int(MAX_ARMY_SIZE) == 0) {
			Buff.prolong(enemy, Charm.class, 4);
		}

		return damage;
	}
		@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( PEDESTAL, nextPedestal );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		nextPedestal = bundle.getBoolean( PEDESTAL );
		BossHealthBar.assignBoss(this);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 35, 80 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 32;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 14);
	}
	
	@Override
	protected boolean getCloser( int target ) {
		return canTryToSummon() ?
			super.getCloser( ((CityBossLevel)Dungeon.level).pedestal( nextPedestal ) ) :
			super.getCloser( target );
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return canTryToSummon() ?
				pos == ((CityBossLevel)Dungeon.level).pedestal( nextPedestal ) :
				Dungeon.level.adjacent( pos, enemy.pos );
	}
	
	private boolean canTryToSummon() {
		if (paralysed <= 0 && Undead.count < maxArmySize()) {
			Char ch = Actor.findChar( ((CityBossLevel)Dungeon.level).pedestal( nextPedestal ) );
			return ch == this || ch == null;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean act() {
		if (canTryToSummon() && pos == ((CityBossLevel)Dungeon.level).pedestal( nextPedestal )) {
			summon();
			return true;
		} else {
			if (enemy != null && Actor.findChar( ((CityBossLevel)Dungeon.level).pedestal( nextPedestal ) ) == enemy) {
				nextPedestal = !nextPedestal;
			}
			return super.act();
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);
		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg);
	}
	
	@Override
	public void die( Object cause ) {

		GameScene.bossSlain();
		Item item;
		switch (Dungeon.hero.heroClass) {
			case MAGE:
				item = new MoltenEarth();
				break;
			case ROGUE:
				item = new SmokeBomb();
				break;
			case WARRIOR: default:
				item = new HeroicLeap();
				break;
			case HUNTRESS:
				item = new SpectralBlades();
				break;
			case PRIESTESS:
				item = new RaiseDead();
				break;
		}

		Dungeon.level.drop(new PotionOfStrength(), pos);
		if (Dungeon.hero.belongings.getItem(item.getClass()) != null) {
			item = new MetalShard();
		}
		Dungeon.level.drop( item, pos ).sprite.drop();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();
		
		super.die( cause );
		
		Badges.validateBossSlain();
		int blobs = Random.chances(new float[]{0, 0, 6, 3, 1});
		for (int i = 0; i < blobs; i++){
			int ofs;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!Dungeon.level.passable[pos + ofs]);
			Dungeon.level.drop( new ScrollOfUpgrade(), pos + ofs ).sprite.drop( pos );
		}
		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(this, "defeated", Dungeon.hero.givenName()) );
	}

	@Override
	public void aggro(Char ch) {
		super.aggro(ch);
		for (Mob mob : Dungeon.level.mobs){
			if (mob instanceof Undead){
				mob.aggro(ch);
			}
		}
	}

	private int maxArmySize() {
		return 1 + MAX_ARMY_SIZE * (HT - HP) / HT;
	}
	
	private void summon() {

		nextPedestal = !nextPedestal;
		
		sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.4f, 2 );
		Sample.INSTANCE.play( Assets.SND_CHALLENGE );
		
		boolean[] passable = Dungeon.level.passable.clone();
		for (Char c : Actor.chars()) {
			passable[c.pos] = false;
		}
		
		int undeadsToSummon = maxArmySize() - Undead.count;

		PathFinder.buildDistanceMap( pos, passable, undeadsToSummon );
		PathFinder.distance[pos] = Integer.MAX_VALUE;
		int dist = 1;
		
	undeadLabel:
		for (int i=0; i < undeadsToSummon; i++) {
			do {
				for (int j=0; j < Dungeon.level.length(); j++) {
					if (PathFinder.distance[j] == dist) {
						
						Undead undead = new Undead();
						undead.pos = j;
						GameScene.add( undead );
						
						ScrollOfTeleportation.appear( undead, j );
						new Flare( 3, 32 ).color( 0x000000, false ).show( undead.sprite, 2f ) ;
						
						PathFinder.distance[j] = Integer.MAX_VALUE;
						
						continue undeadLabel;
					}
				}
				dist++;
			} while (dist < undeadsToSummon);
		}
		
		yell( Messages.get(this, "arise") );
		spend( TICK );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell( Messages.get(this, "notice") );
	}
	
	{
		resistances.add( WandOfDisintegration.class );
		resistances.add( ToxicGas.class );
		resistances.add( Burning.class );
	}
	
	{
		immunities.add( Paralysis.class );
		immunities.add( Vertigo.class );
		immunities.add( Blindness.class );
		immunities.add( Terror.class );
	}
	
	public static class Undead extends Mob {
		
		public static int count = 0;
		
		{
			spriteClass = UndeadSprite.class;
			
			HP = HT = 80;
			defenseSkill = 15;
			
			maxLvl = -2;
			EXP = 0;
			
			state = WANDERING;

			properties.add(Property.UNDEAD);
			properties.add(Property.INORGANIC);
		}
		
		@Override
		protected void onAdd() {
			count++;
			super.onAdd();
		}
		
		@Override
		protected void onRemove() {
			count--;
			super.onRemove();
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 15, 40*(Undead.count/2) );
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 16;
		}
		
		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );
			if (Random.Int( MAX_ARMY_SIZE ) == 0) {
				Buff.prolong( enemy, Paralysis.class, 1 );
			}
			
			return damage;
		}
		
		@Override
		public void damage( int dmg, Object src ) {
			super.damage( dmg, src );
			if (src instanceof ToxicGas) {
				((ToxicGas)src).clear( pos );
			}
		}
		
		@Override
		public void die( Object cause ) {
			super.die( cause );
			
			if (Dungeon.level.heroFOV[pos]) {
				Sample.INSTANCE.play( Assets.SND_BONES );
			}
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 5);
		}

		{
			immunities.add( Grim.class );
			immunities.add( Paralysis.class );
		}
	}
}
