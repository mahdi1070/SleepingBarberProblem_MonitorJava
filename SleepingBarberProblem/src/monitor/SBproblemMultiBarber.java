package monitor;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SBproblemMultiBarber {
	
	static SleepingBaber SB = new SleepingBaber();
	// save number of customer
	public static int numCust = 0;

	public static void main(String[] args) throws InterruptedException {
		// randomly select the number of customers between 1 to 20
		Random rand = new Random();
		int NumOfCustomer = rand.nextInt(20)+1;
		numCust = NumOfCustomer;
		System.out.print("NumOfCustomer : "+NumOfCustomer + "\n");

		// randomly select the number of barbers between 1 to 5
		int NumOfBarber = rand.nextInt(5)+1;
		System.out.print("NumOfBarber : "+NumOfBarber + "\n");
		
		// start barbers Thread and customers Thread
		for (int i = 0; i < NumOfBarber; i++) {
			new Barber(SB).start();
		}
		for (int i = 0; i < NumOfCustomer; i++) {
			new Customer(SB).start();
			Thread.sleep(50);
		}
	}
}

class SleepingBaber {

	int barber = 0;
	private final Lock lock = new ReentrantLock();

	Condition barber_available = lock.newCondition();
	Condition chair_occupied = lock.newCondition();

	void get_haircut() throws InterruptedException {
		lock.lock();

		while (barber == 0)
			barber_available.await();
		barber = barber - 1;
		chair_occupied.signal();
		
		lock.unlock();

	}

	void get_next_customer() throws InterruptedException {
		lock.lock();

		barber = barber + 1;
		barber_available.signal();
		chair_occupied.await();
		
		lock.unlock();
	}
}

class Customer extends Thread {
	SleepingBaber SB;

	Customer(SleepingBaber sb) {
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

class Barber extends Thread {
	SleepingBaber SB;
	
	Barber(SleepingBaber sb) {
		this.SB = sb;
	}

	public void run() {
		while (true) {
			try {
				if(AssignCustomerToBarber() < 1)
					break ;
				System.out.print("Barber "+ currentThread().getId()  +" waited for Customer\n");
				SB.get_next_customer();
				haircut();
				System.out.print("Barber "  + currentThread().getId()  + " finished Customer\n");
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	synchronized int AssignCustomerToBarber()
	{
		return SBproblemMultiBarber.numCust-- ;
	}

	void haircut() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}