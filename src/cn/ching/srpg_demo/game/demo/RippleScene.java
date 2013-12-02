package cn.ching.srpg_demo.game.demo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import cn.ching.srpg_demo.game.Game;
import cn.ching.srpg_demo.game.Scene;
import cn.ching.srpg_demo.game.debug.FPS;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;

public class RippleScene extends Scene {

	List<Ripple> ripples;

	FPS fps;

	TextPaint textPaint;

	public RippleScene(Game game) {
		super(game);
		ripples = new LinkedList<Ripple>();
		fps = new FPS(10);
		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.BLUE);
	}

	@Override
	public Scene onUpdate() {
		Canvas canvas = game.getScreen().lockCanvas();
		if(canvas != null) {
			try {
				Rect viewport = game.getScreen().getSurfaceFrame();
				canvas.drawColor(Color.WHITE);
				for(Iterator<Ripple> it = ripples.iterator(); it.hasNext(); ) {
					Ripple ripple = it.next();
					if(ripple.isExpired()) {
						it.remove();
					} else {
						ripple.render(canvas, viewport);
					}
				}
				fps.render(canvas, 100, 100, textPaint);
			} finally {
				game.getScreen().unlockCanvasAndPost(canvas);
			}
		}
		return this;
	}

	@Override
	public boolean onTap(int x, int y) {
		Ripple ripple = new Ripple(x, y);
		ripples.add(ripple);
		return true;
	}

}
