package cn.ching.srpg_demo.game.map;

import java.util.List;

public interface GeometryMap {

	public boolean contains(int[] position);

	public Geometry coordinate(int[] position);

	public int[] decoordinate(int x, int y);

	public int distance(int[] start, int[] end);

	public int orientate(int[] start, int[] end);

	public int[][] adjacent(int[] position);

	public int[] adjacent(int[] position, int orientation);

	public List<int[]> lineRegion(int[] start, int[] end);

	public List<int[]> circleRegion(int[] center, int radius);

}
