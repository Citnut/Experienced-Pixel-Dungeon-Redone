package com.shatteredpixel.citnutpixeldungeon.mod;

public interface GameMod {
	String id();

	default void onLoad() {
	}

	void onRegister(ModRegistry registry);
}
