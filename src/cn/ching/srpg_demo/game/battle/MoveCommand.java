package cn.ching.srpg_demo.game.battle;

public class MoveCommand extends Command {

	protected int[] start;
	protected int[] end;

	public MoveCommand(int[] start, int[] end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public void execute(Battler taker) {
		Battleground battleground = taker.getBattle().getBattleground();
		taker.setOrientation(battleground.orientate(start, end));
		taker.getBattle().moveBattler(taker, end);
		motion = "move";
		interpolator = new MoveInterpolator(battleground.coordinate(start), battleground.coordinate(end));
	}

	@Override
	public void postExecute(Battler taker) {
		taker.modifyActivity(-taker.getBattle().getBattlegroundSituation(taker).roughness(start));
	}

}