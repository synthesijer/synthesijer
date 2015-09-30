import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;

public class SC1602Wrapper extends HDLModule {

	public boolean req;
	public boolean busy;
	public byte[] data;

	public SC1602Wrapper(String... args) {
		super("sc1602_wrapper", "clk", "reset");

		newParameter("CLKWAIT", 16);

		newPort("pLCD_RS", DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("pLCD_E",  DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("pLCD_DB", DIR.OUT, HDLPrimitiveType.genVectorType(4), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("pLCD_RW", DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));

		newPort("req", DIR.IN, HDLPrimitiveType.genBitType());
		newPort("busy", DIR.OUT, HDLPrimitiveType.genBitType());

		newPort("data_we", DIR.IN, HDLPrimitiveType.genBitType());
		newPort("data_address", DIR.IN, HDLPrimitiveType.genVectorType(32));
		newPort("data_din", DIR.IN, HDLPrimitiveType.genVectorType(8));

	}

}
