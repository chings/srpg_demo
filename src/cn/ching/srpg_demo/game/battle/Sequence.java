package cn.ching.srpg_demo.game.battle;

import java.util.concurrent.CopyOnWriteArrayList;

public class Sequence<T extends Turner> extends CopyOnWriteArrayList<T> {

	private static final long serialVersionUID = 1L;

	int round;
	int turn;
	int limit;

	public void init() {
		round = -1;
		limit = size();
		turn = limit - 1;
	}

	public int getRound() {
		return round;
	}

	public int getTurn() {
		return turn;
	}

	public int getLimit() {
		return limit;
	}

	public T next() {
		T current = get(turn);
		T next = null;
		if(round >= 0) {
			current.onTurnEnd(turn);
		}
		if(turn < limit - 1) {
			++turn;
			next = get(turn);
		} else {
			if(round >= 0) {
				for(int i = limit - 1; i >= 0; i--) {
					T t = get(i);
					t.onRoundEnd(round);
				}
			}
			++round;
			turn = 0;
			next = get(turn);
			for(int i = limit - 1; i >= 0; i--) {
				T t = get(i);
				t.onRoundStart(round);
			}
		}
		next.onTurnStart(turn);
		return next;
	}

	public void exclude(T target) {
		for(int i = limit - 1; i >= 0; i--) {
			T t = get(i);
			if(t == target) {
				if(i < turn) {
					turn--;
				}
				super.add(remove(i));
				limit--;
			}
		}
	}

	@Override
	public boolean add(T t) {
		super.add(limit++, t);
		return true;
	}

}
