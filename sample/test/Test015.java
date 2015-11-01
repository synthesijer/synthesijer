public class Test015{
    private Test015_A obj = new Test015_A();
    private byte[] src = new byte[256];
    private int[] dest = new int[64];

    private void init(){
	for(int i = 0; i < src.length; i++){
	    src[i] = (byte)i;
	}
    }

    public int v;
    private void check(){
	for(int i = 0; i < dest.length; i++){
	    v = dest[i];
	}
    }

    public void test(){
	init();
	obj.pack(src, dest);
	check();
    }

}
