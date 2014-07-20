public class SC1602Test {

	SC1602Wrapper obj = new SC1602Wrapper();

	public void test() {
		obj.req = false;

		while (obj.busy == true){;}

		obj.data[0] = (byte) 'H';
		obj.data[1] = (byte) 'e';
		obj.data[2] = (byte) 'l';
		obj.data[3] = (byte) 'l';
		obj.data[4] = (byte) 'o';
		obj.data[5] = (byte) ',';
		obj.data[0x40] = (byte) 'S';
		obj.data[0x41] = (byte) 'y';
		obj.data[0x42] = (byte) 'n';
		obj.data[0x43] = (byte) 't';
		obj.data[0x44] = (byte) 'h';
		obj.data[0x45] = (byte) 'e';
		obj.data[0x46] = (byte) 's';
		obj.data[0x47] = (byte) 'i';
		obj.data[0x48] = (byte) 'j';
		obj.data[0x49] = (byte) 'e';
		obj.data[0x4a] = (byte) 'r';
		obj.data[0x4b] = (byte) '!';
		obj.data[0x4c] = (byte) '!';

		obj.req = true;
		obj.req = false;
		while(true) ;
	}

}
