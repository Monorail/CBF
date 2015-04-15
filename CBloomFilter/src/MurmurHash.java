import java.util.Scanner;

public class MurmurHash {
	
	public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		
		String one=sc.nextLine();
		String two=sc.nextLine();
		for(int i=0;i<7;i++){
			System.out.println("hi "+i+": "+hashItUp(one,i));
			System.out.println("bye "+i+": "+hashItUp(two,i));
		}
	}
	
	public static int hashItUp(String key, int i) {

		int hone= key.hashCode();
		int htwo = MurmurHash2(key, 100020233);
		

		return Math.abs(hone + i * htwo);
	}
	
	   public static int MurmurHash2(final String text, int seed) {
	        final byte[] bytes = text.getBytes(); 
	        return MurmurHash2(bytes, bytes.length, seed);
	    }
	
    public static int MurmurHash2(final byte[] data, int length, int seed) {
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        final int m = 0x5bd1e995;
        final int r = 24;

        // Initialize the hash to a random value
        int h = seed^length;
        int length4 = length/4;

        for (int i=0; i<length4; i++) {
            final int i4 = i*4;
            int k = (data[i4+0]&0xff) +((data[i4+1]&0xff)<<8)
                    +((data[i4+2]&0xff)<<16) +((data[i4+3]&0xff)<<24);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }
        
        // Handle the last few bytes of the input array
        switch (length%4) {
        case 3: h ^= (data[(length&~3) +2]&0xff) << 16;
        case 2: h ^= (data[(length&~3) +1]&0xff) << 8;
        case 1: h ^= (data[length&~3]&0xff);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

}
