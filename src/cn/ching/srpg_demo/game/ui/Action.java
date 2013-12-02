package cn.ching.srpg_demo.game.ui;


abstract public class Action {

	abstract public int getResource();

	abstract public int prePerform(int[] position);

	abstract public void perform();

	public void byPerform() {

	}

}
