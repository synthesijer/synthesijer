/** This has not been worked well, yet */
public class Test015_A{

	public void pack(byte[] s, int[] d){
		for(int i = 0; i < d.length; i++){
			int ptr = i << 2;
			d[i] = 0;
			d[i] += (s[i + 0] << 24);
			d[i] += (s[i + 1] << 16);
			d[i] += (s[i + 2] <<  8);
			d[i] += (s[i + 3] <<  0);
		}
	}
}
