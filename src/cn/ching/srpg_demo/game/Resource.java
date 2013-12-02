package cn.ching.srpg_demo.game;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.data.DataAccessLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

public class Resource {

	public static Context context;
	public static DataAccessLayer data = new DataAccessLayer();

	static BitmapFactory.Options imagesOptions = new BitmapFactory.Options();
	static {
		imagesOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		imagesOptions.inPurgeable = true;
		imagesOptions.inInputShareable = true;
	}

	@SuppressWarnings("serial")
	public static Map<Integer, Bitmap> images = new HashMap<Integer, Bitmap>() {
		@Override
		public Bitmap get(Object id) {
			Bitmap bitmap = (Bitmap)super.get(id);
			if(bitmap == null) {
				bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource((Integer)id), null, imagesOptions);
				put((Integer)id, bitmap);
			}
			return bitmap;
		}
	};

	public static Integer getObjectId(String name) {
		try {
			Field field = R.raw.class.getDeclaredField(name);
			return field.getInt(R.raw.class);
		} catch(Exception x) {
			return null;
		}
	}

	public static Integer getImageId(String name) {
		try {
			Field field = R.drawable.class.getDeclaredField(name);
			return field.getInt(R.drawable.class);
		} catch(Exception x) {
			return null;
		}
	}

	public static String getImageName(int id) {
		for(Field field : R.drawable.class.getDeclaredFields()) {
			try {
				if(field.getInt(R.drawable.class) == id) {
					return field.getName();
				}
			} catch(Exception x) {
				throw new RuntimeException(x);
			}
		}
		return null;
	}

	public static Map<String, Paint> paints = new HashMap<String, Paint>();
	static {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paints.put("border.black", paint);
		paints.put("border.tile.0", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paints.put("border.action_button", paint);
		paint = new TextPaint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(10);
		paints.put("text.black_bold_centerized", paint);
		paint = new TextPaint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paints.put("text.green", paint);
		paints.put("text.action_button.order", paint);
		paints.put("text.debug", paint);
		paint = new TextPaint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paints.put("text.red", paint);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.black", paint);
		paints.put("background.debug", paint);
		paints.put("background.lifebar", paint);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.red", paint);
		paints.put("background.tile.unreachable", paint);
		paints.put("foreground.lifebar.npc", paint);
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.green", paint);
		paints.put("foreground.lifebar.pc", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(192, 128, 0, 0);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.red_transparent", paint);
		paints.put("background.action_button." + Ability.OUT_OF_ENERGY, paint);
		paints.put("background.action_button." + Ability.OUT_OF_ACTIVITY, paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(128, 0, 255, 0);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.green_transparent", paint);
		paints.put("background.tile.assist", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(192, 0, 0, 128);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.blue_transparent", paint);
		paints.put("background.action_button." + Ability.OUT_OF_RANGE, paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(128, 255, 255, 255);
		paint.setStyle(Paint.Style.FILL);
		paints.put("background.white_transparent", paint);
		paints.put("background.tile.focus", paint);
		paint = new Paint();
		paint.setAntiAlias(false);
		paint.setDither(true);
		paint.setARGB(192, 255, 255, 255);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		paints.put("particle.lightning.trunck", paint);
		paints.put("particle.deathRay.trunck", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setARGB(224, 128, 128, 255);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(1);
		paints.put("particle.lightning.branch", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setARGB(128, 0, 0, 255);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(4);
		paints.put("particle.lightning.shadow", paint);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setARGB(128, 255, 0, 0);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(6);
		paints.put("particle.deathRay.shadow", paint);
	}

}
