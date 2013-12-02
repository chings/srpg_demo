package cn.ching.srpg_demo.game.battle;

public class Weapon extends Gear {

	public boolean isMelee() {
		int[] range = this.getInts("range");
		if(range != null) {
			return range[range.length - 1] == 1;
		}
		return false;
	}

	public boolean isRanged() {
		int[] range = this.getInts("range");
		if(range != null) {
			return range[range.length - 1] > 1;
		}
		return false;
	}

}
