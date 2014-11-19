public class Test009{

	public void test(){
		float fa;
		float fb;
		float fc;
		int x;
		long y;

		fa = 1;
		fb = 2;
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

		da = 1;
		db = 2;
		dc = da + db;
		y = (long)dc;
		dc = da - db;
		y = (long)dc;
		dc = da * db;
		y = (long)dc;
		dc = da / db;
		y = (long)dc;
	}
}
