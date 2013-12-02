package cn.ching.srpg_demo.game.ui;

abstract public class Spread {

	abstract public void reset();

	abstract public int[] next();

	abstract public int[] forward(int side);

}
