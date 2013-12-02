package cn.ching.srpg_demo.game.ui;

import java.util.Iterator;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.MoveCommand;
import cn.ching.srpg_demo.game.map.AStar;
import cn.ching.srpg_demo.game.map.AStar.Path;

public class MoveAction extends Action {

	Battler actor;
	Path path;

	public MoveAction(Battler actor) {
		this.actor = actor;
	}

	@Override
	public int getResource() {
		return R.drawable.icon_move;
	}

	@Override
	public int prePerform(int[] position) {
		AStar.Map map = actor.getBattle().getBattlegroundSituation(actor);
		if(!map.reachable(position)) {
			return Ability.INVALID_TARGET;
		}
		Path path = AStar.findPath(map, actor.getPosition(), position);
		if(path == null) {
			return Ability.INVALID_TARGET;
		}
		this.path = path;
		if(path.cost > actor.getActivity()) {
			return Ability.OUT_OF_ACTIVITY;
		}
		return Ability.READY;
	}

	@Override
	public void perform() {
		Iterator<int[]> it = path.positions.iterator();
		int[] position = it.next();
		int[] nextPosition = null;
		for(; it.hasNext(); ) {
			nextPosition = it.next();
			actor.take(new MoveCommand(position, nextPosition));
			position = nextPosition;
		}
	}

	@Override
	public void byPerform() {
		if(path.positions != null) {
			actor.getBattle().getBattleground().highlight(path.positions);
		}
	}

	@Override
	public String toString() {
		return "Move";
	}

}
