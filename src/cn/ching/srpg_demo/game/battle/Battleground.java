package cn.ching.srpg_demo.game.battle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.core.Camera;
import cn.ching.srpg_demo.game.data.Loader;
import cn.ching.srpg_demo.game.map.Hexagon;
import cn.ching.srpg_demo.game.map.HexagonMap;
import cn.ching.srpg_demo.game.map.TileVisit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class Battleground extends HexagonMap<Tile> {

	BattleScene battle;

	int resource;
	int width, height;
	Map<Integer, Integer> roughnesses;

	Collection<int[]> highlightedPositions;

	public Battleground() {
		roughnesses = new HashMap<Integer, Integer>();
		roughnesses.put(null, 10);
		roughnesses.put(0, 10);
	}

	@Loader
	public void loadResource(int resource) {
		this.resource = resource;
		Bitmap bitmap = Resource.images.get(resource);
		assert bitmap != null;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
	}

	@Loader
	public void loadOrigin(Hexagon origin) {
		configure(origin);
	}

	@Loader
	public void loadGrid(int[][] grid) {
		configure(grid[0].length, grid.length);
		for(int j = 0; j < rows; j++) {
			for(int i = 0; i < columns; i++) {
				tiles[i][j] = new Tile(grid[i][j]);
			}
		}
	}

	public void setBattle(BattleScene battle) {
		this.battle = battle;
	}

	@Override
	public Rect getRect() {
		return new Rect(0, 0, width, height);
	}

	public int getBaseRoughnesses() {
		return roughnesses.get(null);
	}

	public int getRoughnesses(Tile tile) {
		return roughnesses.get(tile.terrain);
	}

	public void highlight(Collection<int[]> positions) {
		this.highlightedPositions = positions;
	}

	public void clear() {
		this.highlightedPositions = null;
	}

	public void tick() {

	}

	Path path = new Path();
	public void render(Canvas canvas, Camera camera) {
		Bitmap bitmap = Resource.images.get(resource);
		canvas.drawBitmap(bitmap, camera.getViewRect(), camera.getRenderRect(), null);
		for(TileVisit<Tile> visit : part(camera.getViewRect())) {
			Hexagon hexagon = (Hexagon)coordinate(visit.position);
			if(Rect.intersects(camera.getViewRect(), hexagon.getFrameRect())) {
				hexagon.getPath(path);
				camera.view(path);
				canvas.drawPath(path, Resource.paints.get("border.tile." + visit.tile.terrain));
				path.reset();
			}
		}
		if(highlightedPositions != null && !this.highlightedPositions.isEmpty()) {
			for(int[] position : highlightedPositions) {
				if(!contains(position)) {
					continue;
				}
				Hexagon hexagon = (Hexagon)coordinate(position);
				if(Rect.intersects(camera.getViewRect(), hexagon.getFrameRect())) {
					hexagon.getPath(path);
					camera.view(path);
					canvas.drawPath(path, Resource.paints.get("background.tile.assist"));
					path.reset();
				}
			}
		}
		Battler turner = battle.getActor();
		if(turner != null) {
			Hexagon hexagon = (Hexagon)coordinate(turner.getPosition());
			if(Rect.intersects(camera.getViewRect(), hexagon.getFrameRect())) {
				hexagon.getPath(path);
				camera.view(path);
				canvas.drawPath(path, Resource.paints.get("background.tile.focus"));
				path.reset();
			}
		}
	}

}
