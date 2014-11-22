public class Test000{

    int ic;
    long lc;
    int x;
    long y;
    public void test(int ia, int ib, long la, long lb){
	    
	ic = ia + ib;
	ic = ia - ib;
	ic = ia * ib;
	ic = ia / ib;
	ic = ia % ib;
	ic = -ia;
	ic = ia >> 2;
	ic = (-ia) >>> 2;
	ic = (-ia) >> 2;
	ic = ia << 2;
	ic = ia << ib;
	ic = ia >> ib;
	ic = (-ia) >>> ib;
	ic = (-ia) >> ib;

	lc = la + lb;
	lc = la - lb;
	lc = la * lb;
	lc = la / lb;
	lc = la % lb;
	lc = -la;
	lc = la >> 2;
	lc = (-la) >>> 2;
	lc = (-la) >> 2;
	lc = la << 2;
	lc = la << lb;
	lc = la >> lb;
	lc = (-la) >>> lb;
	lc = (-la) >> lb;

	float fa, fb, fc;
	fa = ia;
	fb = ib;

	fc = fa + fb;
	x = (int)fc;
	fc = fa - fb;
	x = (int)fc;
	fc = fa * fb;
	x = (int)fc;
	fc = fa / fb;
	x = (int)fc;

	double da, db, dc;
	da = la;
	db = lb;

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
