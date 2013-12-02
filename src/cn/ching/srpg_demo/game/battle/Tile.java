package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.core.Thing;

public class Tile {

	public int terrain;
	public Thing occupier;
	public List<Thing> piles;

	public Tile(int terrain) {
		this.terrain = terrain;
	}

	public boolean isOccupied() {
		return occupier != null;
	}

	public Thing getOccupier() {
		return occupier;
	}

	public void setOccupier(Thing occupier) {
		this.occupier = occupier;
	}

	public List<Thing> getPiles() {
		return piles;
	}

	public void pile(Thing thing) {
		if(piles == null) {
			piles = new ArrayList<Thing>();
		}
		piles.add(thing);
	}

}
