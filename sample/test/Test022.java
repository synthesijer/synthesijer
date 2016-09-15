import synthesijer.rt.*;

public class Test022 {

	public void shift(int[] a, int x){
		for(int i = 1; i < a.length; i++){
			a[i-1] = a[i];
		}
		a[a.length] = x;
	}

	@unsynthesizable
	public static void main(String... args){
		int[] a = new int[]{1, 2, 3};
		Test022 t = new Test022();
		t.shift(a, 4);
		System.out.println(a[0]);
		System.out.println(a[1]);
		System.out.println(a[2]);
	}

}
