public class IO {

	private rs232c obj = new rs232c();

	public void putchar(byte c) {
		obj.write(c);
	}

	public byte getchar() {
		byte b = obj.read();
		return b;
	}

}