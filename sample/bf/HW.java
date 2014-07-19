public class HW {

	BF b = new BF();

	public void main() {

		b.startup();

		while (true) {
			b.init();
			b.read();
			b.print();
			boolean flag = true;
			while (b.step())
				;
		}
	}

}
