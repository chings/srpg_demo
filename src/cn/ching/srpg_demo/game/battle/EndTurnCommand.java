package cn.ching.srpg_demo.game.battle;

public class EndTurnCommand extends Command {

	@Override
	public void execute(Battler taker) {
		taker.endTurn();
	}

}
