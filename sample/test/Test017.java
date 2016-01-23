
import synthesijer.rt.*;

public class Test017{

	private int[] a = new int[10];

	public int test0, test1, test2, test3;

	public boolean test(int idx){
		int ptr = idx;
		for(int i = 0; i < a.length; i++){
			a[i] = i;
		}
		test0 = a[ptr];
		test1 = a[ptr++];
		test2 = a[++ptr];
		test3 = a[ptr];
		boolean flag;
		if(test0 == a[idx] && test1 == a[idx] && test2 == a[idx+2] && test3 == a[idx+2]){
			return true;
		}else{
			return false;
		}
	}

	@unsynthesizable
	public static void main(String[] args){
		Test017 o = new Test017();
		System.out.println(o.test(3));
		System.out.println(o.test0);
		System.out.println(o.test1);
		System.out.println(o.test2);
		System.out.println(o.test3);
	}


	
}
