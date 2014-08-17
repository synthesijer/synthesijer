
public class Test003 {
	
	private final Test002 t = new Test002();
	
	public void test(){
		t.init();
		t.set(0, 100); // 0 <- 100
		t.a[1] = 50; // 1 <- 50
		t.set(3, t.get(0) + t.get(1)); // 3 <- 150
		t.set(4, Test002.DEFAULT_VALUE); // 4 <- 2
		t.set(t.get(0), t.get(1)); // 100 <- 50
		t.get(t.get(0)); // 50
		t.get(3); // 150
		t.get(1); // 50
		t.get(0); // 100
		int a = t.a[3]; // 150
		a = t.a[1]; // 50
		a = t.a[0]; // 100
		for(int i = 0; i < 5; i++){
			int x = t.switch_test(i);
		}
		t.x = 100;
		t.y = 200;
		int v = t.sum_x_y();
	}

}
