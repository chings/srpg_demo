package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.ai.Intelligence;
import cn.ching.srpg_demo.game.core.Camera;
import cn.ching.srpg_demo.game.core.Context;
import cn.ching.srpg_demo.game.core.Sprite;
import cn.ching.srpg_demo.game.core.SpriteListener;
import cn.ching.srpg_demo.game.core.Thing;
import cn.ching.srpg_demo.game.data.Loader;
import cn.ching.srpg_demo.game.ui.TextTip;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Battler implements Thing, SpriteListener, Context, Turner {

	public static final int NORMAL = 0;
	public static final int DEAD = -1;

	BattleScene battle;
	Party party;

	Character character;
	BattlerSprite sprite;
	int[] position;
	int orientation;

	int status;
	int life;
	int energy;
	int activity;

	Command command;
	LinkedList<Command> commands;
	CommandSource commandSource;
	Intelligence intelligence;

	List<ParticleSprite> particleSprites;

	public Battler() {
		commands = new LinkedList<Command>();
		particleSprites = new CopyOnWriteArrayList<ParticleSprite>();
	}

	@Loader
	public void loadCharacter(Character character) {
		this.character = character;
		sprite = new BattlerSprite(character.resource, this);
		life = getInt("maxLife");
		energy = (getInt("maxEnergy", 0) >> 1) -10;
	}

	@Loader
	public void loadPosition(int[] position) {
		this.position = position;
	}

	@Loader
	public void loadOrientation(int orientation) {
		this.orientation = orientation;
		if(sprite != null) {
			sprite.setOrientation(orientation);
		}
	}

	public BattleScene getBattle() {
		return battle;
	}

	public void setBattle(BattleScene battle) {
		this.battle = battle;
	}

	public Party getParty() {
		return party;
	}

	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position[0] = position[0];
		this.position[1] = position[1];
	}

	public void setPosition(int m, int n) {
		this.position[0] = m;
		this.position[1] = n;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		if(orientation == 0) {
			return;
		}
		this.orientation = orientation;
		sprite.setOrientation(orientation);
	}

	public boolean with(String key) {
		return character.with(key);
	}

	public String getString(String key) {
		return character.getString(key);
	}

	public String getString(String key, String defaultValue) {
		return character.getString(key, defaultValue);
	}

	public Boolean getBoolean(String key) {
		return character.getBoolean(key);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return character.getBoolean(key, defaultValue);
	}

	public Integer getInt(String key) {
		return character.getInt(key);
	}

	public int getInt(String key, int defaultValue) {
		return character.getInt(key, defaultValue);
	}

	public int[] getInts(String key) {
		return character.getInts(key);
	}

	public int[] getInts(String key, int[] defaultValue) {
		return character.getInts(key, defaultValue);
	}

	public Float getFloat(String key) {
		return character.getFloat(key);
	}

	public float getFloat(String key, float defaultValue) {
		return character.getFloat(key, defaultValue);
	}

	public int getStatus() {
		return status;
	}

	public boolean isFallen() {
		return status == Battler.DEAD;
	}

	public Character getCharacter() {
		return character;
	}

	public BattlerSprite getSprite() {
		return sprite;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
		if(life <= 0) {
			fall();
		}
	}

	public int modifyLife(int increment) {
		int many = limitIncrement(increment, life, 0, getInt("maxLife"));
		life += many;
		if(life <= 0) {
			fall();
		}
		return many;
	}

	public void fall() {
		endTurn();
		life = 0;
		status = Battler.DEAD;
		sprite.setDefaultMotion("die");
		battle.onBattlerFall(this);
		party.onMemberFall(this);
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int modifyEnergy(int increment) {
		int many = limitIncrement(increment, energy, 0, getInt("maxEnergy"));
		energy += many;
		return many;
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
		if(activity <= 0) {
			endTurn();
		}
	}

	public int modifyActivity(int increment) {
		int many = limitIncrement(increment, activity, 0, getInt("maxActivity"));
		activity += many;
		if(activity <= 0) {
			endTurn();
		}
		return many;
	}

	public void endTurn() {
		activity = 0;
		commands.clear();
		commandSource = null;
	}

	public boolean isBusy() {
		return command != null || !commands.isEmpty() || !particleSprites.isEmpty();
	}

	public void take(Command command) {
		commands.add(command);
	}

	public void take(List<Command> commands) {
		if(commands == null || commands.isEmpty()) {
			return;
		}
		for(Command command : commands) {
			this.commands.add(command);
		}
	}

	public void setCommandSource(CommandSource commandSource) {
		this.commandSource = commandSource;
	}

	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	public boolean isPlayer() {
		return intelligence == null;
	}

	@Override
	public void tick() {
		sprite.tick();
		for(ParticleSprite particle : particleSprites) {
			particle.tick();
		}
	}

	@Override
	public void onMotionEnd(Sprite sprite) {
		if(sprite == this.sprite) {
			if(command != null) {
				command.postExecute(this);
				command = null;
			}
			if(commands.isEmpty() && commandSource != null) {
				if(!commandSource.suggest(this)) {
					commandSource = null;
				}
			}
			String motion = null;
			if(!commands.isEmpty()) {
				command = commands.removeFirst();
				command.execute(this);
				motion = command.getMotion();
			}
			if(motion != null) {
				this.sprite.play(motion, command.getInterpolator());
			} else {
				this.sprite.play();
			}
		} else if(sprite instanceof ParticleSprite) {
			ParticleSprite particleSprite = (ParticleSprite)sprite;
			particleSprite.onFade(this);
			particleSprites.remove(particleSprite);
		}
	}

	public void onRoundStart(int round) {
		modifyEnergy(10);
		setActivity(getInt("maxActivity"));
	}

	public void onTurnStart(int turn) {
		if(!isPlayer() && activity > 0) {
			intelligence.suggest(this);
		}
	}

	public void onTurnEnd(int turn) {
		endTurn();
	}

	public void onRoundEnd(int round) {

	}

	public void renderSprite(Canvas canvas, Camera  camera) {
		sprite.render(canvas, camera);
	}

	public void renderAdditions(Canvas canvas, Camera  camera) {
		for(ParticleSprite particle : particleSprites) {
			particle.render(canvas, camera);
		}
		if(status != DEAD) {
			Rect rect = sprite.getDestRect();
			camera.view(rect);
			canvas.drawRect(rect.left + 7, rect.top - 4, rect.right - 7, rect.top, Resource.paints.get("background.lifebar"));
			int length = Math.round(50f * life / getInt("maxLife"));
			canvas.drawRect(rect.left + 7, rect.top - 4, rect.left + 7 + length, rect.top, isPlayer() ? Resource.paints.get("foreground.lifebar.pc") : Resource.paints.get("foreground.lifebar.npc"));
		}
	}

	@Override
	public String toString() {
		return character != null ? character.name : super.toString();
	}

	public static int limitIncrement(int increment, int current, int min, int max) {
		if(increment < 0) {
			int n = min - current;
			return increment < n ? n : increment;
		} else {
			int n = max - current;
			return increment > n ? n : increment;
		}
	}

	public void attach(ParticleSprite particleSprite) {
		particleSprite.setListener(this);
		particleSprites.add(particleSprite);
	}

	public void attachLifeTip(int many, boolean critical) {
		Rect rect = sprite.getDestRect();
		if(many > 0) {
			String tip = "+" + many;
			if(critical) {
				tip += "!";
			}
			attach(new TextTip(tip, Resource.paints.get("text.green"), rect.left + 32, rect.top));
		} else if(many < 0) {
			String tip = "-" + many;
			if(critical) {
				tip += "!";
			}
			attach(new TextTip(tip, Resource.paints.get("text.red"), rect.left + 32, rect.top));
		}
	}

	public List<Weapon> getWeapons() {
		return character.getWeapons();
	}

	public List<Ability> getAllAbilities() {
		List<Ability> abilities = new ArrayList<Ability>();
		for(Weapon weapon : character.getWeapons()) {
			for(Ability ability : weapon.getAbilities()) {
				if(ability instanceof InstrumentAbility) {
					((InstrumentAbility)ability).setInstrument(weapon);
				}
				abilities.add(ability);
			}
		}
		for(Ability ability : character.getAbilities()) {
			if(ability instanceof InstrumentAbility) {
				((InstrumentAbility)ability).setInstruments(((InstrumentAbility)ability).findInstruments(this));
			}
			abilities.add(ability);
		}
		return abilities;
	}

}
