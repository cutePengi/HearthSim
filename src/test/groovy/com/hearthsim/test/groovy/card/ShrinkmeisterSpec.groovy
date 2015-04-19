package com.hearthsim.test.groovy.card

import com.hearthsim.card.goblinsvsgnomes.minion.common.Shrinkmeister

import static com.hearthsim.model.PlayerSide.CURRENT_PLAYER
import static org.junit.Assert.*

import com.hearthsim.card.basic.minion.BoulderfistOgre
import com.hearthsim.model.BoardModel;
import com.hearthsim.test.helpers.BoardModelBuilder
import com.hearthsim.util.tree.HearthTreeNode;

class ShrinkmeisterSpec extends CardSpec {

    HearthTreeNode root
    BoardModel startingBoard

    def setup() {
        startingBoard = new BoardModelBuilder().make {
            currentPlayer {
                field([[minion: BoulderfistOgre]])
                mana(10)
            }
        }
        root = new HearthTreeNode(startingBoard)
    }

    def "subtracts extra attack"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = new Shrinkmeister()
        def ret = theCard.battlecryEffect.applyEffect(CURRENT_PLAYER, theCard, CURRENT_PLAYER, 1, root)

        expect:
        assertEquals(root, ret);

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                updateMinion(0, [deltaExtraAttack: -2])
            }
        }
    }

    def "buff is additive"() {
        def copiedBoard = startingBoard.deepCopy()
        def target = root.data_.modelForSide(CURRENT_PLAYER).getCharacter(1)
        target.extraAttackUntilTurnEnd = 2
        def theCard = new Shrinkmeister()
        def ret = theCard.battlecryEffect.applyEffect(CURRENT_PLAYER, theCard, CURRENT_PLAYER, 1, root)

        expect:
        assertEquals(root, ret);

        assertBoardDelta(copiedBoard, ret.data_) {
        }
    }
}