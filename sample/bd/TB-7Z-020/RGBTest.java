import synthesijer.rt.unsynthesizable;


public class RGBTest {
	
	private final SinTableRom sin = new SinTableRom();
	private final TestFrame obj = new TestFrame();
	
	private int colortbl[] = new int[6];

	private void init_colortbl(){
		colortbl[0] = 0x00FF0000;
		colortbl[1] = 0x00FFFF00;
		colortbl[2] = 0x0000FF00;
		colortbl[3] = 0x0000FFFF;
		colortbl[4] = 0x000000FF;
		colortbl[5] = 0x00FF00FF;
	}
	

	private void paint_sincurve(int offset){
		int c_id = 0;
	    for(int i = 0; i < (1920 >> 2); i++){ // 1920 / 4
	    	int x = i << 2; // i * 4
	    	int y = sin.sintable[(i+offset)&0x0000007F]; // %128
	    	int c = colortbl[c_id];
	    	obj.fill_rect(x, y, 4, 4, c);
	    	c_id = c_id + 1;
	    	if(c_id == 6) c_id = 0;
	    }
	}
	
	private void sleep(int n){
		for(int i = 0; i < n; i++){ ; }
	}
	
	public void run(){
		
		init_colortbl();
		
		while(true){
			paint_sincurve(0);
			paint_sincurve(32);
			paint_sincurve(64);
			paint_sincurve(96);
			sleep(100);
			obj.flush();
		}
	}
	
	@unsynthesizable
	public static void main(String[] args){
		RGBTest t = new RGBTest();
		t.run();
	}
	    
}
