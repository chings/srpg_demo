package cn.ching.srpg_demo.game.ui;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.Ability;

public class CancelTargetingAction extends Action {

	ActionPanel panel;

	public CancelTargetingAction(ActionPanel panel) {
		this.panel = panel;
	}

	@Override
	public int getResource() {
		return R.drawable.icon_cancel;
	}

	@Override
	public int prePerform(int[] position) {
		return Ability.READY;
	}

	@Override
	public void perform() {
		panel.dismiss();
	}

}
