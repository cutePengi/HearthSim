package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.Card;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.MinionBattlecryInterface;
import com.hearthsim.event.effect.EffectCharacter;
import com.hearthsim.event.effect.EffectCharacterBuffDelta;
import com.hearthsim.event.filter.FilterCharacter;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;

public class ColdlightSeer extends Minion implements MinionBattlecryInterface {

    private static final EffectCharacter<Card> effect = new EffectCharacterBuffDelta<>(0, 2);

    private static final FilterCharacter filter = new FilterCharacter() {
        @Override
        protected boolean includeOwnMinions() {
            return true;
        }

        @Override
        protected MinionTribe tribeFilter() {
            return MinionTribe.MURLOC;
        }

        @Override
        protected boolean excludeSource() {
            return true;
        }
    };

    public ColdlightSeer() {
        super();
    }

    @Override
    public EffectCharacter<Minion> getBattlecryEffect() {
        return (PlayerSide originSide, Minion origin, PlayerSide targetSide, int minionPlacementIndex, HearthTreeNode boardState) -> {
            return this.effectAllUsingFilter(ColdlightSeer.effect, ColdlightSeer.filter, boardState);
        };
    }
}
