package com.hearthsim.card.spellcard.concrete;

import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.Minion.MinionTribe;
import com.hearthsim.card.spellcard.SpellTargetableCard;
import com.hearthsim.event.effect.EffectCharacter;
import com.hearthsim.event.filter.FilterCharacter;
import com.hearthsim.event.filter.FilterCharacterTargetedSpell;

public class SacrificialPact extends SpellTargetableCard {

    private final static FilterCharacter filter = new FilterCharacterTargetedSpell() {
        @Override
        protected boolean includeEnemyHero() {
            return true;
        }

        @Override
        protected boolean includeEnemyMinions() {
            return true;
        }

        @Override
        protected boolean includeOwnMinions() {
            return true;
        }

        @Override
        protected MinionTribe tribeFilter() {
            return MinionTribe.DEMON;
        }
    };

    /**
     * Constructor
     *
     * Defaults to hasBeenUsed = false
     */
    public SacrificialPact() {
        super();
    }

    @Override
    public FilterCharacter getTargetableFilter() {
        return SacrificialPact.filter;
    }

    /**
     *
     * Use the card on the given target
     *
     * Gives a destroy a demon
     *
     *
     *
     * @param side
     * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
     *
     * @return The boardState is manipulated and returned
     */
    @Override
    public EffectCharacter getTargetableEffect() {
        if (this.effect == null) {
            this.effect = (originSide, origin, targetSide, targetCharacterIndex, boardState) -> {
                Minion targetCharacter = boardState.data_.getCharacter(targetSide, targetCharacterIndex);
                boardState = boardState.data_.modelForSide(originSide).getHero().takeHealAndNotify((byte) 5, originSide, boardState);
                targetCharacter.setHealth((byte) -99);
                return boardState;
            };
        }
        return this.effect;
    }
}
