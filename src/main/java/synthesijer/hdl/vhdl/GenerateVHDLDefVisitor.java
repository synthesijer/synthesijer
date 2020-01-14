package synthesijer.hdl.vhdl;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.ArrayList;

import synthesijer.Constant;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLInstanceRef;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLParameter;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.OPTION;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLValue;

public class GenerateVHDLDefVisitor implements HDLTreeVisitor{

	private PrintWriter dest;
	private int offset;
	
	private HashMap<HDLUserDefinedType, Boolean> definedType = new HashMap<>();
	private ArrayList<String> methodKind = new ArrayList<String>();

	public GenerateVHDLDefVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		/*
		if(o.getSubModule().isComponentDeclRequired() == false) return;
		HDLUtils.println(dest, offset, String.format("component %s", o.getSubModule().getName()));
		if(o.getSubModule().getParameters().length > 0){
			genGenericList(dest, offset+2, o.getSubModule().getParameters());
		}
		genPortList(dest, offset+2, o.getSubModule().getPorts(), (o.getSubModule().getParameters().length > 0));
		HDLUtils.println(dest, offset, String.format("end component %s;", o.getSubModule().getName()));
		*/
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub

	}

	private void genGenericList(PrintWriter dest, int offset, HDLParameter[] params){
		HDLUtils.println(dest, offset, "generic (");
		String sep = "";
		for(HDLParameter p: params){
			dest.print(sep);
			offset += 2;
			p.accept(this);
			offset -= 2;
			sep = ";" + Constant.BR;
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	private void genPortList(PrintWriter dest, int offset, HDLPort[] ports, boolean paramFlag, String mk){
		// ここでentity部のportを宣言
		HDLUtils.println(dest, offset, "port (");
		String sep = "";
		String pn;
		for(HDLPort p: ports){
			pn = p.getName();
			//p.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			if(pn.contains("_")){
				if(pn.contains(mk)){
					dest.print(sep);
					HDLUtils.print(dest, offset+2, String.format("%s : %s %s", p.getName(), p.getDir().getVHDL(), ((HDLPrimitiveType)p.getType()).getVHDL(paramFlag)));
					sep = ";" + Constant.BR;
				}
			}else{
				dest.print(sep);
				HDLUtils.print(dest, offset+2, String.format("%s : %s %s", p.getName(), p.getDir().getVHDL(), ((HDLPrimitiveType)p.getType()).getVHDL(paramFlag)));
				sep = ";" + Constant.BR;
			}
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	private void genParamList(PrintWriter dest, int offset, HDLParameter[] params, boolean paramFlag){
		HDLUtils.println(dest, offset, "generic (");
		String sep = "";
		for(HDLParameter p: params){
			dest.print(sep);
			//p.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			HDLUtils.print(dest, offset+2, String.format("%s : %s := %s",
					p.getName(),
					((HDLPrimitiveType)p.getType()).getVHDL(),
					p.getDefaultValue().getVHDL()));
			sep = ";" + Constant.BR;
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	public void setMethodKind(HDLPort[] ports){
		String pn;
		for(HDLPort p: ports){
			pn = p.getName();
			if(pn.contains("_")){
				String[] str = pn.split("_"); 
				if(!methodKind.contains(str[0])){
					methodKind.add(str[0]);
				}
			}
		}
	}

	@Override
	public void visitHDLModule(HDLModule o) {

		setMethodKind(o.getPorts());
		for (int i = 0 ; i < methodKind.size() ; i++){
			// library import
			HDLUtils.println(dest, offset, String.format("library IEEE;"));
			HDLUtils.println(dest, offset, String.format("use IEEE.std_logic_1164.all;"));
			//HDLUtils.println(dest, offset, String.format("use IEEE.numeric_std.all;"));
			HDLUtils.nl(dest);

			HDLModule.LibrariesInfo[] libraries = o.getLibraries();
			for(HDLModule.LibrariesInfo lib: libraries){
				HDLUtils.println(dest, offset, String.format("library " + lib.libName + ";"));
				for(String s: lib.useName){
					HDLUtils.println(dest, offset, String.format("use " + s + ";"));
				}
				HDLUtils.nl(dest);
			}

			String entityName = "";
			if(i == methodKind.size() - 1){
				entityName = o.getName();
			} else {
				entityName = methodKind.get(i);
			}
			HDLUtils.println(dest, offset, String.format("entity %s is", entityName));
			
			if(o.getParameters().length > 0){
				genParamList(dest, offset+2, o.getParameters(), false);
			}
			if(o.getPorts().length > 0){
				// entity部のport宣言出力の呼び出し元
				genPortList(dest, offset+2, o.getPorts(), false, methodKind.get(i));
			}
			HDLUtils.println(dest, offset, String.format("end %s;", entityName));
			HDLUtils.nl(dest);

			// architecture
			HDLUtils.println(dest, offset, String.format("architecture RTL of %s is", entityName));
			//HDLUtils.nl(dest);
			/*
			HDLUtils.println(dest, offset+2, String.format("attribute mark_debug : string;"));
			HDLUtils.println(dest, offset+2, String.format("attribute keep : string;"));
			HDLUtils.println(dest, offset+2, String.format("attribute S : string;"));
			HDLUtils.nl(dest);*/

			Hashtable<String, Boolean> componentFlags = new Hashtable<>();
			for(HDLInstance j: o.getModuleInstances()){
				if(componentFlags.containsKey(j.getSubModule().getName())) continue; // already
				offset += 2;
				j.accept(this);
				offset -= 2;
				//System.out.println(i.getSubModule().getName());
				componentFlags.put(j.getSubModule().getName(), true);
			}

			//HDLUtils.nl(dest);
			for(HDLPort p: o.getPorts()){
				if(p.isSet(OPTION.NO_SIG)) continue;
				if(p.getSignal() == null) continue;
				offset += 2;
				p.getSignal().accept(this);
				offset -= 2;
			}
			//HDLUtils.nl(dest);
			offset += 2;
			for(HDLSignal s: o.getSignals()){ s.accept(this); }
			offset -= 2;
			HDLUtils.println(dest, offset, String.format("end RTL;"));
			HDLUtils.nl(dest);
		}
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		System.out.println(o);
		HDLUtils.print(dest, offset, String.format("%s : %s %s", o.getName(), o.getDir().getVHDL(), o.getType().getVHDL()));
	}

	@Override
	public void visitHDLParameter(HDLParameter o) {
		//HDLUtils.print(dest, offset, String.format("%s : %s := %s", o.getName(), o.getType().getVHDL(), o.getDefaultValue().getVHDL()));
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.getType() instanceof HDLUserDefinedType){
			((HDLUserDefinedType)o.getType()).accept(this);
		}
		String s;
		//if(o.getResetValue() != null && o.isRegister()){
		/*if(o.getResetValue() != null){
			s = String.format("signal %s : %s := %s;", o.getName(), o.getType().getVHDL(), o.getResetValue().getVHDL());
		}else{
			s = String.format("signal %s : %s;", o.getName(), o.getType().getVHDL());
		}
		
		HDLUtils.println(dest, offset, s);
		if(o.isDebugFlag()){
			HDLUtils.println(dest, offset, String.format("attribute mark_debug of %s : signal is \"true\";", o.getName()));
			HDLUtils.println(dest, offset, String.format("attribute keep of %s : signal is \"true\";", o.getName()));
			HDLUtils.println(dest, offset, String.format("attribute S of %s : signal is \"true\";", o.getName()));
		}*/
	}

	@Override
	public void visitHDLType(HDLPrimitiveType o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		if(definedType.containsKey(o) == true) return;
		definedType.put(o, true);
		/*HDLUtils.println(dest, offset, String.format("type %s is (", o.getName()));
		String sep = "";
		for(HDLValue s: o.getItems()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getVHDL()));
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, offset, String.format("%s  );", Constant.BR));*/
	}

	@Override
	public void visitHDLInstanceRef(HDLInstanceRef o){
	}

}
