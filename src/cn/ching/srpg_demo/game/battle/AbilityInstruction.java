package cn.ching.srpg_demo.game.battle;

public interface AbilityInstruction {
	
	public int[] selectCastingPosition(Battler caster);

	public int[] getCastingRange(Battler caster);

}
