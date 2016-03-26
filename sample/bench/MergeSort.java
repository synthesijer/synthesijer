public class MergeSort {

	private static final int LENGTH = 512; 
	private final int[] ar = new int[LENGTH];
	private final int[] tmp = new int[LENGTH];

	public synchronized void init() {
		for (int i = 0; i < ar.length; i++) {
			ar[i] = ar.length - 1 - i;
		}
	}

	public synchronized void set(int id, int data) {
		ar[id] = data;
	}

	public synchronized int get(int id) {
		return ar[id];
	}

	public boolean check() {
		int v = ar[0];
		for (int i = 1; i < ar.length; i++) {
			if(v > ar[i]) return false;
			v = ar[i];
		}
		return true;
	}

	private int min(int a, int b){
		if(a < b){
			return a;
		}else{
			return b;
		}
	}

	public synchronized void test() {
		int div, seg, fb, fe, sb, se, i, j, n, p;

		for (div = 1; div <= LENGTH; div = div << 1) {
			for (seg = 0; seg < LENGTH; seg = seg + (div << 1)) {
				n = seg;
				fb = n;
				fe = min(fb + div, LENGTH);
				sb = fe;
				se = min(sb + div, LENGTH);

				i = fb;
				j = sb;
				while (i < fe && j < se) {
					int a0 = ar[i];
					int a1 = ar[j];
					if (a0 <= a1) {
						tmp[n] = a0;
						i++;
					} else {
						tmp[n] = a1;
						j++;
					}
					n++;
				}
				while (i < fe) {
					tmp[n] = ar[i];
					n++;
					i++;
				}
				while (j < se) {
					tmp[n] = ar[j];
					n++;
					j++;
				}
				for (p = seg; p < n; ++p) {
					ar[p] = tmp[p];
				}
			}
		}
	}
}
