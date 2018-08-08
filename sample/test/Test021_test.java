/** test for loop pipelining **/
import synthesijer.rt.*;

public class Test021_test {

	private final int[] a = new int[16];
	private final int[] b = new int[16];
	private final int[] c = new int[16];

	private final Test021 obj = new Test021();
	
	public boolean test(){
		for(int i = 0; i < a.length; i++){
			a[i] = i;
		}
		for(int i = 0; i < b.length; i++){
			b[i] = 2*i;
		}
		obj.add(a, b, c, a.length);
		for(int i = 0; i < c.length; i++){
			if(c[i] != 3*i) return false;
		}
		return true;
	}

	@unsynthesizable
	public static void main(String... args){
		Test021_test t = new Test021_test();
		System.out.println(t.test());
	}

}
