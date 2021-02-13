package monitor;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SBproblemOneBarber {
	
	
	static SleepingBaber1 SB = new SleepingBaber1();
	// save number of customer
	public static int numCust = 0;

	public static void main(String[] args) throws InterruptedException {
		
		// randomly select the number of customers between 1 to 20
		Random rand = new Random();
		int NumOfCustomer = rand.nextInt(20)+1;
		numCust = NumOfCustomer;
		System.out.print("NumOfCustomer : " + NumOfCustomer + "\n");

		// start barber Thread and customers Thread
		new Barber1(SB).start();
		for (int i = 0; i < NumOfCustomer; i++) {
			new Customer1(SB).start();
			Thread.sleep(50);
		}
	}
}

class SleepingBaber1 {

	// Number of barber which available for serve to customer
	int barber = 0;
	
	// define 2 condition barber_available and chair_occupied 
	private final Lock lock = new ReentrantLock();
	Condition barber_available = lock.newCondition();
	Condition chair_occupied = lock.newCondition();

	
	// barbers cut the hair of customer
	void get_haircut() throws InterruptedException {
		lock.lock();

		while (barber == 0)
			barber_available.await();
		barber = barber - 1;
		chair_occupied.signal();
		
		lock.unlock();

	}
	
	// barbers get next customer to haircut
	void get_next_customer() throws InterruptedException {
		lock.lock();

		barber = barber + 1;
		barber_available.signal();
		chair_occupied.await();
		
		lock.unlock();
	}
}

class Customer1 extends Thread {
	SleepingBaber1 SB;

	Customer1(SleepingBaber1 sb) {
		this.SB = sb;
	}

	public void run() {
		try {
			let_your_hair_grow();
			System.out.print("Customer " + currentThread().getId() + " arrive\n");
			SB.get_haircut();
			System.out.print("Customer " + currentThread().getId() + " went\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void let_your_hair_grow() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Barber1 extends Thread {
	SleepingBaber1 SB;
	
	Barber1(SleepingBaber1 sb) {
		this.SB = sb;
	}

	public void run() {
		while (true) {
			try {
				if(SBproblemOneBarber.numCust == 0 )
					break ;
				System.out.print("Barber "+ " waited for Customer\n");
				SB.get_next_customer();
				haircut();
				System.out.print("Barber "  + " finished Customer\n");
				SBproblemOneBarber.numCust--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void haircut() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}