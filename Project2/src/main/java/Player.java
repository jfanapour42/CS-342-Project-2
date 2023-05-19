public class Player {
	private int earnings;
	private BetCard bet;
	
	public Player() {
		earnings = 0;
		bet = new BetCard();
	}
	
	public int getEarnings() {
		return earnings;
	}
	
	public void setEarnings(int e) {
		earnings = e;
	}
	
	public void addEarnings(int e) {
		earnings += e;
	}
	
	public BetCard getBetCard() {
		return bet;
	}
}
