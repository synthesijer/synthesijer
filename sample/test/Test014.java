public class Test014{

	private int test_ar0(int a){
		return a >> 0;
	}

	private int test_ar1(int a){
		return a >> 1;
	}

	private int test_ar8(int a){
		return a >> 8;
	}

	private int test_ar16(int a){
		return a >> 16;
	}

	private int test_ar24(int a){
		return a >> 24;
	}

	private int test_ar31(int a){
		return a >> 31;
	}

	private int test_ar32(int a){
		return a >> 32;
	}

	private int test_al0(int a){
		return a << 0;
	}

	private int test_al1(int a){
		return a << 1;
	}

	private int test_al8(int a){
		return a << 8;
	}

	private int test_al16(int a){
		return a << 16;
	}

	private int test_al24(int a){
		return a << 24;
	}

	private int test_al31(int a){
		return a << 31;
	}

	private int test_al32(int a){
		return a << 32;
	}

	private int test_lr0(int a){
		return a >>> 0;
	}

	private int test_lr1(int a){
		return a >>> 1;
	}

	private int test_lr8(int a){
		return a >>> 8;
	}

	private int test_lr16(int a){
		return a >>> 16;
	}

	private int test_lr24(int a){
		return a >>> 24;
	}

	private int test_lr31(int a){
		return a >>> 31;
	}

	private int test_lr32(int a){
		return a >>> 32;
	}

	private boolean test1(){
		if(test_ar0(0x88888888) != 0x88888888) return false;
		if(test_ar1(0x88888888) != 0xC4444444) return false;
		if(test_ar8(0x88888888) != 0xFF888888) return false;
		if(test_ar16(0x88888888) != 0xFFFF8888) return false;
		if(test_ar24(0x88888888) != 0xFFFFFF88) return false;
		if(test_ar31(0x88888888) != 0xFFFFFFFF) return false;
		if(test_ar32(0x88888888) != 0x88888888) return false;
		
		if(test_al0(0x88888888) != 0x88888888) return false;
		if(test_al1(0x88888888) != 0x11111110) return false;
		if(test_al8(0x88888888) != 0x88888800) return false;
		if(test_al16(0x88888888) != 0x88880000) return false;
		if(test_al24(0x88888888) != 0x88000000) return false;
		if(test_al31(0x88888888) != 0x00000000) return false;
		if(test_al32(0x88888888) != 0x88888888) return false;
		
		if(test_lr0(0x88888888) != 0x88888888) return false;
		if(test_lr1(0x88888888) != 0x44444444) return false;
		if(test_lr8(0x88888888) != 0x00888888) return false;
		if(test_lr16(0x88888888) != 0x00008888) return false;
		if(test_lr24(0x88888888) != 0x00000088) return false;
		if(test_lr31(0x88888888) != 0x00000001) return false;
		if(test_lr32(0x88888888) != 0x88888888) return false;
		return true;
	}

	private boolean test2(){
		if(test_ar0(0x48888888) != 0x48888888) return false;
		if(test_ar1(0x48888888) != 0x24444444) return false;
		if(test_ar8(0x48888888) != 0x00488888) return false;
		if(test_ar16(0x48888888) != 0x00004888) return false;
		if(test_ar24(0x48888888) != 0x00000048) return false;
		if(test_ar31(0x48888888) != 0x00000000) return false;
		if(test_ar32(0x48888888) != 0x48888888) return false;
		
		if(test_al0(0x48888888) != 0x48888888) return false;
		if(test_al1(0x48888888) != 0x91111110) return false;
		if(test_al8(0x48888888) != 0x88888800) return false;
		if(test_al16(0x48888888) != 0x88880000) return false;
		if(test_al24(0x48888888) != 0x88000000) return false;
		if(test_al31(0x48888888) != 0x00000000) return false;
		if(test_al32(0x48888888) != 0x48888888) return false;
		
		if(test_lr0(0x48888888) != 0x48888888) return false;
		if(test_lr1(0x48888888) != 0x24444444) return false;
		if(test_lr8(0x48888888) != 0x00488888) return false;
		if(test_lr16(0x48888888) != 0x00004888) return false;
		if(test_lr24(0x48888888) != 0x00000048) return false;
		if(test_lr31(0x48888888) != 0x00000000) return false;
		if(test_lr32(0x48888888) != 0x48888888) return false;

		return true;
	}
	
	public boolean test(){
		if(test1() == false) return false;
		if(test2() == false) return false;
		return true;
	}
	
	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test014 o = new Test014();
		System.out.println(o.test());
	}
	
}
