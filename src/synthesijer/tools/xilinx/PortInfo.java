package synthesijer.tools.xilinx;

public class PortInfo {

	public final String name;
	public final String dir;
	public final String type;
	public final boolean vector;
	public final long left;
	public final long right;

	public PortInfo(String n, String d, String t) {
		this(n, d, t, false, 0L, 0L);
	}

	public PortInfo(String n, String d, String t, boolean vector, long left,
			long right) {
		this.name = n;
		this.dir = d;
		this.type = t;
		this.vector = vector;
		this.left = left;
		this.right = right;
	}

}
