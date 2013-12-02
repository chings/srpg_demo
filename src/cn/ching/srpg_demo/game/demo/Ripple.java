package cn.ching.srpg_demo.game.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Ripple {

	int x, y;
	int lifetime;

	Paint paint;

	public Ripple(int x, int y) {
		this.x = x;
		this.y = y;
		lifetime = 0;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
	}

	public void render(final Canvas canvas, final Rect viewport) {
		int span = lifetime * 2;
		canvas.drawOval(new RectF(x - span, y - span, x + span, y + span), paint);
		lifetime++;
	}

	public boolean isExpired() {
		return lifetime >= 25;
	}

}
