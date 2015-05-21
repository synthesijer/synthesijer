/** This has not been worked well, yet */
public class Test015{
	Test014_A obj = new Test014_A();
	private byte[] src = new byte[1024];
	private int[] dest = new int[256];

	public void test(){
		obj.pack(src, dest);
	}
}
