import synthesijer.rt.*;

public class Test022_test {

	private int[] a = new int[16];
	private final Test022 obj = new Test022();

	public boolean test(){
		for(int i = 0; i < a.length; i++){
			a[i] = i;
		}
		obj.shift(a, a.length);
		for(int i = 0; i < a.length; i++){
			if(a[i] != i+1) return false;
		}
		return true;
	}

	@unsynthesizable
	public static void main(String... args){
		Test022_test t = new Test022_test();
		System.out.println(t.test());
	}

}
