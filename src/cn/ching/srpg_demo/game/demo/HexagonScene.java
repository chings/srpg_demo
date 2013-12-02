package cn.ching.srpg_demo.game.demo;

import java.util.Collection;


import cn.ching.srpg_demo.game.Game;
import cn.ching.srpg_demo.game.Scene;
import cn.ching.srpg_demo.game.map.AStar;
import cn.ching.srpg_demo.game.map.Hexagon;
import cn.ching.srpg_demo.game.map.HexagonMap;
import cn.ching.srpg_demo.game.map.TileFactory;
import cn.ching.srpg_demo.game.map.TileVisit;
import cn.ching.srpg_demo.util.Rectangles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;

public class HexagonScene extends Scene {

	static int[] roughnesses = new int[] { -1, 10, 20, 30 };

	HexagonMap<Integer> map;

	Rect field;
	Rect viewport;

	AStar.Map aStarMap;

	Collection<int[]> focuses;

	int[] pathStart = new int[] {3, 3};
	Paint[] hexagonPaints;
	Paint borderPaint, focusPaint, flagPaint, textPaint;

	public HexagonScene(Game game) {
		super(game);
		map = new HexagonMap<Integer>();
		map.configure(new Hexagon(0, 0, 30, 30, 15));
		map.configure(32, 32);
		map.fill(new TileFactory<Integer>() {
			@Override
			public Integer create(int m, int n) {
				return 1;
			}
		});
		map.setTile(new int[] { 3, 2 }, 3);
		field = map.getRect();
		viewport = new Rect(game.getScreen().getSurfaceFrame());
		aStarMap = new AStar.Map() {
			@Override
			public int[][] adjacent(int[] position) {
				return map.adjacent(position);
			}
			@Override
			public boolean reachable(int[] position) {
				return map.contains(position) && roughnesses[map.getTile(position)] >= 0;
			}
			@Override
			public int roughness(int[] position) {
				return roughnesses[map.getTile(position)];
			}
			@Override
			public int baseRoughness() {
				return 10;
			}
			@Override
			public int distance(int[] start, int[] end) {
				return map.distance(start, end);
			}
		};
		hexagonPaints = new Paint[roughnesses.length];
		hexagonPaints[0] = new Paint();
		hexagonPaints[0].setARGB(255, 255, 0, 0);
		hexagonPaints[0].setStyle(Paint.Style.FILL_AND_STROKE);
		hexagonPaints[1] = new Paint();
		hexagonPaints[1].setARGB(64, 0, 128, 0);
		hexagonPaints[1].setStyle(Paint.Style.FILL_AND_STROKE);
		hexagonPaints[2] = new Paint();
		hexagonPaints[2].setARGB(128, 0, 128, 0);
		hexagonPaints[2].setStyle(Paint.Style.FILL_AND_STROKE);
		hexagonPaints[3] = new Paint();
		hexagonPaints[3].setARGB(192, 0, 128, 0);
		hexagonPaints[3].setStyle(Paint.Style.FILL_AND_STROKE);
		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(Color.GRAY);
		borderPaint.setStyle(Paint.Style.STROKE);
		focusPaint = new Paint();
		focusPaint.setAntiAlias(true);
		focusPaint.setColor(Color.BLUE);
		focusPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		flagPaint = new Paint();
		flagPaint.setAntiAlias(true);
		flagPaint.setColor(Color.CYAN);
		flagPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.GRAY);
		textPaint.setTextAlign(Align.CENTER);
	}

	Path path = new Path();
	@Override
	public Scene onUpdate() {
		Canvas canvas = game.getScreen().lockCanvas();
		if(canvas != null) {
			try {
				canvas.drawColor(Color.WHITE);
				for(TileVisit<Integer> visit : map.part(viewport)) {
					Hexagon hexagon = (Hexagon)map.coordinate(visit.position);
					if(Rect.intersects(viewport, hexagon.getFrameRect())) {
						hexagon.getPath(path);
						path.offset(-viewport.left, -viewport.top);
						canvas.drawPath(path, hexagonPaints[visit.tile]);
						canvas.drawPath(path, borderPaint);
						int[] center = hexagon.getCenter();
						canvas.drawText("(" + visit.position[0] + "," + visit.position[1] + ")", center[0] - viewport.left, center[1] - viewport.top + 4, textPaint);
						path.reset();
					}
				}
				if(pathStart != null) {
					int[] position = pathStart;
					Hexagon hexagon = (Hexagon)map.coordinate(position);
					if(Rect.intersects(viewport, hexagon.getFrameRect())) {
						hexagon.getPath(path);
						path.offset(-viewport.left, -viewport.top);
						canvas.drawPath(path, flagPaint);
						canvas.drawPath(path, borderPaint);
						int[] center = hexagon.getCenter();
						canvas.drawText("(" + position[0] + "," + position[1] + ")", center[0] - viewport.left, center[1] - viewport.top + 4, textPaint);
						path.reset();
					}
				}
				if(focuses != null) {
					for(int[] position : focuses) {
						Hexagon hexagon = (Hexagon)map.coordinate(position);
						if(Rect.intersects(viewport, hexagon.getFrameRect())) {
							hexagon.getPath(path);
							path.offset(-viewport.left, -viewport.top);
							canvas.drawPath(path, focusPaint);
							canvas.drawPath(path, borderPaint);
							int[] center = hexagon.getCenter();
							canvas.drawText("(" + position[0] + "," + position[1] + ")", center[0] - viewport.left, center[1] - viewport.top + 4, textPaint);
							path.reset();
						}
					}
				}

				canvas.drawText(viewport.toString(), 5, viewport.height() - 5, focusPaint);
			} finally {
				game.getScreen().unlockCanvasAndPost(canvas);
			}
		}
		return this;
	}

	@Override
	public boolean onTap(int x, int y) {
		x += viewport.left;
		y += viewport.top;
		focuses = null;
		int[] position = map.decoordinate(x, y);
		if(position != null) {
			Integer tile = map.getTile(position);
			map.setTile(position, tile < roughnesses.length - 1 ? tile + 1 : 0);
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(int x, int y) {
		x += viewport.left;
		y += viewport.top;
		pathStart = map.decoordinate(x, y).clone();
		/*
		x -= viewport.width() >> 1;
		y -= viewport.height() >> 1;
		viewport.offsetTo(x, y);
		Rectangles.confine(viewport, field);
		*/
		return true;
	}

	@Override
	public boolean onLongPress(int x, int y) {
		x += viewport.left;
		y += viewport.top;
		focuses = null;
		int[] position = map.decoordinate(x, y);
		if(position != null) {
			AStar.Path path = AStar.findPath(aStarMap, pathStart, position);
			if(path != null) {
				focuses = path.positions;
			}
		}
		return true;
	}

	@Override
	public boolean onDrag(int xStart, int yStart, int xEnd, int yEnd) {
		xEnd += viewport.left;
		yEnd += viewport.top;
		focuses = null;
		int[] position = map.decoordinate(xEnd, yEnd);
		if(position != null) {
			focuses = map.lineRegion(pathStart, position);
		}
		return true;
	}

	@Override
	public boolean onDragRelease(int xStart, int yStart, int xEnd, int yEnd) {
		int dx = xStart - xEnd;
		int dy = yStart - yEnd;
		viewport.offset(dx, dy);
		Rectangles.confine(viewport, field);
		return true;
	}

}
