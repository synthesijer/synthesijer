import synthesijer.lib.*;

public class Test008{

	private byte byte_value = 100;
	private char char_value = 100;
	private short short_value = 100;
	private int int_value = 100;
	private long long_value = 100;
	private double double_value = 100;
	private float float_value = 100;

    public void run(){

		int_value = byte_value;
		byte_value = (byte)int_value;
		int_value = char_value;
		char_value = (char)int_value;
		int_value = short_value;
		short_value = (short)int_value;
		long_value = int_value;
		int_value = (int)long_value;

		float_value = byte_value;
		float_value = short_value;
		float_value = int_value;
		float_value = long_value;

		byte_value = (byte)float_value;
		short_value = (short)float_value;
		int_value = (int)float_value;
		long_value = (long)float_value;

		double_value = byte_value;
		double_value = short_value;
		double_value = int_value;
		double_value = long_value;

		byte_value = (byte)double_value;
		short_value = (short)double_value;
		int_value = (int)double_value;
		long_value = (long)double_value;

		float_value = (float)double_value;
		double_value = float_value;

		int_value = int_value;
		int_value = int_value + int_value;
		int_value = byte_value + int_value;
		int_value = int_value + byte_value;
    }

	public boolean test(){
		if(byte_value != 100) return false;
		if(char_value != 'd') return false;
		if(short_value != 100) return false;
		if(int_value != 400) return false;
		if(long_value != 100) return false;
		if(double_value != 100.0d) return false;
		if(float_value != 100.0f) return false;
		return true;
	}

	@synthesijer.rt.unsynthesizable
	public static void main(String... args){
		Test008 t = new Test008();
		t.run();
		System.out.println(t.byte_value);
		System.out.println(t.char_value);
		System.out.println(t.short_value);
		System.out.println(t.int_value);
		System.out.println(t.long_value);
		System.out.println(t.double_value);
		System.out.println(t.float_value);
		System.out.println(t.test());
	}
    
}
