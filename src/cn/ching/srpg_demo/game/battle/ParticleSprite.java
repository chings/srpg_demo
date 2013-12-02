package cn.ching.srpg_demo.game.battle;

import cn.ching.srpg_demo.game.core.Sprite;

abstract public class ParticleSprite extends Sprite {

	abstract public void onFade(Battler owner);

}