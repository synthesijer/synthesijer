public class Test015{
    private Test015_A obj = new Test015_A();
    private byte[] src = new byte[256];
    private int[] dest = new int[64];
    private byte[] src1 = new byte[128];
    private int[] dest1 = new int[32];

    private void init(){
	for(int i = 0; i < src.length; i++){
	    src[i] = (byte)i;
	}
	for(int i = 0; i < src1.length; i++){
	    src1[i] = (byte)(i<<1);
	}
    }

    public int v;
    public int v1;
    private void check(){
	for(int i = 0; i < dest.length; i++){
	    v = dest[i];
	}
	for(int i = 0; i < dest1.length; i++){
	    v1 = dest1[i];
	}	
    }

    public void test(){
	init();
	obj.pack(src, dest);
	obj.pack(src1, dest1);
	check();
    }

}
