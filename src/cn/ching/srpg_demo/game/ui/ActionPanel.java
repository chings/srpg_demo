package cn.ching.srpg_demo.game.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.core.Camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class ActionPanel implements Widget {

	public static final int MAX_TARGET_NUMBER = 20;

	public static final int HIDDEN = -1;
	public static final int READY = 0;
	public static final int TARGETING = 1;
	public static final int AJUSTING = 2;

	List<Action> battlerActions;
	Action endTurnAction;
	Action endTargetingAction;
	Action cancelTargetingAction;

	int phase;
	List<Action> actions;
	List<Action> additionalActions;
	Action currentAction;
	int[] targetedPositions;
	int targetedNumber;
	Map<String, List<Integer>> targetedOrdersWithRect;
	int[] lastCheckedPositionDuringDrag;
	Map<Action, Integer> results;

	int x, y;
	Spread spread;
	int diameter;
	int radius;
	Rect srcRect;
	Rect destRect;

	public ActionPanel() {
		phase = HIDDEN;
		endTargetingAction = new EndTargetingAction(this);
		cancelTargetingAction = new CancelTargetingAction(this);
		actions = new ArrayList<Action>();
		additionalActions = new ArrayList<Action>();
		currentAction = null;
		targetedPositions = new int[MAX_TARGET_NUMBER << 1];
		targetedOrdersWithRect = new LinkedHashMap<String, List<Integer>>();
		lastCheckedPositionDuringDrag = new int[2];
		results = new HashMap<Action, Integer>();
		diameter = 50;
		radius = diameter >> 1;
		spread = new HexagonalSpiralSpread(54, 31);
		srcRect = new Rect(4, 4, 60, 60);
	}

	public void load(Battler battler) {
		battlerActions = new ArrayList<Action>();
		battlerActions.add(new MoveAction(battler));
		for(Ability ability : battler.getAllAbilities()) {
			battlerActions.add(new AbilityAction(battler, ability));
		}
		endTurnAction = new EndTurnAction(battler);
	}

	public boolean isHidden() {
		return phase == HIDDEN;
	}

	public boolean isReady() {
		return phase == READY;
	}

	public boolean isTargeting() {
		return phase == TARGETING;
	}

	public boolean isAjusting() {
		return phase == AJUSTING;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public int getResult(Action action) {
		return results.get(action);
	}

	public void setResult(Action action, int result) {
		results.put(action, result);
	}

	public int getX() {
		return x + radius;
	}

	public int getY() {
		return y + radius;
	}

	public void moveTo(int x, int y) {
		this.x = x - radius;
		this.y = y - radius;
		destRect = new Rect(this.x, this.y, this.x + diameter, this.y + diameter);
	}

	public void show() {
		phase = READY;
	}

	public void beginAjusting(Action action) {
		phase = AJUSTING;
		currentAction = action;
		actions.clear();
		actions.add(currentAction);
		additionalActions.clear();
	}

	public void beginTargeting(Action action) {
		phase = TARGETING;
		currentAction = action;
		actions.clear();
		additionalActions.clear();
		additionalActions.add(endTargetingAction);
		additionalActions.add(cancelTargetingAction);
	}

	public void dismiss() {
		phase = HIDDEN;
		currentAction = null;
		actions.clear();
		additionalActions.clear();
		targetedNumber = 0;
		targetedOrdersWithRect.clear();
		results.clear();
	}

	public void filter(int[] position) {
		for(Action action : battlerActions) {
			int result = action.prePerform(position);
			if(result >= Ability.READY) {
				actions.add(action);
				results.put(action, result);
			}
		}
		additionalActions.add(endTurnAction);
		results.put(endTurnAction, Ability.READY);
		this.lastCheckedPositionDuringDrag[0] = position[0];
		this.lastCheckedPositionDuringDrag[1] = position[1];
	}

	private boolean hasIntersectedWithTargetedRects(Rect testRect) {
		if(targetedOrdersWithRect.isEmpty()) {
			return false;
		}
		Rect rect = null;
		for(Entry<String, List<Integer>> entry : targetedOrdersWithRect.entrySet()) {
			rect = Rect.unflattenFromString(entry.getKey());
			if(Rect.intersects(rect, testRect)) {
				return true;
			}
		}
		return false;
	}

	public Action choose(int x, int y, Camera camera) {
		rect.set(destRect);
		if(Rect.intersects(destRect, camera.getViewRect())) {
			spread.reset();
			for(Action action : actions) {
				do {
					int[] point = spread.next();
					rect.offsetTo(this.x + point[0], this.y + point[1]);
				} while(!camera.canView(rect) || hasIntersectedWithTargetedRects(rect));
				if(rect.contains(x, y)) {
					return action;
				}
			}
			spread.forward(4);
			for(Action action : additionalActions) {
				do {
					int[] point = spread.next();
					rect.offsetTo(this.x + point[0], this.y + point[1]);
				} while(!camera.canView(rect) || hasIntersectedWithTargetedRects(rect));
				if(rect.contains(x, y)) {
					return action;
				}
			}
		}
		return null;
	}

	public int check(Action action, int[] position) {
		if(lastCheckedPositionDuringDrag != null && Arrays.equals(this.lastCheckedPositionDuringDrag, position)) {
			return results.get(action);
		}
		int result = action.prePerform(position);
		results.put(action, result);
		this.lastCheckedPositionDuringDrag[0] = position[0];
		this.lastCheckedPositionDuringDrag[1] = position[1];
		return result;
	}

	public int recheck(Action action, int[] newPosition) {
		int i = targetedNumber << 1;
		for(int n : newPosition) {
			targetedPositions[i++] = n;
		}
		int[] position = new int[i];
		System.arraycopy(targetedPositions, 0, position, 0, i);
		int result = action.prePerform(position);
		results.put(action, result);
		return result;
	}

	public void target(int[] position, int x, int y) {
		int i = targetedNumber << 1;
		for(int n : position) {
			targetedPositions[i++] = n;
		}
		targetedNumber++;
		rect.set(x - radius, y - radius, x + radius, y + radius);
		String rectKey = rect.flattenToString();
		List<Integer> targetOrder = targetedOrdersWithRect.get(rectKey);
		if(targetOrder == null) {
			targetOrder = new ArrayList<Integer>(1);
			targetedOrdersWithRect.put(rectKey, targetOrder);
		}
		targetOrder.add(targetedNumber);
	}

	private void drawButton(Canvas canvas, Bitmap bitmap, RectF rect) {
		drawButton(canvas, bitmap, rect, Ability.READY);
	}

	private void drawButton(Canvas canvas, Bitmap bitmap, RectF rect, int result) {
		path.reset();
		path.addOval(rectf, Path.Direction.CW);
		canvas.clipPath(path, Region.Op.REPLACE);
		canvas.drawBitmap(bitmap, srcRect, rect, null);
		Paint style = Resource.paints.get("background.action_button." + result);
		if(style != null) {
			canvas.drawRect(rectf, style);
		}
		canvas.drawOval(rectf, Resource.paints.get("border.action_button"));
	}

	Path path = new Path();
	Rect rect = new Rect();
	RectF rectf = new RectF();
	@Override
	public void render(Canvas canvas, Camera camera) {
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		Rect targetedRect;
		for(Entry<String, List<Integer>> entry : targetedOrdersWithRect.entrySet()) {
			targetedRect = Rect.unflattenFromString(entry.getKey());
			camera.view(targetedRect);
			rectf.set(targetedRect);
			drawButton(canvas, Resource.images.get(currentAction.getResource()), rectf);
			canvas.clipRect(rectf, Region.Op.REPLACE);
			String orderString = entry.getValue().toString();
			orderString = orderString.substring(1, orderString.length() - 1);
			Paint paint = Resource.paints.get("text.action_button.order");
			paint.getTextBounds(orderString, 0, orderString.length(), rect);
			rect.offsetTo(targetedRect.left, targetedRect.top);
			canvas.drawText(orderString, rect.left, rect.bottom, paint);
		}
		rect.set(destRect);
		if(Rect.intersects(rect, camera.getViewRect())) {
			spread.reset();
			for(Action action : actions) {
				Integer result = results.get(action);
				if(result == null || result < 0) {
					continue;
				}
				do {
					int[] point = spread.next();
					rect.offsetTo(this.x + point[0], this.y + point[1]);
				} while(!camera.canView(rect) || hasIntersectedWithTargetedRects(rect));
				camera.view(rect);
				rectf.set(rect);
				drawButton(canvas, Resource.images.get(action.getResource()), rectf, result);
			}
			spread.forward(4);
			for(Action action : additionalActions) {
				do {
					int[] point = spread.next();
					rect.offsetTo(this.x + point[0], this.y + point[1]);
				} while(!camera.canView(rect) || hasIntersectedWithTargetedRects(rect));
				camera.view(rect);
				rectf.set(rect);
				drawButton(canvas, Resource.images.get(action.getResource()), rectf);
			}
		}
		canvas.restore();
	}

}
