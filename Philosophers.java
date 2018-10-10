import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.*;

/**
 * Philosophers class replicates the dining philosophers problem
 * Program uses lock to allow each philosopher to eat and finish when the philosopher gets two forks
 * If they don't have two fork, they cannot begin eating
 * @author Anh-Thy Ho | CS149
 *
 */
public class Philosophers {

	public boolean finish;
	public boolean fork1;
	public boolean fork2;
	public boolean thinking;
	public boolean eating;
	public String name;
	private final Lock waiter = new ReentrantLock(true);

	/**
	 * Philosopher object that initializes var 
	 * Has no forks to start with and is thinking 
	 * @param name is name of philosopher
	 */
	public Philosophers(String name) {
		this.name = name;
		finish = false;
		fork1 = false;
		fork2 = false;
		thinking = true;
		eating = false;
		waiter.lock();
	}

	/**
	 * gets name of philosopher
	 * @return name of philosopher
	 */
	public String getName() {
		return name;
	}

	/**
	 * if philosopher has finished, finishes food 
	 */
	public void setFinish() {
		finish = true;
	}

	/**
	 * checks if philosopher can pick up fork
	 * @param s clarifies is fork can be picked up or down
	 */
	public void setfork1(String s) {
		if (waiter.tryLock()) {
			if (s.equals("down")) {
				fork1 = false;
			}
			if (s.equals("up")) {
				fork1 = true;
			}
		}
		if (!waiter.tryLock()) {
			System.out.println("waiter locked");
		}

	}

	/**
	 * checks if philosopher can pick up fork
	 * @param s clarifies is fork can be picked up or down
	 */
	public void setfork2(String s) {
		if (waiter.tryLock()) {

			if (s.equals("down")) {
				fork2 = false;
			}
			if (s.equals("up")) {
				fork2 = true;
			}
		}
		if (!waiter.tryLock()) {
			System.out.println("waiter locked");
		}
	}

	/**
	 * sets thinking state of philosopher
	 * @param s true or false depending of philosopher is thinking
	 */
	public void think(boolean s) {
		thinking = s;
	}

	/**
	 * unlocked the waiter if philosopher has both forks 
	 * allows philosopher to eat 
	 */
	public void eat() {
		if (fork1 && fork2) {
			waiter.unlock();
			System.out.println("waiter unlocked");
			thinking = false;
			finish = true;
			System.out.println("finished eating");
			setfork1("down");
			System.out.println("put down fork 1");
			setfork2("down");
			System.out.println("put down fork 2");
			eating = false;
			thinking = true;
			waiter.lock();
			System.out.println("waiter locked");
		}
	}

	public static void main(String[] args) {
		Philosophers[] philosophers = new Philosophers[5];
		for (int i = 0; i < philosophers.length; i++) {
			String pi = "p" + i;
			Philosophers p = new Philosophers(pi);
			philosophers[i] = p;

		}
		Random rand = new Random();

		boolean AllDone = false;

		while (!AllDone) {

			ArrayList<Boolean> all = new ArrayList<>();
			for (int i = 0; i < philosophers.length; i++) {
				if (philosophers[i].finish)
					all.add(philosophers[i].finish);
				//System.out.println("how many have finished: " +all.size());

			}
			if (all.size() >= 5) {
				AllDone = true;
				System.out.println("all done");
				return;
			}
			int r = rand.nextInt(philosophers.length);
			System.out.println("next: " +r);
			String pi = "p" + r;
			Philosophers p = philosophers[r];
			if (p.finish) {
				System.out.println(pi + " finished already");
			}
			if (!p.finish && r < 1) {
				p.waiter.unlock();
			}
			if (!p.finish && p.waiter.tryLock()) {
				System.out.println("Has not finished, eating now: " + pi);
				if (r < 1) {
					p.setfork1("up");
					p.setfork2("up");
					System.out.println("pick up forks");
				} else if (!philosophers[r - 1].fork1 && !philosophers[r - 1].fork2) {
					p.setfork1("up");
					p.setfork2("up");
					System.out.println("pick up forks");
				}
				p.eating = true;
				p.thinking = false;
				while (p.eating) {
					for (Philosophers t : philosophers) {
						System.out.println(t.getName() + " Eating: " + t.eating + " Thinking: " + t.thinking);
					}
					p.eat();
				}
			}

		}

	}

}
