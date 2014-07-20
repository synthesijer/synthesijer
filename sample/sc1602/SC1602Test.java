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

		obj.req = true;
		obj.req = false;
	}

}
