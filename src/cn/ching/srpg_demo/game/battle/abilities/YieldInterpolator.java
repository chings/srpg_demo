package cn.ching.srpg_demo.game.battle.abilities;


import cn.ching.srpg_demo.game.core.IntegerPairInterpolator;
import cn.ching.srpg_demo.game.map.Hexagon;
import cn.ching.srpg_demo.game.map.HexagonMap;

public class YieldInterpolator implements IntegerPairInterpolator {

	int dx, dy;
	int[] result;

	public YieldInterpolator(Hexagon hexagon, int orientation) {
		this(hexagon, orientation, 0.5f);
	}

	public YieldInterpolator(Hexagon hexagon, int orientation, float factor) {
		result = new int[2];
		int a = Math.round(hexagon.getHalfWidth() * factor);
		int b = Math.round((hexagon.getHalfWidth() >> 1) * factor);
		int c = Math.round((hexagon.getBaseHeight() >> 1) * factor);
		switch(orientation) {
		case HexagonMap.LEFT:
			dx = -a;
			break;
		case HexagonMap.DOWNLEFT:
			dx = -b;
			dy = c;
			break;
		case HexagonMap.UPLEFT:;
			dx = -b;
			dy = -c;
			break;
		case HexagonMap.DOWNRIGHT:
			dx = b;
			dy = c;
			break;
		case HexagonMap.UPRIGHT:
			dx = b;
			dy = -c;
		case HexagonMap.RIGHT:
			dx = a;
			break;
		}
	}

	@Override
	public int[] getPair(float scale) {
		scale = (float)(scale > 0.5 ? 1.0 - scale : scale);
		result[0] = Math.round(scale * dx);
		result[1] = Math.round(scale * dy);
		return result;
	}

}
