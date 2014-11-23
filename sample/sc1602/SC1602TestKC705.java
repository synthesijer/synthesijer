import java.io.IOException;

import java.util.EnumSet;

import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;

public class SC1602TestKC705 {

	public static void main(String... args) throws IOException{
		HDLModule top = new HDLModule("SC1602TestKC705");
		HDLPort sys_clk_p = top.newPort("SYS_CLK_P", HDLPort.DIR.IN, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort sys_clk_n = top.newPort("SYS_CLK_N", HDLPort.DIR.IN, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort user_reset = top.newPort("USER_RESET", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		
		HDLPort top_lcd_rs = top.newPort("pLCD_RS", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort top_lcd_e  = top.newPort("pLCD_E",  HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort top_lcd_db = top.newPort("pLCD_DB", HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(4), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort top_lcd_rw = top.newPort("pLCD_RW", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));

		HDLModule target = new HDLModule("SC1602Test", "clk", "reset");
		HDLPort lcd_rs = target.newPort("class_obj_0000_pLCD_RS_exp", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort lcd_e  = target.newPort("class_obj_0000_pLCD_E_exp",  HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort lcd_db = target.newPort("class_obj_0000_pLCD_DB_exp", HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(4), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort lcd_rw = target.newPort("class_obj_0000_pLCD_RW_exp", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort test_req  = target.newPort("test_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort test_busy = target.newPort("test_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		
		HDLInstance inst = top.newModuleInstance(target, "U");
		
		HDLModule ibufds = new HDLModule("IBUFDS");
		HDLPort ibufds_out = ibufds.newPort("O",  HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort ibufds_i = ibufds.newPort("I",  HDLPort.DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLPort ibufds_ib = ibufds.newPort("IB", HDLPort.DIR.IN,  HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.NO_SIG));
		HDLInstance u_ibufds = top.newModuleInstance(ibufds, "U_IBUFDS");
		
		u_ibufds.addPortPair(sys_clk_p, ibufds_i);
		u_ibufds.addPortPair(sys_clk_n, ibufds_ib);

		inst.getSignalForPort("clk").setAssign(null, u_ibufds.getSignalForPort("O"));
		inst.getSignalForPort("reset").setAssign(null, user_reset.getSignal());

		inst.getSignalForPort(test_req.getName()).setAssign(null, HDLPreDefinedConstant.HIGH); // always high to start immediately
		inst.addPortPair(top_lcd_rs, lcd_rs);		
		inst.addPortPair(top_lcd_e, lcd_e);		
		inst.addPortPair(top_lcd_db, lcd_db);		
		inst.addPortPair(top_lcd_rw, lcd_rw);		
		
		HDLUtils.generate(top, HDLUtils.VHDL);
		HDLUtils.generate(top, HDLUtils.Verilog);
	}
}
