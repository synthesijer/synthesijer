public class Sim {

	public static void main(String... args) {

		BF b = new BF();
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
