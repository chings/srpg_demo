package cn.ching.srpg_demo.game.battle;

import cn.ching.srpg_demo.game.core.IntegerPairInterpolator;

abstract public class Command {

	protected String motion;
	protected IntegerPairInterpolator interpolator;

	abstract public void execute(Battler taker);

	public void postExecute(Battler taker) {

	}

	public String getMotion() {
		return motion;
	}

	public IntegerPairInterpolator getInterpolator() {
		return interpolator;
	}

}
