package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.Beast;
import com.hearthsim.exception.HSInvalidPlayerIndexException;
import com.hearthsim.util.BoardState;
import com.hearthsim.util.HearthTreeNode;

public class Leokk extends Beast {

	public Leokk() {
		this(
				(byte)3,
				(byte)2,
				(byte)4,
				(byte)2,
				(byte)4,
				(byte)4,
				false,
				false,
				false,
				true, //has charge
				false,
				false,
				false,
				true, //by default summoned
				false,
				true,
				false
			);
	}
	
	public Leokk(	
							byte mana,
							byte attack,
							byte health,
							byte baseAttack,
							byte baseHealth,
							byte maxHealth,
							boolean taunt,
							boolean divineShield,
							boolean windFury,
							boolean charge,
							boolean hasAttacked,
							boolean hasWindFuryAttacked,
							boolean frozen,
							boolean summoned,
							boolean transformed,
							boolean isInHand,
							boolean hasBeenUsed) {
		
		super(
			"Leokk",
			mana,
			attack,
			health,
			baseAttack,
			baseHealth,
			maxHealth,
			taunt,
			divineShield,
			windFury,
			charge,
			hasAttacked,
			hasWindFuryAttacked,
			frozen,
			summoned,
			transformed,
			isInHand,
			hasBeenUsed);
	}
	
	
	/**
	 * 
	 * Use the card on the given target
	 * 
	 * Override for the temporary buff to attack
	 * 
	 * @param thisCardIndex The index (position) of the card in the hand
	 * @param playerIndex The index of the target player.  0 if targeting yourself or your own minions, 1 if targeting the enemy
	 * @param minionIndex The index of the target minion.
	 * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
	 * 
	 * @return The boardState is manipulated and returned
	 */
	@Override
	protected HearthTreeNode<BoardState> use_core(
			int thisCardIndex,
			int playerIndex,
			int minionIndex,
			HearthTreeNode<BoardState> boardState,
			Deck deck)
		throws HSInvalidPlayerIndexException
	{
		if (hasBeenUsed_) {
			//Card is already used, nothing to do
			return null;
		}
		
		if (playerIndex == 1 || minionIndex == 0)
			return null;
		
		if (boardState.data_.getNumMinions_p0() < 7) {

			if (!charge_) {
				hasAttacked_ = true;
			}
			hasBeenUsed_ = true;
			boardState.data_.placeMinion_p0(this, minionIndex - 1);
			boardState.data_.setMana_p0(boardState.data_.getMana_p0() - this.mana_);
			boardState.data_.removeCard_hand(thisCardIndex);
			
			for (Minion minion : boardState.data_.getMinions_p0()) {
				if (minion != this) {
					minion.setAttack((byte)(minion.getAttack() + 1));
				}
			}
			
			return boardState;
							
		} else {
			return null;
		}
	}
	
	/**
	 * Called when this minion is silenced
	 * 
	 * Override for the aura effect
	 * 
	 * @param thisPlayerIndex The player index of this minion
	 * @param thisMinionIndex The minion index of this minion
	 * @param boardState 
	 * @param deck
	 * @throws HSInvalidPlayerIndexException
	 */
	public void silenced(int thisPlayerIndex, int thisMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) throws HSInvalidPlayerIndexException {
		for (Minion minion : boardState.data_.getMinions_p0()) {
			if (minion != this) {
				minion.setAttack((byte)(minion.getAttack() - 1));
			}
		}
	}
	
	/**
	 * Called when this minion dies (destroyed)
	 * 
	 * Override for the aura effect
	 * 
	 * @param thisPlayerIndex The player index of this minion
	 * @param thisMinionIndex The minion index of this minion
	 * @param boardState 
	 * @param deck
	 * @throws HSInvalidPlayerIndexException
	 */
	public void destroyed(int thisPlayerIndex, int thisMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) throws HSInvalidPlayerIndexException {
		
		for (Minion minion : boardState.data_.getMinions_p0()) {
			if (minion != this) {
				minion.setAttack((byte)(minion.getAttack() - 1));
			}
		}
		super.destroyed(thisPlayerIndex, thisMinionIndex, boardState, deck);
	}
	
	private HearthTreeNode<BoardState> doBuffs(int targetMinionPlayerIndex, int targetMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) {
		if (targetMinionPlayerIndex == 1)
			return boardState;
		Minion minion = boardState.data_.getMinion_p0(targetMinionIndex - 1);
		if (minion != this)
			minion.setAttack((byte)(minion.getAttack() + 1));
		return boardState;		
	}
	
	/**
	 * 
	 * Called whenever another minion is placed on board
	 * 
	 * Override for the aura effect
	 *
	 * @param playerIndex The index of the created minion's player.  0 if targeting yourself or your own minions, 1 if targeting the enemy
	 * @param minionIndex The index of the created minion.
	 * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
	 * 
	 * @return The boardState is manipulated and returned
	 */
	@Override
	public HearthTreeNode<BoardState> minionPlacedEvent(int placedMinionPlayerIndex, int placedMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) {
		return this.doBuffs(placedMinionPlayerIndex, placedMinionIndex, boardState, deck);
	}

	/**
	 * 
	 * Called whenever another minion comes on board
	 * 
	 * Override for the aura effect
	 *
	 * @param playerIndex The index of the created minion's player.  0 if targeting yourself or your own minions, 1 if targeting the enemy
	 * @param minionIndex The index of the created minion.
	 * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
	 * 
	 * @return The boardState is manipulated and returned
	 */
	public HearthTreeNode<BoardState> minionSummonedEvent(int summonedMinionPlayerIndex, int summeonedMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) {
		return this.doBuffs(summonedMinionPlayerIndex, summeonedMinionIndex, boardState, deck);
	}
	
	/**
	 * 
	 * Called whenever another minion is summoned using a spell
	 * 
	 * @param playerIndex The index of the created minion's player.  0 if targeting yourself or your own minions, 1 if targeting the enemy
	 * @param minionIndex The index of the created minion.
	 * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
	 * 
	 * @return The boardState is manipulated and returned
	 */
	public HearthTreeNode<BoardState> minionTransformedEvent(int transformedMinionPlayerIndex, int transformedMinionIndex, HearthTreeNode<BoardState> boardState, Deck deck) {
		return this.doBuffs(transformedMinionPlayerIndex, transformedMinionIndex, boardState, deck);
	}

}
