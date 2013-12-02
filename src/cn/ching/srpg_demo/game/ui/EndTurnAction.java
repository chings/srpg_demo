package cn.ching.srpg_demo.game.ui;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.EndTurnCommand;

public class EndTurnAction extends Action {

	Battler actor;

	public EndTurnAction(Battler actor) {
		this.actor = actor;
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
		actor.take(new EndTurnCommand());
	}

}
