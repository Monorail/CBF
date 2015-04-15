import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class BloomFilter<AnyType> implements Runnable {

	static int numThreads =2;
	static ArrayList<Thread> threads = new ArrayList<Thread>();
	static ArrayList<BloomFilter<String>> workers = new ArrayList<BloomFilter<String>>();
	static hashSet[] hashTables;
	static Hashtable testCompare;
	static int tableSize;
	static int numTables;
	static int numElems = 1000;
	static int expectedElems = 1000;
	static float errorRate = .01f;
	static AtomicBoolean[] announceTable;
	static boolean runDebug = false;
	static boolean percStats = true;
	static boolean usingOF = true;
	static AtomicInteger mayCon = new AtomicInteger(0);
	static AtomicInteger noCon = new AtomicInteger(0); 
	// set up wait free queue for adding elements
	static ConcurrentLinkedQueue<String> opQueue = new ConcurrentLinkedQueue<String>();
	
	
	
	public static double log2(double L) {
		return Math.log(L) / Math.log(2);
	}

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter( new FileWriter("BF_Results.txt",true)));
		
		
		if(args.length < 2){
			System.out.println("Incorrect args");
			System.exit(0);
		}
		
		Scanner sc = new Scanner(new File("magicrules.txt"));

		// sample strings
		ArrayList<String> corpus = new ArrayList<String>();
		while (sc.hasNext()) {
			corpus.add(sc.next());
		}
		//sc = new Scanner(System.in);
		
		//writer.print(corpus.size() + " ");
		writer.print(Integer.parseInt(args[0])+" ");
		expectedElems = Integer.parseInt(args[0]);//sc.nextInt();
		writer.print(Integer.parseInt(args[1])+ " ");
		numElems = Integer.parseInt(args[1]); //sc.nextInt();
		
		
		testCompare= new Hashtable(expectedElems,0.5f);
		numTables = (int) Math.ceil(log2(1.0f / errorRate));
		//10 proven to be best bit/element ratio
		tableSize = (int) Math.ceil(expectedElems * Math.abs(Math.log(.01f)) / ((Math.log(2) * Math.log(2)) * numTables));
		
		
		//writer.println("numTables: " + numTables);
		writer.print(tableSize+ " ");
		// sc.next();
		// initialize hash table and capacity array
		hashTables = new hashSet[numTables];
		announceTable = new AtomicBoolean[numTables];
		for (int i = 0; i < numTables; i++) {
			hashTables[i] = new hashSet(tableSize, i, 0);
			announceTable[i] = new AtomicBoolean(false);
		}
		Collections.shuffle(corpus);
		
		String[] filterStrs = corpus.toArray(new String[corpus.size()]);// 
		
		// generate add operations at random to perform
		Random R = new Random();
		int addedAdds = 0, contAdds = 0;
		// for (int i = 0; i < numElems; i++) {
		
		
		while (addedAdds != numElems || contAdds != numElems) {
			Boolean opAdd = R.nextBoolean();
			String operation="";
			int strsIndex = R.nextInt(filterStrs.length);
			if (opAdd && addedAdds < numElems) {
				addedAdds++;
				opQueue.add("add " + filterStrs[strsIndex]);
			} else if (contAdds < numElems) {
				contAdds++;
				opQueue.add("con " + filterStrs[strsIndex]);
			}
			
			// System.out.println(operation);
		}
		
//		for(int i=0; i< numElems;i++){
//			int strsIndex = R.nextInt(filterStrs.length);
//			opQueue.add("add "+filterStrs[strsIndex]);
//		}
//		
//		for(int i= 0; i<numElems;i++){
//			int strsIndex = R.nextInt(filterStrs.length);
//			opQueue.add("con "+filterStrs[strsIndex]);
//		}
		
		
		

		// initialize threads
		for (int i = 0; i < numThreads; i++) {
			BloomFilter<String> temp = new BloomFilter<String>(i);
			workers.add(temp);
			threads.add(new Thread(temp));
		}
		long startTime = System.currentTimeMillis();
		// start all threads
		for (int i = 0; i < numThreads; i++) {
			threads.get(i).start();
		}
		for (int i = 0; i < numThreads; i++)
			threads.get(i).join();
		if (percStats)
			writer.printf("%d %d", noCon.get(), mayCon.get());
			
		//print();
		//writer.println("Time taken: " + (System.currentTimeMillis() - startTime) + "ms");
		writer.println();
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int ID;

	public BloomFilter(int ID) {
		this.ID = ID;
	}

	@Override
	public void run() {
		String operation = "";
		// poll wait-free queue for an operation until queue is empty
		while ((operation = opQueue.poll()) != null) {
			// System.out.println(ID + " " + operation);

			// parse the operation
			//System.out.println("Thread "+ ID + ": " +operation);
			String[] ops = operation.split(" ");
			// call contains method
			if (ops[0].equals("con")) {
				if (contains((AnyType) ops[1]) && runDebug) {
					//System.out.println("The bloom filter may contain the string \"" + ops[1] + "\"");
				} else if (runDebug) {
					//System.out.println("The bloom filter does not contain the string \"" + ops[1]+ "\"");
				}
			}

			// call add method
			else {
				testCompare.put(MurmurHash.MurmurHash2(ops[1], 100020233), ops[1]);
				add((AnyType) ops[1]);
				// System.out.println("Added " + ops[1]);
			}
		}
	}

	// adds object to the filter
	public void add(AnyType o) {
		// mark bits that correspond to hash value in each hash set as true
		// int
		for (int i = 0; i < hashTables.length; i++) {
			
			hashTables[i].add((String)o);
			// if(i ==4)
			// System.out.println(hVal);
		}
		//print();
	}

	// checks for object inside the filter
	// all true -> return true
	// any false -> return false
	public boolean contains(AnyType o) {
		// generate hash code for the object
		// check bits that correspond to hash value in each hash set
		// if every bit is marked, the filter MAY contain the object
		
		if (!testCompare.contains((String)o)&& percStats ){
			noCon.getAndIncrement();
		}
		for (int i = 0; i < hashTables.length; i++) {
			//int hval = Math.abs(MurmurHash.hashItUp((String)o, i) );
			if (!hashTables[i].contains((String)o)) {
				if (percStats) {
					mayCon.getAndIncrement();
				}
//				print();
				return false;
			}
		}
		
		//print();
		return true;
	}

	static void print(){

		for(hashSet h : hashTables){
			for (boolean b : h.set){
				System.out.print(b?1:0);
			}
			System.out.println();
		}
		System.out.println();
	}
	static class hashSet {
		hashSet next = null;
		int tableSize, ID;
		boolean full = false;
		int link;
		boolean[] set;

		public hashSet(int size, int ID, int link) {
			this.link = link;
			this.tableSize = size;
			this.ID = ID;
			set = new boolean[tableSize];
			for (int i = 0; i < tableSize; i++) {
				// //System.out.println("set");
				set[i] = false;
			}
			//System.out.println(ID + " created hashLink " + link);

		}

		public boolean contains(String o) {
			int hVal=MurmurHash.hashItUp(o,ID);
			if (set[hVal % tableSize]) {
				// if(runDebug)
				// System.out.println(ID +"[" +index%tableSize
				// +"] return true");
				return true;
			}
			if(!usingOF)
				return false;
			if (next == null) {
				// if(runDebug)
				// System.out.println(ID +"[" +index%tableSize
				// +"] return false");
				return false;
			}
			return next.contains(o);
		}

		public void add(String o) {
			// if the hash set is not full
			//System.out.println("derp");
			
			int hVal = Math.abs(MurmurHash.hashItUp((String)o, ID) );
			
			
			if(set[hVal%tableSize]){
				return;
			}
			
			if (!full) {
				// set the index value to true
				// count fullness
				
				set[hVal % tableSize] = true;
				
				int capacity = 0;
				for (boolean b : set)
					if (b)
						capacity++;
				capacity++;
				// System.out.println("full: " + (((float) capacity.get()) /
				// tableSize));
				// if(full)
				// System.out.println("full set true");
				full = (((float) capacity) / tableSize) > (.5f);
				//System.out.println(full);
				if(!usingOF){
					full = false;
					System.out.println("full false");
				}
				
				// if the hash set is full
				return;
			}
			if(!usingOF){
				System.out.println("bad");
				return;
			}
			//System.out.println("free");
			if (next != null) {
				// System.out.println(ID + " adding to next");
				next.add(o);
				return;
			}
			// and another thread is not creating the overflow table
			if (announceTable[ID%numTables].compareAndSet(false, true)) {

				// System.out.println(ID + " lock acquired");
				// and the overflow table doesn't exist
				if (next == null) {
					// System.out.println(ID + " creating overflow table");
					// create the overflow table
					// System.out.println("Creating overflow table for hashset #" + ID);
					next = new hashSet(tableSize, ID+numTables, link + 1);
					next.add(o);
					announceTable[ID%numTables].set(false);
					if (announceTable[ID%numTables].get()) {
						System.out.println(ID + " critical CAS money failure");
					}

					// and the overflow table does exist
				} else {
					next.add(o);
					announceTable[ID%numTables].set(false);
					// if (next == null)
					// System.out.println("1 goat ladder");
				}
			} else {
				// System.out.println(ID + " another thread is handling it");
				while (announceTable[ID%numTables].get());
				// if (next == null)
				// System.out.println("2 goat ladder");
				next.add(o);
			}

		}

	}
}