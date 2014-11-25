public class Test009{

	public void test(){
		float fa;
		float fb;
		float fc;
		int x;
		long y;

		fa = 100;
		fb = 3;
		fc = fa + fb;
		x = (int)fc;
		fc = fa - fb;
		x = (int)fc;
		fc = fa * fb;
		x = (int)fc;
		fc = fa / fb;
		x = (int)fc;

		double da;
		double db;
		double dc;

		da = 100;
		db = 3;
		dc = da + db;
		y = (long)dc;
		dc = da - db;
		y = (long)dc;
		dc = da * db;
		y = (long)dc;
		dc = da / db;
		y = (long)dc;

		dc = da + fb;
		y = (long)dc;
		fc = (float)(da + fb);
		x = (int)fc;
		
		if(fa > fb){ x = 1; }else{ x = 0; }
		if(fa < fb){ x = 0; }else{ x = 1; }
		if(fa == fb){ x = 0; }else{ x = 1; }
		if(fa != fb){ x = 1; }else{ x = 0; }
		if(fa >= fb){ x = 1; }else{ x = 0; }
		if(fa <= fb){ x = 0; }else{ x = 1; }

		if(da > db){ x = 1; }else{ x = 0; }
		if(da < db){ x = 0; }else{ x = 1; }
		if(da == db){ x = 0; }else{ x = 1; }
		if(da != db){ x = 1; }else{ x = 0; }
		if(da >= db){ x = 1; }else{ x = 0; }
		if(da <= db){ x = 0; }else{ x = 1; }

		if(da == fa){ x = 1; }else{ x = 0; }
	}
}
