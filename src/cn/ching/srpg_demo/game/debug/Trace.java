package cn.ching.srpg_demo.game.debug;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public class Trace {

	protected Rect rect;
	protected List<String> text;

	protected Paint rectPaint, borderPaint, textPaint;
	protected int lineHeight;

	public Trace(Rect rect) {
		this.rect = rect;
		text = new ArrayList<String>();
		rectPaint = new Paint();
		rectPaint.setARGB(64, 0, 0, 0);
		rectPaint.setStyle(Paint.Style.FILL);
		borderPaint = new Paint();
		borderPaint.setColor(Color.GREEN);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(2);
		textPaint = new TextPaint();
		textPaint.setColor(Color.GREEN);
		textPaint.setTypeface(Typeface.create("Courier", Typeface.NORMAL));
		lineHeight = 12;
		textPaint.setTextSize(lineHeight);
	}

	public void clear() {
		text.clear();
	}

	public void println(Object... args) {
		StringBuilder s = new StringBuilder();
		for(Object arg : args) {
			s.append(arg);
		}
		text.add(s.toString());
	}

	public void render(Canvas canvas) {
		canvas.drawRect(rect, rectPaint);
		canvas.drawRect(rect, borderPaint);
		int line = 0;
		for(String s : text) {
			line += textPaint.getTextSize();
			canvas.drawText(s, rect.left + 2, rect.top + 2 + line, textPaint);
		}
	}

}
