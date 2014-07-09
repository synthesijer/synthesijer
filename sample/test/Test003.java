
public class Test003 {
	
	private final Test002 t = new Test002();
	
	public void test(){
		t.init();
		t.set(0, 100);
		t.set(1, 50);
		t.set(3, t.get(0) + t.get(1));
		t.set(t.get(0), t.get(1));
		t.get(t.get(0));
		t.get(3);
		t.get(1);
		t.get(0);
	}

}
