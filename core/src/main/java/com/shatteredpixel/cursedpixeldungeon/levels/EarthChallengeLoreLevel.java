package com.shatteredpixel.cursedpixeldungeon.levels;

import com.shatteredpixel.cursedpixeldungeon.Assets;
import com.shatteredpixel.cursedpixeldungeon.actors.mobs.Mob;

public class EarthChallengeLoreLevel extends Level {
	{
		viewDistance = 4;
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_EARTH;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}

	@Override
	public int nMobs() {
		return 0;
	}

	@Override
	protected boolean build() {

		setSize(48, 48);

		map = Layouts.TOWN_LAYOUT.clone();

		buildFlagMaps();
		cleanWalls();

		entrance = 25 + width() * 21;
		exit = 5 + height() * 40;

		return true;
	}

	@Override
	public Mob createMob() {
		return super.createMob();
	}

	@Override
	protected void createMobs() {

	}

	@Override
	protected void createItems() {

	}
}
