package cn.ching.srpg_demo.game.map;

public interface TileFactory<T> {

	public T create(int m, int n);

}
