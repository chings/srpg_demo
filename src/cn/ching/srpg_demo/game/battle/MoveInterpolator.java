package cn.ching.srpg_demo.game.battle;


import cn.ching.srpg_demo.game.core.IntegerPairInterpolator;
import cn.ching.srpg_demo.game.map.Geometry;

public class MoveInterpolator implements IntegerPairInterpolator {

	int dx, dy;
	int[] result;

	public MoveInterpolator(Geometry start, Geometry end) {
		int[] src = start.getCenter();
		int[] dest = end.getCenter();
		dx = src[0] - dest[0];
		dy = src[1] - dest[1];
		result = new int[2];
	}

	@Override
	public int[] getPair(float scale) {
		scale = (float)(1.0 - scale);
		result[0] = Math.round(scale * dx);
		result[1] = Math.round(scale * dy);
		return result;
	}

}
