
public class Test002 {
	
	public static final int DEFAULT_VALUE = 0x20;

	public int[] a = new int[128];
	public int x, y;
	
	public void init(){
		for(int i = 0; i < a.length; i++){
			a[i] = 0;
		}
	}

	public void dec(int i){
		a[i]--;
	}

	public void inc(int i){
		a[i]++;
	}

	public void set(int i, int v){
		a[i] = v;
	}

	public int get(int i){
		return a[i];
	}

	public int switch_test(int x){
		switch(x){
		case 0: return a[0];
		case 1: return a[1];
		case 2: return a[2];
		default: return a[3];
		}
	}
	
	public int sum_x_y(){
		int v = x + y;
		x = 0;
		y = 0;
		return v;
	}
}
