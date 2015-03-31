import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BloomFilter<AnyType> implements Runnable {

	static int numThreads = 4;
	static ArrayList<Thread> threads = new ArrayList<Thread>();
	static ArrayList<BloomFilter<String>> workers = new ArrayList<BloomFilter<String>>();
	static hashSet[] hashTables;
	static int tableSize = 53;
	static int numTables = 5;
	static ConcurrentLinkedQueue<String> opQueue = new ConcurrentLinkedQueue<String>();

	public static void main(String[] args) {
		hashTables = new hashSet[numTables];
		for(int i = 0; i < numTables; i++){
			hashTables[i] = new hashSet(tableSize, 9);
		}
		// initialize threads
		for (int i = 0; i < numThreads; i++) {
			BloomFilter<String> temp = new BloomFilter<String>(i);
			workers.add(temp);
			threads.add(new Thread(temp));
		}

		// start all threads
		for (int i = 0; i < numThreads; i++) {
			threads.get(i).start();
		}
	}
	
	
	int ID;

	public BloomFilter(int ID) {
		this.ID = ID;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	static class hashSet{
		hashSet next;
		int tableSize, seed;
		AtomicInteger capacity;
		boolean full = false;
		public hashSet(int size, int seed){
			this.tableSize = size;
			this.seed = seed;
			capacity = new AtomicInteger(0);
		}
	}
	
}

/*
	@Override
	public void run() {
		String operation = "";
		//poll wait-free queue for an operation until queue is empty
		while ((operation = opQueue.poll()) != null) {
			// System.out.println(ID + " " + operation);
			
			//parse the operation
			String[] ops = operation.split(" ");
			//call contains method
			if (ops[0].equals("con")) {
				System.out.printf("The bloom filter %s the name %s\n",
						contains((AnyType) ops[1]) ? "may contain"
								: "does not contain", ops[1]);
			} 
			//call add method
			else {
				add((AnyType) ops[1]);
				System.out.println("Added " + ops[1]);
			}
		}
	}

	//adds object to the filter
	public void add(AnyType o) {
		//generate hash code for the object
		int hval = o.hashCode();
		
		//check for indexOutOfBounds error
		if (hval < 0)
			hval = hval*-1;
		
		//mark bits that correspond to hash value in each hash set as true
		for (int i = 0; i < hashTables.length; i++){
			if(hashTables[i][hval % hashTables[i].length])tableCapacity[i].incrementAndGet();
			hashTables[i][hval % hashTables[i].length] = true;
		}
	}

	//checks for object inside the filter
	public boolean contains(AnyType o) {
		//generate hash code for the object
		int hval = o.hashCode();

		//check for indexOutOfBoundsError
		if (hval < 0)
			hval = hval *-1;

		//check bits that correspond to hash value in each hash set
		//if every bit is marked, the filter MAY contain the object
		for (int i = 0; i < hashTables.length; i++)
			if (hashTables[i][hval % hashTables[i].length] == false)
				return false;

		return true;
	}

*/






























