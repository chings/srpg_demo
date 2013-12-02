package cn.ching.srpg_demo.game.ui;


import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.ParticleSprite;
import cn.ching.srpg_demo.game.core.Camera;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

public class TextTip extends ParticleSprite {

	String text;
	TextPaint paint;
	int x, y;
	int alpha;

	public TextTip(String text, Paint paint, int x, int y) {
		this.text = text;
		this.paint = new TextPaint(paint);
		this.paint.setTextAlign(Paint.Align.CENTER);
		this.paint.setAlpha(alpha = 255);
		this.x = x;
		this.y = y;
	}

	@Override
	public void play(String motion) {
		nonce = 0;
	}

	@Override
	public void tick() {
		if(nonce++ > 15) {
			listener.onMotionEnd(this);
		} else {
			y -= 1;
			paint.setAlpha(alpha -= 4);
		}
	}

	@Override
	public void render(Canvas canvas, Camera camera) {
		int[] point = camera.view(x, y);
		canvas.drawText(text, point[0], point[1], paint);
	}

	@Override
	public void onFade(Battler owner) {
		
	}

}
