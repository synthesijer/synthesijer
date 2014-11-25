package synthesijer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.ast.Module;
import synthesijer.hdl.HDLModule;
import synthesijer.lib.ARITH_RSHIFT32;
import synthesijer.lib.ARITH_RSHIFT64;
import synthesijer.lib.BlockRAM;
import synthesijer.lib.DIV32;
import synthesijer.lib.DIV64;
import synthesijer.lib.FADD32;
import synthesijer.lib.FADD64;
import synthesijer.lib.FCOMP32;
import synthesijer.lib.FCOMP64;
import synthesijer.lib.FCONV_D2F;
import synthesijer.lib.FCONV_D2L;
import synthesijer.lib.FCONV_F2D;
import synthesijer.lib.FCONV_F2I;
import synthesijer.lib.FCONV_I2F;
import synthesijer.lib.FCONV_L2D;
import synthesijer.lib.FDIV32;
import synthesijer.lib.FDIV64;
import synthesijer.lib.FMUL32;
import synthesijer.lib.FMUL64;
import synthesijer.lib.FSUB32;
import synthesijer.lib.FSUB64;
import synthesijer.lib.INPUT1;
import synthesijer.lib.INPUT16;
import synthesijer.lib.INPUT32;
import synthesijer.lib.LOGIC_RSHIFT32;
import synthesijer.lib.LOGIC_RSHIFT64;
import synthesijer.lib.LSHIFT32;
import synthesijer.lib.LSHIFT64;
import synthesijer.lib.MUL32;
import synthesijer.lib.MUL64;
import synthesijer.lib.OUTPUT1;
import synthesijer.lib.OUTPUT16;
import synthesijer.lib.OUTPUT32;
import synthesijer.scheduler.GenSchedulerBoardVisitor;
import synthesijer.scheduler.GlobalSymbolTable;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerInfoCompiler;

public enum Manager {
	
	INSTANCE;
	
	/**
	 * A table of modules to treat this synthesis session
	 */
	private Hashtable<String, SynthesijerModuleInfo> modules = new Hashtable<>();
		
	private Manager(){
		addHDLModule("BlockRAM1",  null, new BlockRAM(1, 10, 1024), false);
		addHDLModule("BlockRAM8",  null, new BlockRAM(8, 10, 1024), false);
		addHDLModule("BlockRAM16", null, new BlockRAM(16, 10, 1024), false);
		addHDLModule("BlockRAM32", null, new BlockRAM(32, 10, 1024), false);
		addHDLModule("BlockRAM64", null, new BlockRAM(64, 10, 1024), false);
		addHDLModule("synthesijer.lib.INPUT1", null, new INPUT1(), false);
		addHDLModule("synthesijer.lib.INPUT16", null, new INPUT16(), false);
		addHDLModule("synthesijer.lib.INPUT32", null, new INPUT32(), false);
		addHDLModule("synthesijer.lib.OUTPUT1", null, new OUTPUT1(), false);
		addHDLModule("synthesijer.lib.OUTPUT16", null, new OUTPUT16(), false);
		addHDLModule("synthesijer.lib.OUTPUT32", null, new OUTPUT32(), false);
		addHDLModule("INPUT1", null, new INPUT1(), false);
		addHDLModule("INPUT16", null, new INPUT16(), false);
		addHDLModule("INPUT32", null, new INPUT32(), false);
		addHDLModule("OUTPUT1", null, new OUTPUT1(), false);
		addHDLModule("OUTPUT16", null, new OUTPUT16(), false);
		addHDLModule("OUTPUT32", null, new OUTPUT32(), false);
		// MUL/DIV
		addHDLModule("MUL32", null, new MUL32(), false);
		addHDLModule("MUL64", null, new MUL64(), false);
		addHDLModule("DIV32", null, new DIV32(), false);
		addHDLModule("DIV64", null, new DIV64(), false);
		// floating 32-bit
		addHDLModule("FADD32", null, new FADD32(), false);
		addHDLModule("FSUB32", null, new FSUB32(), false);
		addHDLModule("FMUL32", null, new FMUL32(), false);
		addHDLModule("FDIV32", null, new FDIV32(), false);
		// floating 64-bit
		addHDLModule("FADD64", null, new FADD64(), false);
		addHDLModule("FSUB64", null, new FSUB64(), false);
		addHDLModule("FMUL64", null, new FMUL64(), false);
		addHDLModule("FDIV64", null, new FDIV64(), false);
		// conversion
		addHDLModule("FCONV_F2I", null, new FCONV_F2I(), false);
		addHDLModule("FCONV_D2L", null, new FCONV_D2L(), false);
		addHDLModule("FCONV_I2F", null, new FCONV_I2F(), false);
		addHDLModule("FCONV_L2D", null, new FCONV_L2D(), false);
		addHDLModule("FCONV_F2D", null, new FCONV_F2D(), false);
		addHDLModule("FCONV_D2F", null, new FCONV_D2F(), false);
		// SHIFT
		addHDLModule("LSHIFT32", null, new LSHIFT32(), false);
		addHDLModule("LSHIFT64", null, new LSHIFT64(), false);
		addHDLModule("LOGIC_RSHIFT32", null, new LOGIC_RSHIFT32(), false);
		addHDLModule("LOGIC_RSHIFT64", null, new LOGIC_RSHIFT64(), false);
		addHDLModule("ARITH_RSHIFT32", null, new ARITH_RSHIFT32(), false);
		addHDLModule("ARITH_RSHIFT64", null, new ARITH_RSHIFT64(), false);
		// COMP
		addHDLModule("FCOMP32", null, new FCOMP32(), false);
		addHDLModule("FCOMP64", null, new FCOMP64(), false);
	}
	
	public void addModule(Module m, boolean synthesisFlag){
		if(hasModule(m.getName())) return;
		addHDLModule(m.getName(), m, null, synthesisFlag);
	}

	private void addHDLModule(String name, Module m, HDLModule hm, boolean synthesisFlag){
		modules.put(name, new SynthesijerModuleInfo(m, hm, synthesisFlag));
	}

	public Module searchModule(String name){
		SynthesijerModuleInfo info = modules.get(name);
		return info.m;
	}

	public SynthesijerModuleInfo searchHDLModuleInfo(String name){
		SynthesijerModuleInfo info = modules.get(name);
		return info;
	}
	
	public void HDLModuleInfoList(){
		Enumeration<String> keys = modules.keys();
		while(keys.hasMoreElements()){
			System.out.println(keys.nextElement());
		}
	}

	public boolean isGeneratedHDLModule(String name){
		SynthesijerModuleInfo info = modules.get(name);
		return !info.sysnthesisFlag || !info.state.isBefore(CompileState.GENERATE_HDL);
	}

	public boolean hasModule(String key){
		return modules.contains(key);
	}
	
	private void genGlobalSymbolTable(){
		Enumeration<String> keys = modules.keys();
		while(keys.hasMoreElements()){
			String k = keys.nextElement();
			SynthesijerModuleInfo info = modules.get(k);
			if(info.m != null){
				GlobalSymbolTable.INSTANCE.add(info.m);
			}else{
				GlobalSymbolTable.INSTANCE.add(k, info.hm);
			}
		}
	}
	
	private void dumpSchedulerInfo(SchedulerInfo si, String postfix){
		try(PrintStream txt = new PrintStream(new FileOutputStream(new File(si.getName() + "_scheduler_board" + postfix + ".txt")));
				PrintStream dot = new PrintStream(new FileOutputStream(new File(si.getName() + "_scheduler_board" + postfix + ".dot")))){
				dot.println("digraph {");
				for(SchedulerBoard b: si.getBoardsList()){
					b.dump(txt);
					b.dumpDot(dot);
				}
				dot.println("}");
				txt.close();
				dot.close();
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	
	private void doGenSchedulerBoard(){
		for(SynthesijerModuleInfo info : modules.values()){
			if(info.sysnthesisFlag == false){
				// skip
				continue;
			}
			String name = info.m.getName();
			System.out.println("SchdulerBoard init: " + name);
			IdentifierGenerator i = new IdentifierGenerator();
			SchedulerInfo si = new SchedulerInfo(name);
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(si, i);
			info.m.accept(v);
			info.setSchedulerInfo(si);
			dumpSchedulerInfo(si, "init");
		}
	}
	
	public void preprocess(){
		genGlobalSymbolTable();
		doGenSchedulerBoard();
	}
	
	private HDLModule loadUserHDLModule(String s){
		try {
			Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(s);
			
			Constructor<?> ct = clazz.getConstructor(new Class[]{String[].class});
			Object obj = ct.newInstance(new Object[]{new String[]{}});
			if(!(obj instanceof HDLModule)){
				System.err.printf("unsupported type: %s (%s)", obj, obj.getClass());
				System.exit(0);
			}
			return (HDLModule)obj;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private void compileSchedulerInfoAll(){
		for(SynthesijerModuleInfo info: modules.values()){
			compileSchedulerInfo(info);
		}
	}
	
	public void compileSchedulerInfo(String name){
		compileSchedulerInfo(modules.get(name));
	}
	
	public void compileSchedulerInfo(SynthesijerModuleInfo info){
		if(info.getCompileState() == CompileState.GENERATE_HDL){ // nothing to do
			return;
		}
		if(info.getCompileState() == CompileState.WAIT_FOR_LOADING){
			info.state = CompileState.GENERATE_HDL;
			info.setHDLModule(loadUserHDLModule(info.m.getName())); 
		}else{
			info.state = CompileState.GENERATE_HDL;
			HDLModule hm = new HDLModule(info.m.getName(), "clk", "reset");
			info.setHDLModule(hm); 
			SchedulerInfoCompiler compiler = new SchedulerInfoCompiler(info.getSchedulerInfo(), hm);			
			compiler.compile();
		}
	}
	
	public void generate(boolean optimizeFlag){
		compileSchedulerInfoAll();
	}

	enum OutputFormat { Verilog, VHDL; };

	public void output(OutputFormat format){
		for(SynthesijerModuleInfo info: modules.values()){
			if(info.sysnthesisFlag == false) continue;
			HDLModule hm = info.getHDLModule();
			if(format == OutputFormat.VHDL){
				String destFileName = String.format("%s.vhd", hm.getName());
				System.out.printf("Output VHDL: %s\n", destFileName);
				try(PrintWriter dest = new PrintWriter(new FileOutputStream(destFileName), true)){ 
					hm.genVHDL(dest);
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				String destFileName = String.format("%s.v", hm.getName());
				System.out.printf("Output Verilog HDL: %s\n", destFileName);
				try(PrintWriter dest = new PrintWriter(new FileOutputStream(destFileName), true)){ 
					hm.genVerilogHDL(dest);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}

	public void dumpAsXML(PrintWriter dest){
		DumpAsXMLVisitor visitor = new DumpAsXMLVisitor(dest);
		dest.printf("<?xml version=\"1.0\" ?>\n");
		dest.printf("<modules>\n");
		for(SynthesijerModuleInfo info: modules.values()){
			if(info.m != null) info.m.accept(visitor);
		}
		dest.printf("</modules>\n");
	}
	
	public class SynthesijerModuleInfo{
		/**
		 * A given module
		 */
		public final Module m;
		
		/**
		 *
		 */
		private SchedulerInfo info;
		
		/**
		 * A generated HDL module form the corresponding given module
		 */
		private HDLModule hm;
		
		/**
		 * Flat whether this module should be synthesized or not
		 */
		public final boolean sysnthesisFlag;
		
		private CompileState state;
		
		public SynthesijerModuleInfo(Module m, HDLModule hm, boolean synthesisFlag) {
			this.m = m;
			this.hm = hm;
			this.sysnthesisFlag = synthesisFlag;
			if(synthesisFlag == false){
				if(m == null){
					this.state = CompileState.GENERATE_HDL;
				}else{
					this.state = CompileState.WAIT_FOR_LOADING;
				}
			}else{
				this.state = CompileState.INITIALIZE;
			}
		}
		
		/**
		 * whether a given source code exists or not
		 * @return
		 */
		public boolean hasSource(){
			return m != null;
		}
		
		public void setHDLModule(HDLModule hm){
			this.hm = hm;
		}
		
		public HDLModule getHDLModule(){
			return this.hm;
		}
		
		public void setSchedulerInfo(SchedulerInfo info){
			this.info = info;
		}
		
		public SchedulerInfo getSchedulerInfo(){
			return info;
		}

		public void setCompileState(CompileState s){
			state = s;
		}
		
		public CompileState getCompileState(){
			return state;
		}
	}	

}
