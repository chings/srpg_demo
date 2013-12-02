package cn.ching.srpg_demo.game.ui;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.Ability;

public class EndTargetingAction extends Action {

	static int[] END_FLAG = new int[1];

	ActionPanel panel;

	public EndTargetingAction(ActionPanel panel) {
		this.panel = panel;
	}

	@Override
	public int getResource() {
		return R.drawable.icon_proceed;
	}

	@Override
	public int prePerform(int[] position) {
		return Ability.READY;
	}

	@Override
	public void perform() {
		Action action = panel.getCurrentAction();
		int result = panel.recheck(action, END_FLAG);
		if(result == Ability.READY) {
			action.perform();
		}
		panel.dismiss();
	}

}
