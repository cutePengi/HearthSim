package com.hearthsim.util;

import com.hearthsim.Game;
import com.hearthsim.card.Card;
import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Hero;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;


/**
 * A class representing an action that a player can take
 * 
 *
 */
public class HearthAction {
	
	// TODO the DO_NOT_ verbs are used for history tracking but we can probably optimize them away in the future.
	public enum Verb {
		USE_CARD, HERO_ABILITY, ATTACK, UNTARGETABLE_BATTLECRY, TARGETABLE_BATTLECRY, START_TURN, END_TURN, DO_NOT_USE_CARD, DO_NOT_ATTACK, DO_NOT_USE_HEROPOWER
	}
		
	public final Verb verb_;
	
	public final PlayerSide actionPerformerPlayerSide;
	public final int cardOrCharacterIndex_;
	
	public final PlayerSide targetPlayerSide;
	public final int targetCharacterIndex_;
	
	public final PlayerSide battlecryTargetPlayerSide;
	public final int battlecryTargetCharacterIndex;
	
	public HearthAction(Verb verb, PlayerSide actionPerformerPlayerSide, int cardOrCharacterIndex, PlayerSide targetPlayerSide, int targetCharacterIndex) {
		this(verb, actionPerformerPlayerSide, cardOrCharacterIndex, targetPlayerSide, targetCharacterIndex, null, -1);
	}

	public HearthAction(Verb verb, PlayerSide actionPerformerPlayerSide, int cardOrCharacterIndex, PlayerSide targetPlayerSide, int targetCharacterIndex, PlayerSide battlecryTargetPlayerSide, int battlecryTargetCharacterIndex) {
		verb_ = verb;
		this.actionPerformerPlayerSide = actionPerformerPlayerSide;
		cardOrCharacterIndex_ = cardOrCharacterIndex;

		this.targetPlayerSide = targetPlayerSide;
		targetCharacterIndex_ = targetCharacterIndex;
		
		this.battlecryTargetPlayerSide = battlecryTargetPlayerSide;
		this.battlecryTargetCharacterIndex = battlecryTargetCharacterIndex;
	}

	public HearthTreeNode perform(HearthTreeNode boardState, Deck deckPlayer0, Deck deckPlayer1) throws HSException {
		return this.perform(boardState, deckPlayer0, deckPlayer1, true);
	}

	public HearthTreeNode perform(HearthTreeNode boardState, Deck deckPlayer0, Deck deckPlayer1, boolean singleRealization) throws HSException {
		HearthTreeNode toRet = boardState;
		switch(verb_) {
			case USE_CARD: {
				Card card = boardState.data_.getCard_hand(actionPerformerPlayerSide, cardOrCharacterIndex_);
				Minion target = boardState.data_.getCharacter(targetPlayerSide, targetCharacterIndex_);
				toRet = card.useOn(targetPlayerSide, target, toRet, deckPlayer0, deckPlayer1, singleRealization);
			}
			break;
			case HERO_ABILITY: {
				Hero hero = boardState.data_.getHero(actionPerformerPlayerSide);
				Minion target = boardState.data_.getCharacter(targetPlayerSide, targetCharacterIndex_);
				toRet = hero.useHeroAbility(targetPlayerSide, target, toRet, deckPlayer0, deckPlayer1, singleRealization);
			}
			break;
			case ATTACK: {
				Minion attacker = boardState.data_.getCharacter(actionPerformerPlayerSide, cardOrCharacterIndex_);
				Minion target = boardState.data_.getCharacter(targetPlayerSide, targetCharacterIndex_);
				toRet = attacker.attack(targetPlayerSide, target, toRet, deckPlayer0, deckPlayer1);
			}
			break;
			case UNTARGETABLE_BATTLECRY: {
				Minion minion = boardState.data_.getCharacter(actionPerformerPlayerSide, cardOrCharacterIndex_);
				Minion placementTarget = boardState.data_.getCharacter(targetPlayerSide, targetCharacterIndex_);
				toRet = minion.useUntargetableBattlecry(placementTarget, toRet, deckPlayer0, deckPlayer1, singleRealization);
				break;
			}
			case TARGETABLE_BATTLECRY: {
				Minion minion = boardState.data_.getCharacter(actionPerformerPlayerSide, cardOrCharacterIndex_);
				Minion battlecryTarget = boardState.data_.getCharacter(targetPlayerSide, targetCharacterIndex_);
				toRet = minion.useTargetableBattlecry(targetPlayerSide, battlecryTarget, toRet, deckPlayer0, deckPlayer1);
				break;
			}
			case START_TURN: {
				toRet = new HearthTreeNode(Game.beginTurn(boardState.data_.deepCopy()));
				break;
			}
			case END_TURN: {
				toRet = new HearthTreeNode(Game.endTurn(boardState.data_.deepCopy()).flipPlayers());
				break;
			}
			case DO_NOT_USE_CARD: {
				for(Card c : boardState.data_.getCurrentPlayerHand()) {
					c.hasBeenUsed(true);
				}
				break;
			}
			case DO_NOT_ATTACK: {
				for(Minion minion : PlayerSide.CURRENT_PLAYER.getPlayer(boardState).getMinions()) {
					minion.hasAttacked(true);
				}
				boardState.data_.getCurrentPlayerHero().hasAttacked(true);
				break;
			}
			case DO_NOT_USE_HEROPOWER: {
				boardState.data_.getCurrentPlayerHero().hasBeenUsed(true);
				break;
			}
		}
		return toRet;
	}
}
