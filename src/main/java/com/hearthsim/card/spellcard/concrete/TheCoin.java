package com.hearthsim.card.spellcard.concrete;

import com.hearthsim.card.spellcard.SpellTargetableCard;
import com.hearthsim.event.effect.EffectCharacter;
import com.hearthsim.event.filter.FilterCharacter;
import com.hearthsim.event.filter.FilterCharacterTargetedSpell;
import org.json.JSONObject;

public class TheCoin extends SpellTargetableCard {

    @Deprecated
    public TheCoin(boolean hasBeenUsed) {
        this();
        this.hasBeenUsed = hasBeenUsed;
    }

    public TheCoin() {
        super();
    }

    @Override
    public FilterCharacter getTargetableFilter() {
        return FilterCharacterTargetedSpell.SELF;
    }

    /**
     *
     * Use the card on the given target
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
                byte newMana = boardState.data_.getCurrentPlayer().getMana();
                newMana = newMana >= 10 ? newMana : (byte)(newMana + 1);
                boardState.data_.getCurrentPlayer().setMana(newMana);
                return boardState;
            };
        }
        return this.effect;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "SpellTheCoin");
        return json;
    }
}
