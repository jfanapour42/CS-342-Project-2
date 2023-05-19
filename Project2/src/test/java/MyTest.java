import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	static BetCard betCard;
	static HashSet<Integer> hashSet;
	static Player player;
	
	@BeforeEach
	void betCardSetUp1() {
		betCard = new BetCard();
		betCard.setSpots(10);
		hashSet = new HashSet<>();
		player = new Player();
		player.getBetCard().setSpots(10);
		for(int i = 0; i < 10; i++) {
			player.getBetCard().add(i);
			betCard.add(i);
			hashSet.add(i);
		}
	}
	
	void betCardSetUp2() {
		betCard = new BetCard();
		betCard.setSpots(10);
		hashSet = new HashSet<>();
		int num = 0; 
		for(int i = 0; i < 10; i++) {
			num = (int) (Math.random()*(80)) + 1;
			betCard.add(num);
			hashSet.add(num);
		}
	}
	
	//
	// BetCard Test Cases
	//
	@Test
	void betCardConstructorSpotsTest() {
		BetCard bc = new BetCard();
		assertEquals(0, bc.getSpots(), "Bet Card Constructor Spots test failed");
	}
	
	@Test
	void betCardConstructorDrawTest() {
		BetCard bc = new BetCard();
		assertEquals(1, bc.getDrawNumber(), "Bet Card Constructor draw test failed");
	}
	
	@Test
	void betCardConstructorNumbersTest() {
		BetCard bc = new BetCard();
		HashSet<Integer> s = new HashSet<>();
		assertEquals(s, bc.getNumbers(), "Bet Card Constructor Numbers test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1,4,8,10})
	void betCardSetSpotsInRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setSpots(val);
		assertEquals(val, bc.getSpots(), "Bet Card In range Spots test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {11,12,100})
	void betCardSetSpotsAboveRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setSpots(val);
		assertEquals(0, bc.getSpots(), "Bet Card Above range Spots test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {-1,-2,-100})
	void betCardSetSpotsBelowRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setSpots(val);
		assertEquals(0, bc.getSpots(), "Bet Card Below range Spots test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1,2,3,4})
	void betCardSetDrawInRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setDrawNumber(val);
		assertEquals(val, bc.getDrawNumber(), "Bet Card In range draw number test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {5,6,100})
	void betCardSetDrawAboveRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setDrawNumber(val);
		assertEquals(1, bc.getDrawNumber(), "Bet Card Above range draw number test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,-1,100})
	void betCardSetDrawBelowRangeTest(int val) {
		BetCard bc = new BetCard();
		bc.setDrawNumber(val);
		assertEquals(1, bc.getDrawNumber(), "Bet Card Below range draw number test failed");
	}
	
	@Test
	void betCardCompareTest() {
		assertEquals(hashSet, betCard.getNumbers(), "Bet Card compare to hash set test failed");
	}
	
	@Test 
	void betCardCompareTest2() {
		betCard.add(11);
		hashSet.add(11);
		assertNotEquals(hashSet, betCard.getNumbers(), "Bet Card compare to hash set test 2 failed");
	}
	
	@Test
	void betCardNumbersCountTest() {
		assertEquals(hashSet.size(), betCard.getNumberCount(), "Bet Card numbers count test failed");
	}
	
	@Test
	void betCardNumbersCountTest2() {
		betCard.add(11);
		hashSet.add(11);
		assertNotEquals(hashSet.size(), betCard.getNumberCount(), "Bet Card numbers count test 2 failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,1,2,3,4,5,6,7,8,9})
	void betCardContainsTest(int val) {
		assertEquals(true, betCard.contains(val), "Bet Card contains test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {10,11,12,13,14,15,16,17,18,19})
	void betCardDoesNotContainsTest(int val) {
		assertEquals(false, betCard.contains(val), "Bet Card does not contains test failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19})
	void betCardRemoveAndCompareTest(int val) {
		betCard.remove(val);
		hashSet.remove(val);
		assertEquals(hashSet, betCard.getNumbers(), "Bet Card remove and compare test failed");
	}
	
	@Test
	void betCardClearNumbersTest() {
		betCard.clearNumbers();
		assertEquals(0, betCard.getNumberCount(), "Bet Card clear test failed");
	}
	
	@Test
	void betCardFullTest() {
		assertEquals(true, betCard.full(),"Bet Card full test failed");
	}
	
	@Test
	void betCardRemoveAndFullTest() {
		betCard.remove(0);
		assertEquals(false, betCard.full(), "Bet Card remove and full test failed");
	}
	
	@Test 
	void betCardResetSpotsTest() {
		betCard.reset();
		assertEquals(0, betCard.getSpots(), "Bet Card reset spots test failed");
	}
	
	@Test 
	void betCardResetDrawTest() {
		betCard.reset();
		assertEquals(1, betCard.getDrawNumber(), "Bet Card reset draw test failed");
	}
	
	@Test 
	void betCardResetNumberCountTest() {
		betCard.reset();
		assertEquals(0, betCard.getNumberCount(), "Bet Card reset number count test failed");
	}
	
	//	
	//	Player Tests
	//
	@Test
	void playerGetEarnings() {
		Player p = new Player();
		assertEquals(0, p.getEarnings(), "Player get earnings failed");
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1,10,100})
	void playerSetEarnings(int val) {
		Player p = new Player();
		p.setEarnings(val);
		assertEquals(val, p.getEarnings(), "Player set earnings failed");
	}
	
	@ParameterizedTest
	@CsvSource({"1,5", "10,6", "100,7"})
	void playerAddEarnings(int val1, int val2) {
		Player p = new Player();
		p.addEarnings(val1);
		p.addEarnings(val2);
		assertEquals(val1 + val2, p.getEarnings(), "Player add earnings failed");
	}
	
	@Test
	void playerGetBetCardTest() {
		assertEquals(betCard.getNumbers(), player.getBetCard().getNumbers(), "Player get bet card test failed");
	}
}
