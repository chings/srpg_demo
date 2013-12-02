package cn.ching.srpg_demo.game.battle;

import cn.ching.srpg_demo.game.Game;
import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.Scene;

import android.graphics.Canvas;
import android.graphics.Color;

public class LoadingScene extends Scene {

	int id;
	
	public LoadingScene(Game game, int id) {
		super(game);
		this.id = id;
	}

	@Override
	public Scene onUpdate() {
		Canvas canvas = game.getScreen().lockCanvas();
		if(canvas != null) {
			try {
				canvas.drawColor(Color.BLACK);
				canvas.drawText("Loading...", 5, game.getScreen().getSurfaceFrame().bottom - 5, Resource.paints.get("text.debug"));
			} finally {
				game.getScreen().unlockCanvasAndPost(canvas);
			}
		}
		return new BattleScene(game, id);
	}

}
