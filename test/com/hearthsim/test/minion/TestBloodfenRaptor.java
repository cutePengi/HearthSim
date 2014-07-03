package com.hearthsim.test.minion;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.hearthsim.card.Card;
import com.hearthsim.card.minion.Hero;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.concrete.BloodfenRaptor;
import com.hearthsim.util.BoardState;

public class TestBloodfenRaptor {


	private BoardState board;
	private static final byte mana = 2;
	private static final byte attack0 = 2;
	private static final byte health0 = 3;
	private static final byte health1 = 7;

	@Before
	public void setup() {
		board = new BoardState();

		Minion minion0_0 = new Minion("" + 0, mana, attack0, health0, attack0, health0, health0);
		Minion minion0_1 = new Minion("" + 0, mana, attack0, health1, attack0, health1, health1);
		Minion minion1_0 = new Minion("" + 0, mana, attack0, health0, attack0, health0, health0);
		Minion minion1_1 = new Minion("" + 0, mana, attack0, health1, attack0, health1, health1);
		
		board.placeMinion_p0(minion0_0);
		board.placeMinion_p0(minion0_1);
		
		board.placeMinion_p1(minion1_0);
		board.placeMinion_p1(minion1_1);
				
	}
	
	@Test
	public void test0() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 0, 0, board);
		
		assertTrue("test0_0", ret == null);
		assertTrue("test0_1", board.getNumCards_hand() == 1);
		assertTrue("test0_2", board.getNumMinions_p0() == 2);
		assertTrue("test0_3", board.getNumMinions_p1() == 2);
		assertTrue("test0_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test0_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test0_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test0_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test0_8", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test0_9", board.getMinion_p1(1).getHealth() == health1);
	}
	
	@Test
	public void test1() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 1, 0, board);
		
		assertTrue("test1_0", ret == null);
		assertTrue("test1_1", board.getNumCards_hand() == 1);
		assertTrue("test1_2", board.getNumMinions_p0() == 2);
		assertTrue("test1_3", board.getNumMinions_p1() == 2);
		assertTrue("test1_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test1_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test1_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test1_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test1_8", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test1_9", board.getMinion_p1(1).getHealth() == health1);
	}
	
	@Test
	public void test2() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 0, 1, board);
		
		assertFalse("test2_0", ret == null);
		assertTrue("test2_1", board.getNumCards_hand() == 0);
		assertTrue("test2_2", board.getNumMinions_p0() == 3);
		assertTrue("test2_3", board.getNumMinions_p1() == 2);
		assertTrue("test2_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test2_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test2_6", board.getMinion_p0(0).getHealth() == fb.getHealth());
		assertTrue("test2_7", board.getMinion_p0(1).getHealth() == health0);
		assertTrue("test2_8", board.getMinion_p0(2).getHealth() == health1);
		assertTrue("test2_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test2_10", board.getMinion_p1(1).getHealth() == health1);
		
		Minion theAttacker = board.getMinion_p0(0);
		ret = theAttacker.useOn(0, 0, 0, board);
		assertTrue("test2", ret == null);

		theAttacker = board.getMinion_p0(0);
		ret = theAttacker.useOn(0, 1, 0, board);
		assertTrue("test2", ret == null);

		theAttacker = board.getMinion_p0(0);
		theAttacker.hasAttacked(false);
		theAttacker.hasBeenUsed(false);
		ret = theAttacker.useOn(0, 1, 0, board);
		assertFalse("test2_0", ret == null);
		assertTrue("test2_1", board.getNumCards_hand() == 0);
		assertTrue("test2_2", board.getNumMinions_p0() == 3);
		assertTrue("test2_3", board.getNumMinions_p1() == 2);
		assertTrue("test2_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test2_5", board.getHero_p1().getHealth() == 27);
		assertTrue("test2_6", board.getMinion_p0(0).getHealth() == fb.getHealth());
		assertTrue("test2_7", board.getMinion_p0(1).getHealth() == health0);
		assertTrue("test2_8", board.getMinion_p0(2).getHealth() == health1);
		assertTrue("test2_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test2_10", board.getMinion_p1(1).getHealth() == health1);
		assertTrue("test2", board.getMinion_p0(0).hasAttacked());
		
		theAttacker = board.getMinion_p0(0);
		theAttacker.hasAttacked(false);
		theAttacker.hasBeenUsed(false);
		ret = theAttacker.useOn(0, 1, 1, board);
		assertFalse("test2_0", ret == null);
		assertTrue("test2_1", board.getNumCards_hand() == 0);
		assertTrue("test2_2", board.getNumMinions_p0() == 2);
		assertTrue("test2_3", board.getNumMinions_p1() == 1);
		assertTrue("test2_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test2_5", board.getHero_p1().getHealth() == 27);
		assertTrue("test2_7", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test2_8", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test2_9", board.getMinion_p1(0).getHealth() == health1);
		
	}

	@Test
	public void test3() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 0, 2, board);
		
		assertFalse("test3_0", ret == null);
		assertTrue("test3_1", board.getNumCards_hand() == 0);
		assertTrue("test3_2", board.getNumMinions_p0() == 3);
		assertTrue("test3_3", board.getNumMinions_p1() == 2);
		assertTrue("test3_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test3_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test3_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test3_7", board.getMinion_p0(1).getHealth() == fb.getHealth());
		assertTrue("test3_8", board.getMinion_p0(2).getHealth() == health1);
		assertTrue("test3_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test3_10", board.getMinion_p1(1).getHealth() == health1);
	}

	@Test
	public void test4() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 0, 3, board);
		
		assertFalse("test4_0", ret == null);
		assertTrue("test4_1", board.getNumCards_hand() == 0);
		assertTrue("test4_2", board.getNumMinions_p0() == 3);
		assertTrue("test4_3", board.getNumMinions_p1() == 2);
		assertTrue("test4_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test4_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test4_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test4_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test4_8", board.getMinion_p0(2).getHealth() == fb.getHealth());
		assertTrue("test4_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test4_10", board.getMinion_p1(1).getHealth() == health1);
	}

	@Test
	public void test5() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 1, 1, board);
		
		assertTrue("test5_0", ret == null);
		assertTrue("test5_1", board.getNumCards_hand() == 1);
		assertTrue("test5_2", board.getNumMinions_p0() == 2);
		assertTrue("test5_3", board.getNumMinions_p1() == 2);
		assertTrue("test5_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test5_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test5_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test5_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test5_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test5_10", board.getMinion_p1(1).getHealth() == health1);
	}

	@Test
	public void test6() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 1, 2, board);
		
		assertTrue("test6_0", ret == null);
		assertTrue("test6_1", board.getNumCards_hand() == 1);
		assertTrue("test6_2", board.getNumMinions_p0() == 2);
		assertTrue("test6_3", board.getNumMinions_p1() == 2);
		assertTrue("test6_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test6_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test6_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test6_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test6_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test6_10", board.getMinion_p1(1).getHealth() == health1);
	}

	@Test
	public void test7() {
		BloodfenRaptor fb = new BloodfenRaptor();
		board.placeCard_hand(fb);
		
		Card theCard = board.getCard_hand(0);
		BoardState ret = theCard.useOn(0, 1, 3, board);
		
		assertTrue("test7_0", ret == null);
		assertTrue("test7_1", board.getNumCards_hand() == 1);
		assertTrue("test7_2", board.getNumMinions_p0() == 2);
		assertTrue("test7_3", board.getNumMinions_p1() == 2);
		assertTrue("test7_4", board.getHero_p0().getHealth() == 30);
		assertTrue("test7_5", board.getHero_p1().getHealth() == 30);
		assertTrue("test7_6", board.getMinion_p0(0).getHealth() == health0);
		assertTrue("test7_7", board.getMinion_p0(1).getHealth() == health1);
		assertTrue("test7_9", board.getMinion_p1(0).getHealth() == health0);
		assertTrue("test7_10", board.getMinion_p1(1).getHealth() == health1);
	}
}