
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

}
