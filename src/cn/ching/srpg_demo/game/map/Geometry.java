package cn.ching.srpg_demo.game.map;

import android.graphics.Path;
import android.graphics.Rect;

public interface Geometry {

	public Rect getFrameRect();

	public int[] getCenter();

	public int[][] getEndpoints();

	public Path getPath();

	public Path getPath(Path path);

}
