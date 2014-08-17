
/*
 * cf. http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/CODES/mt19937ar.c
 */
public class MT{
	private static final int N = 624;
	private static final int M = 397;
	private static final long MATRIX_A = 0x000000009908b0dfL;
	private static final long UPPER_MASK = 0x0000000080000000L;
	private static final long LOWER_MASK = 0x000000007fffffffL;

	private final long[] mt = new long[N]; /* the array for the state vector  */
	private int mti = N+1; /* mti==N+1 means mt[N] is not initialized */

	/* initializes mt[N] with a seed */
	private void init_genrand(long s){
		mt[0]= s & 0x00000000ffffffffL;
		for (mti=1; mti<N; mti++) {
			mt[mti] = (1812433253L * (mt[mti-1] ^ (mt[mti-1] >> 30)) + mti); 
			/* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
			/* In the previous versions, MSBs of the seed affect   */
			/* only MSBs of the array mt[].                        */
			/* 2002/01/09 modified by Makoto Matsumoto             */
			mt[mti] = mt[mti] & 0x00000000ffffffffL;
			/* for >32 bit machines */
		}
	}

	private final long[] mag01 = new long[2];
	/* generates a random number on [0,0xffffffff]-interval */
	public long genrand_int32(){
		long y;
		/* mag01[x] = x * MATRIX_A  for x=0,1 */
		mag01[0] = 0x0L;
		mag01[1] = MATRIX_A;
		
		if (mti >= N) { /* generate N words at one time */
			int kk;
			
			if (mti == N+1)   /* if init_genrand() has not been called, */
				init_genrand(5489L); /* a default initial seed is used */
			
			for (kk=0;kk<N-M;kk++) {
				y = (mt[kk]&UPPER_MASK)|(mt[kk+1]&LOWER_MASK);
				mt[kk] = mt[kk+M] ^ (y >> 1) ^ mag01[(int)(y & 0x1)];
			}
			for (;kk<N-1;kk++) {
				y = (mt[kk]&UPPER_MASK)|(mt[kk+1]&LOWER_MASK);
				mt[kk] = mt[kk+(M-N)] ^ (y >> 1) ^ mag01[(int)(y & 0x1)];
			}
			y = (mt[N-1]&UPPER_MASK)|(mt[0]&LOWER_MASK);
			mt[N-1] = mt[M-1] ^ (y >> 1) ^ mag01[(int)(y & 0x1)];
			
			mti = 0;
		}
		
		y = mt[mti++];
		
		/* Tempering */
		y = y ^ ((y >> 11));
		y = y ^ ((y << 7) & 0x000000009d2c5680L);
		y = y ^ ((y << 15) & 0x00000000efc60000L);
		y = y ^ ((y >> 18));
		
		return y;
	}

	/* generates a random number on [0,0x7fffffff]-interval */
	public long genrand_int31(){
		return (long)(genrand_int32()>>1);
	}
	
}