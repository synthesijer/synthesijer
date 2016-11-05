public class Test023{
	
	private static final int CONST1 = 1;
	private static final int CONST2 = -CONST1;

	public boolean test(){
		if(CONST1 != 1) return false;	
		if(CONST2 != -1) return false;	
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test023 o = new Test023();
		System.out.println(o.test());
	}
	
}
