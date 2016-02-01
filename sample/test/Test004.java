public class Test004 extends Thread{
	public int i;
	public void run(){
		run_body();
	}

	private void run_body(){
		i = 0;
		for(int j = 0; j < 1000; j++){
			i++;
		}
	}

	public void break_test(){
		i = 0;
		while(true){
		  i++;
		  if(i == 10) break;
		}
	}

	// todo something wrong
	public int continue_test(){
		i = 0;
		int j = 0;
		for(j = 0; j < 20; j++){
		  if(i == 10) continue;
		  i++;
		}
		return j;
	}

	public boolean test(){
		break_test();
		if(i != 10) return false;
		int v = continue_test();
		if(i != 10) return false;
		if(v != 20) return false;
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test004 o = new Test004();
		System.out.println(o.test());
	}
	
}
