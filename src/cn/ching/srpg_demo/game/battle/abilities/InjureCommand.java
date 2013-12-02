package cn.ching.srpg_demo.game.battle.abilities;


import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.map.Hexagon;

public class InjureCommand extends Command {

	int damage;
	int direction;
	boolean critical;
	
	public InjureCommand(int damage) {
		this.damage = damage;
	}

	public InjureCommand(int damage, int direction) {
		this.damage = damage;
		this.direction = direction;
	}

	public InjureCommand(int damage, int direction, boolean critical) {
		this.damage = damage;
		this.direction = direction;
		this.critical = critical;
	}

	@Override
	public void execute(Battler taker) {
		if(taker.getLife() > damage) {
			//taker.setOrientation(-direction);
			motion = "yield";
			interpolator = new YieldInterpolator((Hexagon)taker.getBattle().getBattleground().getOrigin(), direction);
		} else {
			//taker.setOrientation(-direction);
			motion = "fall";
		}
	}

	@Override
	public void postExecute(Battler taker) {
		int many = taker.modifyLife(-damage);
		taker.attachLifeTip(many, critical);
	}

}
