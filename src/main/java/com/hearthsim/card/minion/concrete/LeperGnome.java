package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.minion.Minion;
import com.hearthsim.event.deathrattle.DeathrattleDealDamageEnemyHeroAction;

public class LeperGnome extends Minion {

	private static final boolean HERO_TARGETABLE = true;
	private static final byte SPELL_DAMAGE = 0;
	
	public LeperGnome() {
        super();
        spellDamage_ = SPELL_DAMAGE;
        heroTargetable_ = HERO_TARGETABLE;

        deathrattleAction_ = new DeathrattleDealDamageEnemyHeroAction((byte)2);
	}
}
