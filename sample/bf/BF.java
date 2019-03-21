public class BF {

	private final int ARRAYSIZE = 10000;
	private final int CODESIZE = 10000;

	private IO io = new IO();
	private byte[] prog = new byte[CODESIZE];
	private byte[] data = new byte[ARRAYSIZE];
	private int ptr, pc;

	public void startup() {
		io.putchar((byte) '\r');
		io.putchar((byte) '\n');
		io.putchar((byte) 'B');
		io.putchar((byte) 'r');
		io.putchar((byte) 'a');
		io.putchar((byte) 'i');
		io.putchar((byte) 'n');
		io.putchar((byte) 'f');
		io.putchar((byte) '*');
		io.putchar((byte) '*');
		io.putchar((byte) 'k');
		io.putchar((byte) '\r');
		io.putchar((byte) '\n');
		return;
	}

	private void prompt() {
		io.putchar((byte) '>');
		io.putchar((byte) ' ');
		return;
	}

	public void read() {
		prompt();
		for (int i = 0; i < CODESIZE; i++) {
			byte b;
			b = io.getchar();
			//io.putchar(b);
			if (b == '\n' || b == '\r') {
				prog[i] = (byte) 0;
				break;
			} else {
				prog[i] = b;
			}
		}
	}

	public void print() {
		boolean flag = true;
		for (int i = 0; i < CODESIZE; i++) {
			byte b;
			b = prog[i];
			if (b == 0) {
				break;
			}
			io.putchar(b);
		}
		io.putchar((byte) '\r');
		io.putchar((byte) '\n');
	}

	private void put_hex(byte b) {
		byte h;
		h = (byte) ((b >> 4) & 0x0F);
		if (0 <= h && h <= 9) {
			io.putchar((byte) (h + '0'));
		} else {
			io.putchar((byte) ((h - 10) + 'A'));
		}
		byte l;
		l = (byte) ((b >> 0) & 0x0F);
		if (0 <= h && l <= 9) {
			io.putchar((byte) (l + '0'));
		} else {
			io.putchar((byte) ((l - 10) + 'A'));
		}
		io.putchar((byte) '\r');
		io.putchar((byte) '\n');
	}

	public void init() {
		ptr = 0;
		pc = 0;
		for (int i = 0; i < ARRAYSIZE; i++) {
			data[i] = 0;
		}
		return;
	}

	public boolean step() {
//System.out.printf("[0]=%02x\n", data[0]);
//System.out.printf("[1]=%02x\n", data[1]);
//System.out.printf("[2]=%02x\n", data[2]);
//System.out.printf("pc=%02x\n", pc);
//System.out.printf("ptr=%02x\n", ptr);
		byte cmd;
		cmd = prog[pc];
		byte tmp;
		int nlvl = 0;
		switch (cmd) {
		case 0:
			return false;
		case '>':
			ptr++;
			break;
		case '<':
			ptr--;
			break;
		case '+':
			data[ptr] = (byte) (data[ptr] + 1);
			break;
		case '-':
			data[ptr] = (byte) (data[ptr] - 1);
			break;
		case '.':
			io.putchar(data[ptr]);
			break;
		case ',':
			data[ptr] = io.getchar();
			break;
		case '[':
			if (data[ptr] == (byte) 0) {
				while (true) {
					pc++;
					if(prog[pc] == ']' && nlvl == 0) break;
					if(prog[pc] == '[') nlvl++;
					if(prog[pc] == ']') nlvl--;
				}
			}
			break;
		case ']':
			while (true) {
				pc--;
				if(prog[pc] == '[' && nlvl == 0) break;
				if(prog[pc] == ']') nlvl++;
				if(prog[pc] == '[') nlvl--;
			}
			pc--;
			break;
		default:
			break;
		}
		pc++;
		return true;
	}

}
