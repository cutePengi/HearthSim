package com.hearthsim;

import java.util.List;

import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.spellcard.concrete.TheCoin;
import com.hearthsim.exception.HSException;
import com.hearthsim.exception.HSInvalidPlayerIndexException;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.player.playercontroller.ArtificialPlayer;
import com.hearthsim.results.GameRecord;
import com.hearthsim.results.GameResult;
import com.hearthsim.results.GameSimpleRecord;
import com.hearthsim.util.HearthActionBoardPair;
import com.hearthsim.util.factory.BoardStateFactoryBase;
import com.hearthsim.util.tree.HearthTreeNode;

public class Game {
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	final static int maxTurns_ = 100;

    BoardModel boardModel;
    
    PlayerModel playerGoingFirst;
    PlayerModel playerGoingSecond;
    
    ArtificialPlayer aiForPlayerGoingFirst;
    ArtificialPlayer aiForPlayerGoingSecond;

	int curTurn_;
	
	public Game(PlayerModel playerModel0, PlayerModel playerModel1, ArtificialPlayer ai0, ArtificialPlayer ai1) {
		this(playerModel0, playerModel1, ai0, ai1, false);
	}
	
	public Game(PlayerModel playerModel0, PlayerModel playerModel1, ArtificialPlayer ai0, ArtificialPlayer ai1, int firstPlayerId) {
		playerGoingFirst = playerModel0;
        playerGoingSecond = playerModel1;
        
        aiForPlayerGoingFirst = ai0;
        aiForPlayerGoingSecond = ai1;

        if (firstPlayerId == 1) {
            playerGoingFirst = playerModel1;
            playerGoingSecond = playerModel0;
            aiForPlayerGoingFirst = ai1;
            aiForPlayerGoingSecond = ai0;
        }
        log.debug("alternate play order: {}", firstPlayerId);
        log.debug("first player id: {}", playerGoingFirst.getPlayerId());

		boardModel = new BoardModel(playerGoingFirst, playerGoingSecond);
		
	}
	
	public Game(PlayerModel playerModel0, PlayerModel playerModel1, ArtificialPlayer ai0, ArtificialPlayer ai1, boolean shufflePlayOrder) {
        
		playerGoingFirst = playerModel0;
        playerGoingSecond = playerModel1;
        
        aiForPlayerGoingFirst = ai0;
        aiForPlayerGoingSecond = ai1;

        if (shufflePlayOrder && Math.random() > 0.5) {
            playerGoingFirst = playerModel1;
            playerGoingSecond = playerModel0;
            aiForPlayerGoingFirst = ai1;
            aiForPlayerGoingSecond = ai0;
        }
        log.debug("shuffle play order: {}", shufflePlayOrder);
        log.debug("first player id: {}", playerGoingFirst.getPlayerId());

		boardModel = new BoardModel(playerGoingFirst, playerGoingSecond);
	}
	
	public GameResult runGame() throws HSException {
		curTurn_ = 0;

		//the first player draws 3 cards
		boardModel.placeCardHandCurrentPlayer(0);
		boardModel.placeCardHandCurrentPlayer(1);
		boardModel.placeCardHandCurrentPlayer(2);
		boardModel.getCurrentPlayer().setDeckPos(3);

		//the second player draws 4 cards
		boardModel.placeCardHandWaitingPlayer(0);
		boardModel.placeCardHandWaitingPlayer(1);
		boardModel.placeCardHandWaitingPlayer(2);
		boardModel.placeCardHandWaitingPlayer(3);
		boardModel.placeCardHandWaitingPlayer(new TheCoin());
		boardModel.getWaitingPlayer().setDeckPos(4);
		
		GameRecord record = new GameSimpleRecord();

		record.put(0, PlayerSide.CURRENT_PLAYER, (BoardModel) boardModel.deepCopy(), null);
		record.put(0, PlayerSide.CURRENT_PLAYER, (BoardModel) boardModel.flipPlayers().deepCopy(), null);

        GameResult gameResult;
        for (int turnCount = 0; turnCount < maxTurns_; ++turnCount) {
            log.debug("starting turn " + turnCount);
            long turnStart = System.currentTimeMillis();

            gameResult = playTurn(turnCount, record, aiForPlayerGoingFirst);
            if (gameResult != null)
                return gameResult;

            gameResult = playTurn(turnCount, record, aiForPlayerGoingSecond);
            if (gameResult != null)
                return gameResult;

            long turnEnd = System.currentTimeMillis();
            long turnDelta = turnEnd - turnStart;
            if (turnDelta > aiForPlayerGoingFirst.getMaxThinkTime() / 2) {
                log.warn("turn took {} ms, more than half of alloted think time ({})", turnDelta, aiForPlayerGoingFirst.getMaxThinkTime());
            } else {
                log.debug("turn took {} ms", turnDelta);
            }

		}
		return new GameResult(playerGoingFirst.getPlayerId(), -1, 0, record);
	}

    private GameResult playTurn(int turnCount, GameRecord record, ArtificialPlayer ai) throws HSException {
        beginTurn(boardModel);

        GameResult gameResult;

        gameResult = checkGameOver(turnCount, record);
        if (gameResult != null) return gameResult;

        List<HearthActionBoardPair> allMoves = playAITurn(turnCount, boardModel, ai);
        if (allMoves.size() > 0) {
        	//If allMoves is empty, it means that there was absolutely nothing the AI could do
            boardModel = allMoves.get(allMoves.size() - 1).board;        	
        }
        
        boardModel = endTurn(boardModel);

        record.put(turnCount + 1, PlayerSide.CURRENT_PLAYER, (BoardModel) boardModel.deepCopy(), allMoves);

        gameResult = checkGameOver(turnCount, record);
        if (gameResult != null) return gameResult;

        boardModel = boardModel.flipPlayers();

        return null;
    }

    public GameResult checkGameOver(int turnCount, GameRecord record){
        if (!boardModel.isAlive(PlayerSide.CURRENT_PLAYER)) {
        	PlayerModel winner = boardModel.modelForSide(PlayerSide.WAITING_PLAYER);
            return new GameResult(playerGoingFirst.getPlayerId(), winner.getPlayerId(), turnCount + 1, record);
        } else if (!boardModel.isAlive(PlayerSide.WAITING_PLAYER)) {
        	PlayerModel winner = boardModel.modelForSide(PlayerSide.CURRENT_PLAYER);
            return new GameResult(playerGoingFirst.getPlayerId(), winner.getPlayerId(), turnCount + 1, record);
        } 
        return null;
    }

    
    public static BoardModel beginTurn(BoardModel board) throws HSException {

    	HearthTreeNode toRet = new HearthTreeNode(board);
        
    	toRet.data_.resetHand();
    	toRet.data_.resetMinions();
        
        for (Minion targetMinion : toRet.data_.getCurrentPlayer().getMinions()) {
            try {
                toRet = targetMinion.startTurn(PlayerSide.CURRENT_PLAYER, toRet, toRet.data_.getCurrentPlayer().getDeck(), toRet.data_.getWaitingPlayer().getDeck());
            } catch (HSInvalidPlayerIndexException e) {
                e.printStackTrace();
            }
        }
        for (Minion targetMinion : toRet.data_.getWaitingPlayer().getMinions()) {
            try {
            	toRet = targetMinion.startTurn(PlayerSide.WAITING_PLAYER, toRet, toRet.data_.getCurrentPlayer().getDeck(), toRet.data_.getWaitingPlayer().getDeck());
            } catch (HSInvalidPlayerIndexException e) {
                e.printStackTrace();
            }
        }
        toRet = BoardStateFactoryBase.handleDeadMinions(toRet, toRet.data_.getCurrentPlayer().getDeck(), toRet.data_.getWaitingPlayer().getDeck());        
        
        
        toRet.data_.getCurrentPlayer().drawNextCardFromDeck();
        if (toRet.data_.getCurrentPlayer().getMaxMana() < 10)
        	toRet.data_.getCurrentPlayer().addMaxMana(1);
        toRet.data_.resetMana();
        
        return toRet.data_;
    }

    public List<HearthActionBoardPair> playAITurn(int turn, BoardModel board, ArtificialPlayer ai) throws HSException {
        return ai.playTurn(turn, board);
    }

    public static BoardModel endTurn(BoardModel board) throws HSException {
    	Deck deckPlayer0 = board.getCurrentPlayer().getDeck();
    	Deck deckPlayer1 = board.getWaitingPlayer().getDeck();
    	
    	HearthTreeNode toRet = new HearthTreeNode(board);
    	
    	toRet = toRet.data_.getCurrentPlayer().getHero().endTurn(PlayerSide.CURRENT_PLAYER, toRet, deckPlayer0, deckPlayer1);
    	toRet = toRet.data_.getWaitingPlayer().getHero().endTurn(PlayerSide.WAITING_PLAYER, toRet, deckPlayer0, deckPlayer1);

    	//To Do: The minions should trigger end-of-turn effects in the order that they were played
        for (int index = 0; index < toRet.data_.getCurrentPlayer().getMinions().size(); ++index) {
            Minion targetMinion = toRet.data_.getCurrentPlayer().getMinions().get(index);
            try {
                toRet = targetMinion.endTurn(PlayerSide.CURRENT_PLAYER, toRet, deckPlayer0, deckPlayer1);
            } catch (HSException e) {
                e.printStackTrace();
            }
        }
        for (int index = 0; index < toRet.data_.getWaitingPlayer().getMinions().size(); ++index) {
            Minion targetMinion = toRet.data_.getWaitingPlayer().getMinions().get(index);
            try {
                toRet = targetMinion.endTurn(PlayerSide.WAITING_PLAYER, toRet, deckPlayer0, deckPlayer1);
            } catch (HSException e) {
                e.printStackTrace();
            }
        }

        toRet = BoardStateFactoryBase.handleDeadMinions(toRet, toRet.data_.getCurrentPlayer().getDeck(), toRet.data_.getWaitingPlayer().getDeck());
        
        return toRet.data_;
    }
}
