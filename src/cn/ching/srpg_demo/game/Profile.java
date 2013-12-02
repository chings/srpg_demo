package cn.ching.srpg_demo.game;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.battle.PlayerCharacter;

public class Profile {

	public static Profile current = new Profile();

	public PlayerCharacter getPlayerCharacter(int n) {
		switch(n) {
		case 0:
			return (PlayerCharacter)Resource.data.load(R.raw.character_leoht, new PlayerCharacter());
		case 1:
			return (PlayerCharacter)Resource.data.load(R.raw.character_ariel, new PlayerCharacter());
		case 2:
			return (PlayerCharacter)Resource.data.load(R.raw.character_zaen, new PlayerCharacter());
		default:
			return null;
		}
	}

}
