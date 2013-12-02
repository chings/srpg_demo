package cn.ching.srpg_demo.util;

import android.graphics.Rect;

public class Rectangles {

	public static void confine(Rect rect, Rect field) {
		int x = rect.right > field.right ? field.right - rect.width() : rect.left;
		if(x < field.left) x = field.left;
		int y = rect.bottom > field.bottom ? field.bottom - rect.height() : rect.top;
		if(y < field.top) y = field.top;
		rect.offsetTo(x, y);
	}

	public static int[] intersect(Rect rect1, Rect rect2) {
		if(rect1.left < rect2.right && rect2.left < rect1.right && rect1.top < rect2.bottom && rect2.top < rect1.bottom) {
			return new int[] {
				rect1.left < rect2.left ? rect2.left - rect1.left : 0,
				rect1.top < rect2.top ? rect2.top - rect1.top : 0,
				rect1.right > rect2.right ? rect1.right - rect2.right : 0,
				rect1.bottom > rect2.bottom ? rect1.bottom - rect2.bottom : 0
			};
		}
		return null;
	}

}
