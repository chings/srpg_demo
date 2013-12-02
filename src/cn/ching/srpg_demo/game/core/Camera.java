package cn.ching.srpg_demo.game.core;

import cn.ching.srpg_demo.util.Rectangles;

import android.graphics.Path;
import android.graphics.Rect;

public class Camera {

	Rect renderRect;
	Rect viewRect;

	public Camera(Rect rect) {
		this.renderRect = new Rect(rect);
		this.viewRect = new Rect(rect);
	}

	public Rect getRenderRect() {
		return renderRect;
	}

	public Rect getViewRect() {
		return viewRect;
	}

	public int getViewLeft() {
		return viewRect.left;
	}

	public int getViewTop() {
		return viewRect.top;
	}

	public void view(Rect rect) {
		rect.offset(-viewRect.left, -viewRect.top);
	}

	public void view(Path path) {
		path.offset(-viewRect.left, -viewRect.top);
	}

	public void view(int[] point) {
		point[0] -= viewRect.left;
		point[1] -= viewRect.top;
	}

	int[] point = new int[2];
	public int[] view(int x, int y) {
		point[0] = x -viewRect.left;
		point[1] = y -viewRect.top;
		return point;
	}

	public boolean canView(Rect rect) {
		return viewRect.contains(rect);
	}

	public boolean canView(int x, int y) {
		return viewRect.contains(x, y);
	}

	public void move(int dx, int dy) {
		viewRect.offset(dx, dy);
	}

	public void center(int x, int y, Rect world) {
		x -= renderRect.width() >> 1;
		y -= renderRect.height() >> 1;
		viewRect.offsetTo(x, y);
		Rectangles.confine(viewRect, world);
	}
}
