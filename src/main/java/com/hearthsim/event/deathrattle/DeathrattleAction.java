package com.hearthsim.event.deathrattle;

import com.hearthsim.card.Card;
import com.hearthsim.card.Deck;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;

/**
 * Actions triggered by a deathrattle
 */
public abstract class DeathrattleAction<T extends Card> {

    /**
     * Perform the action
     *
     * @param origin      The minion that is performing the action (aka, the dying minion)
     * @param playerSide
     * @param boardState
     * @throws HSException
     */
    public HearthTreeNode performAction(T origin,
                                        PlayerSide playerSide,
                                        HearthTreeNode boardState,
                                        boolean singleRealizationOnly) {
        return boardState;
    }

    @Deprecated
    public HearthTreeNode performAction(T origin,
                                        PlayerSide playerSide,
                                        HearthTreeNode boardState,
                                        Deck deckPlayer0,
                                        Deck deckPlayer1,
                                        boolean singleRealizationOnly) throws HSException {
        return this.performAction(origin, playerSide, boardState, singleRealizationOnly);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof DeathrattleAction))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
