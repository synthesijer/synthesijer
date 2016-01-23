import synthesijer.rt.*;

public class Test018{

	private int[] a = new int[10];

	public boolean test(int idx){
		for(int i = 0; i < a.length; i++){
			a[i] = i;
		}
		int i = idx;
		int j = idx+1;
		a[i] = a[j];
		if(a[i] != a[j]){
			return false;
		}
		a[8] = a[j];
		if(a[8] != a[j]){
			return false;
		}
		return true;
	}

	@unsynthesizable
	public static void main(String[] args){
		Test018 o = new Test018();
		System.out.println(o.test(3));
	}


	
}
