import synthesijer.rt.*;

public class Test_ArithInt{

	@synthesijer.rt.Debug
	public int ic;
	
	@synthesijer.rt.Debug
	public long lc;

    private int add32(int ia, int ib){
		ic = ia + ib;
		return ic;
	}
	    
    private int sub32(int ia, int ib){
		ic = ia - ib;
		return ic;
	}

	private int mul32(int ia, int ib){
		ic = ia * ib;
		return ic;
	}

	private int div32(int ia, int ib){
		ic = ia / ib;
		return ic;
	}

	private int mod32(int ia, int ib){
		ic = ia % ib;
		return ic;
	}

	private long add64(long la, long lb){
		lc = la + lb;
		return lc;
	}
	    
    private long sub64(long la, long lb){
		lc = la - lb;
		return lc;
	}

	private long mul64(long la, long lb){
		lc = la * lb;
		return lc;
	}

	private long div64(long la, long lb){
		lc = la / lb;
		return lc;
	}

	private long mod64(long la, long lb){
		lc = la % lb;
		return lc;
	}

	private boolean test32(){
		int ia, ib;
		ia = 300; ib = 200;
		if(add32(ia, ib) != 500)   return false;
		if(sub32(ia, ib) != 100)   return false;
		if(mul32(ia, ib) != 60000) return false;
		if(div32(ia, ib) != 1)     return false;
		if(mod32(ia, ib) != 100)   return false;

		ia = 300; ib = -200;
		if(add32(ia, ib) != 100)    return false;
		if(sub32(ia, ib) != 500)    return false;
		if(mul32(ia, ib) != -60000) return false;
		if(div32(ia, ib) != -1)     return false;
		if(mod32(ia, ib) != 100)    return false;
		
		ia = -300; ib = 200;
		if(add32(ia, ib) != -100)   return false;
		if(sub32(ia, ib) != -500)   return false;
		if(mul32(ia, ib) != -60000) return false;
		if(div32(ia, ib) != -1)     return false;
		if(mod32(ia, ib) != -100)   return false;
		
		ia = -300; ib = -200;
		if(add32(ia, ib) != -500)   return false;
		if(sub32(ia, ib) != -100)   return false;
		if(mul32(ia, ib) != 60000)  return false;
		if(div32(ia, ib) != 1)      return false;
		if(mod32(ia, ib) != -100)   return false;
		
		return true;
    }

	private boolean test64(){
		long la, lb;
		la = 5000000000L; lb = 2000000000L;
		if(add64(la, lb) != 7000000000L) return false;
		if(sub64(la, lb) != 3000000000L) return false;
		//if(mul64(la, lb) != 10000000000000000000L) return false;
		if(mul64(la, lb) != -8446744073709551616L) return false;
		if(div64(la, lb) != 2) return false;
		if(mod64(la, lb) != 1000000000L) return false;
		
		la = 5000000000L; lb = -2000000000L;
		if(add64(la, lb) != 3000000000L) return false;
		if(sub64(la, lb) != 7000000000L) return false;
		//if(mul64(la, lb) != -10000000000000000000L) return false;
		if(mul64(la, lb) != 8446744073709551616L) return false;
		if(div64(la, lb) != -2) return false;
		if(mod64(la, lb) != 1000000000L) return false;
		
		la = -5000000000L; lb = 2000000000L;
		if(add64(la, lb) != -3000000000L) return false;
		if(sub64(la, lb) != -7000000000L) return false;
		//if(mul64(la, lb) != -10000000000000000000L) return false;
		if(mul64(la, lb) != 8446744073709551616L) return false;
		if(div64(la, lb) != -2) return false;
		if(mod64(la, lb) != -1000000000L) return false;
		
		la = -5000000000L; lb = -2000000000L;
		if(add64(la, lb) != -7000000000L) return false;
		if(sub64(la, lb) != -3000000000L) return false;
		//if(mul64(la, lb) != 10000000000000000000L) return false;
		if(mul64(la, lb) != -8446744073709551616L) return false;
		if(div64(la, lb) != 2) return false;
		if(mod64(la, lb) != -1000000000L) return false;
		
		return true;
    }

	public boolean test(){
		if(test32() == false) return false;
		if(test64() == false) return false;
		return true;
    }

	@unsynthesizable
	public static void main(String... args){
		Test_ArithInt obj = new Test_ArithInt();
		System.out.println(obj.test());
	}
	
}
