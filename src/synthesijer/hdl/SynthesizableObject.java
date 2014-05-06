package synthesijer.hdl;

import java.io.PrintWriter;

public interface SynthesizableObject {
	
	public void dumpAsVHDL(PrintWriter dest, int offset);

	public void dumpAsVerilogHDL(PrintWriter dest, int offset);

}
