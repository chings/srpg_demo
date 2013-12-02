package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.core.IntegerPairInterpolator;

public class JumpInterpolator implements IntegerPairInterpolator {

	int height;
	int[] result;

	public JumpInterpolator(int height) {
		this.height = height;
		this.result = new int[2];
	}

	@Override
	public int[] getPair(float scale) {
		scale = (float)(scale > 0.5 ? 1.0 - scale : scale);
		result[0] = 0;
		result[1] = -Math.round(scale * height);
		return result;
	}

}
