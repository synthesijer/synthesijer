public class Test009{

	public void test(){
		float fa;
		float fb;
		float fc;
		int x;
		long y;

		float fd = 10.0f;
		float fe = 30.0F;
		fa = 10.0f;
		fa = 30.0F;
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

		double dd = 10.0d;
		double de = 30.0D;
		double df = 40.0f;
		double dg = 50.0F;
		da = 10.0d;
		da = 30.0D;
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
