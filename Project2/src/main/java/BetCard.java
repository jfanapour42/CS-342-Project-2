import java.util.HashSet;

public class BetCard {
	private int spots;
	private int draws;
	private HashSet<Integer> numbers;
	
	public BetCard() {
		spots = 0;
		draws = 1;
		numbers = new HashSet<>();
	}
	
	public int getSpots() {
		return spots;
	}
	
	public void setSpots(int s) {
		if (s <= 10 && s >= 0) {
			spots = s;
		}
	}
	
	public int getDrawNumber() {
		return draws;
	}
	
	public void setDrawNumber(int d) {
		if (d <= 4 && d > 0) {
			draws = d;
		}
	}
	
	public HashSet<Integer> getNumbers() {
		return numbers;
	}
	
	public boolean add(int n) {
		if(numbers.size() < spots) {
			return numbers.add(n);
		}
		return false;
	}
	
	public boolean contains(int n) {
		return numbers.contains(n);
	}
	
	public boolean remove(int n) {
		return numbers.remove(n);
	}
	
	public void clearNumbers() {
		numbers.clear();
	}
	
	public int getNumberCount() {
		return numbers.size();
	}
	
	public boolean full() {
		return spots == numbers.size();
	}
	
	public void reset() {
		spots = 0;
		draws = 1;
		numbers.clear();
	}
}
