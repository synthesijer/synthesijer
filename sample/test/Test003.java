
public class Test003 {
	
	private final Test002 t = new Test002();
	
	public boolean test(){
		t.init();
		t.set(0, 100); // 0 <- 100
		t.a[1] = 50; // 1 <- 50
		t.set(3, t.get(0) + t.get(1)); // 3 <- 150
		t.set(4, Test002.DEFAULT_VALUE); // 4 <- 0x20
		t.set(t.get(0), t.get(1)); // 100 <- 50
		
		int v;
		v = t.a[0];
		if(v != 100) return false;
		v = t.a[1];
		if(v != 50) return false;
		v = t.a[3];
		if(v != 150) return false;
		v = t.a[4];
		if(v != 0x20) return false;
		v = t.a[100];
		if(v != 50) return false;
		for(int i = 0; i < 5; i++){
			v = t.switch_test(i);
		}
		if(v != 150) return false; // should be a[3] (= 150)
		
		t.x = 100;
		t.y = 200;
		v = t.sum_x_y();
		if(v != 300) return false;
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test003 obj = new Test003();
		System.out.println(obj.test());
	}
	
}
