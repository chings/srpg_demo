package cn.ching.srpg_demo.game.battle;

public interface Turner {

	public void onRoundStart(int round);

	public void onTurnStart(int turn);

	public void onTurnEnd(int turn);

	public void onRoundEnd(int round);

}
