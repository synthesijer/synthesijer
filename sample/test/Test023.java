public class Test023{
	
	private static final int CONST1 = 1;
	private static final int CONST2 = (1 << CONST1);
	private static final int CONST3 = -CONST2;
	
	public boolean test(int c1, int c2, int c3){
		if(CONST1 != c1) return false;	
		if(CONST2 != c2) return false;	
		if(CONST3 != c3) return false;	
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test023 o = new Test023();
		System.out.println(o.test(1, 2, -2));
	}
	
}
