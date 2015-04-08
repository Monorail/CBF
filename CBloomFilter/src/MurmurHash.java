public class MurmurHash {

	public static int hashItUp(Object key, int i) {

		int hone = MurmurHash2(key, 43);
		int htwo = MurmurHash2(key, 213);

		return hone + i * htwo;
	}

	public static int MurmurHash2(Object key, int seed) {
		// 'm' and 'r' are mixing constants generated offline.
		// They're not really 'magic', they just happen to work well.

		final int m = 0x5bd1e995;
		final int r = 24;

		// Initialize the hash to a 'random' value

		int h = seed;
		int k = key.hashCode();

		k *= m;
		k ^= k >> r;
		k *= m;

		h *= m;
		h ^= k;

		return h;
	}

}
