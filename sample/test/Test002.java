
public class Test002 {
	
	public static final int DEFAULT_VALUE = 0x20;

	public int[] a = new int[128];
	public int x, y;
	
	public void init(){
		for(int i = 0; i < a.length; i++){
			a[i] = 0;
		}
	}

	public int dec(int i){
		return a[i]--;
	}

	public int inc(int i){
		return a[i]++;
	}

	public void copy(int i, int j){
		a[j] = a[i];
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

	public boolean test(){
		int v;
		init();
		for(int i = 0; i < a.length; i++){
			set(i, i);
		}
		for(int i = 0; i < a.length; i++){
			if(get(i) != i) return false;
		}
		v = dec(10);
		if(v != 10) return false;
/*
		if(a[10] != 9) return false;
		v = inc(20);
		if(v != 20) return false;
		if(a[20] != 21) return false;
		copy(100, 110);
		if(a[100] != a[110]) return false;
		v = switch_test(0);
		if(v != 0) return false;
		v = switch_test(1);
		if(v != 1) return false;
		v = switch_test(2);
		if(v != 2) return false;
		v = switch_test(3);
		if(v != 3) return false;
		v = switch_test(4);
		if(v != 3) return false;
		x = 20;
		y = 30;
		v = sum_x_y();
		if(v != 50) return false;
*/
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test002 obj = new Test002();
		System.out.println(obj.test());
	}
	
}
