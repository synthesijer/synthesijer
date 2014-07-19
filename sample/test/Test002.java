
public class Test002 {
	
	int[] a = new int[1024];
	
	public void init(){
		for(int i = 0; i < a.length; i++){
			a[i] = 0;
		}
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
}
