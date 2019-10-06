package synthesijer.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.ArrayList;

import synthesijer.ast.type.PrimitiveTypeKind;

public class IrohaWriter {

	private final String name;

	public IrohaWriter(String name){
		this.name = name;
	}

	public void generate(SchedulerInfo info) throws IOException{
		try(
				PrintStream ir = new PrintStream(new FileOutputStream(new File(name + ".iroha")));
		){
			ir.println("(MODULE " + info.getName());
			//genVariables(ir, info.getModuleVarList().toArray(new VariableOperand[]{}));
			//genVariables(ir, info.getModuleVarList().toArray(new Operand[]{}));
			ir.println("(PARAMS (RESET-POLARITY true) (RESET-NAME reset))");
			for(SchedulerBoard b: info.getBoardsList()){
				genSchedulerBoard(ir, b);
			}
			ir.println(")");
			ir.close();
		}catch(IOException e){
			throw new IOException(e);
		}
	}

	private String pad(int w){
		String s = "";
		for(int i = 0; i < w; i++) s += " ";
		return s;
	}

	private void println(PrintStream ir, int offset, String mesg){
		ir.println(pad(offset)+mesg);
	}

	private int table_id = 1;
	private void genSchedulerBoard(PrintStream ir, SchedulerBoard b){
		println(ir, 2, "(TABLE " + (table_id++));
		genRegisters(ir, b);
		genResources(ir, b);
		println(ir, 2, "  (INITIAL 1)");
		int insn_id = 1;
		for(SchedulerSlot s: b.getSlots()){
			insn_id = genState(ir, b, s, insn_id);
		}
		println(ir, 2, ")");
	}

	private int genState(PrintStream ir, SchedulerBoard b, SchedulerSlot slot, int insn_id){
		println(ir, 4, "(STATE " + (slot.getStepId()+1)); // step id is 0-origin, but state id is 1-origin
		if(slot.getNextStep().length == 1 && slot.hasBranchOp() == false){
			insn_id += genStateTr(ir, (slot.getNextStep()[0]+1), insn_id);
		}
		for(SchedulerItem i: slot.getItems()){
			insn_id += genStateInsn(ir, b, i, insn_id);
		}
		println(ir, 4, ")");
		return insn_id;
	}

	private int genStateTr(PrintStream ir, int nextId, int id){
		println(ir, 6, String.format("(INSN %d tr 1 () (%d) () ())", id, nextId));
		return 1;
	}

	private int genStateInsn(PrintStream ir, SchedulerBoard b, SchedulerItem item, int id){
		switch(item.getOp()){
			case METHOD_EXIT:
				// busy <= 0
				println(ir, 6, String.format("(INSN %d ext_output %d () () (%d) ())", id++, 3, 2));
				return 1;
			case METHOD_ENTRY:
				// req -> req_wire
				println(ir, 6, String.format("(INSN %d ext_input %d () () () (%d))", id++, 2, 1));
				// tr into 2 when req_wire == '1'
				println(ir, 6, String.format("(INSN %d tr 1 () (2 3) (1) ())", id++));
				// req_wire -> busy
				println(ir, 6, String.format("(INSN %d ext_output %d () () (%d) ())", id++, 3, 1));
				ArrayList<VariableOperand> params = getMethodParams(b);
				for(VariableOperand v: params){
					int s = ext_variables.get(v);
					int d = registers.get(v);
					println(ir, 6, String.format("(INSN %d ext_input %d () () () (%d))", id++, s, d));
				}
				return 3 + params.size();
			case RETURN:
				Operand o = item.getSrcOperand()[0];
				println(ir, 6, String.format("(INSN %d ext_output %d () () (%d) ())", id++, 4, registers.get(o)));
				println(ir, 6, String.format("(INSN %d tr 1 () (1) () ())", id++));
				return 2;
			case JP:
				println(ir, 6, String.format("(INSN %d tr 1 () (1) () ())", id++));
				return 1;
			default:
				System.out.println(item.toSexp());
				if(item.getSrcOperand() == null){
					System.out.println("Warning : source operand is null");
				}else if(item.getSrcOperand().length == 2){
					int r = resources.get(item);
					int s0 = 0, s1 = 0, d = 0;
					if(registers.get(item.getSrcOperand()[0]) == null){
						System.out.println("Warning : skip :" + item.getSrcOperand()[0].dump());
					}else{
						s0 = registers.get(item.getSrcOperand()[0]);
					}
					if(registers.get(item.getSrcOperand()[1]) == null){
						System.out.println("Warning : skip :" + item.getSrcOperand()[1].dump());
					}else{
						s1 = registers.get(item.getSrcOperand()[1]);
					}
					if(registers.get(item.getDestOperand()) == null){
						System.out.println("Warning : skip :" + item.getDestOperand().dump());
					}else{
						d = registers.get(item.getDestOperand());
					}
					println(ir, 6, String.format("(INSN %d %s %d () () (%d %d) (%d))", id, convOp(item.getOp()), r, s0, s1, d));
				}else if(item.getSrcOperand().length == 1){
					int r = resources.get(item);
					Operand operand_s0 = item.getSrcOperand()[0];
					int s0 = 0;
					if(operand_s0 instanceof ConstantOperand){
						try{
							s0 = Integer.parseInt(((ConstantOperand)operand_s0).getValue());
						}catch(NumberFormatException e){
							System.out.println("Warning : skip [number format exception]:" + ((ConstantOperand)operand_s0).getValue());
						}
					}else if(registers.get(operand_s0) == null){
						System.out.println("Warning : skip :" + operand_s0.dump());
					}else{
						s0 = registers.get(operand_s0);
					}
					int d = 0;
					if(item.getDestOperand() == null){
						System.out.println("Warning : destination is null");
					}else if(registers.get(item.getDestOperand()) == null){
						System.out.println("Warning : skip :" + item.getDestOperand().dump());
					}else{
						d = registers.get(item.getDestOperand());
					}
					println(ir, 6, String.format("(INSN %d %s %d () () (%d) (%d))", id, convOp(item.getOp()), r, s0, d));
				}else{
					System.out.println("skip unknown item [ " + item.toSexp() + " ]");
				}
				return 1;
		}
	}

	private String convOp(Op o){
		switch(o){
			case ADD: return "add";
			default: return "UNKNOWN";
		}
	}

	private ArrayList<VariableOperand> getMethodParams(SchedulerBoard b){
		ArrayList<VariableOperand> r = new ArrayList<>();
		for(Operand v: b.getVarList()){
			if(v instanceof VariableOperand){
				VariableOperand vo = ((VariableOperand)v);
				if(!(vo.isMethodParam())) continue; // skip
				if(!(vo.getType() instanceof PrimitiveTypeKind)) continue; // skip
				r.add(vo);
			}
		}
		return r;
	}

	private Hashtable<Operand, Integer> registers = new Hashtable<>();
	private Hashtable<VariableOperand, Integer> ext_variables = new Hashtable<>();
	private Hashtable<SchedulerItem, Integer> resources = new Hashtable<>();

	private void genResource(PrintStream ir, VariableOperand vo, int id){
		ext_variables.put(vo, id);
		println(ir, 6, "(RESOURCE " + id + " ext_input");
		println(ir, 6, "  () ()");
		println(ir, 6, "  (PARAMS (INPUT " + vo.getMethodName() + "_" + vo.getOrigName() + ")");
		println(ir, 6, "   (WIDTH " + ((PrimitiveTypeKind)vo.getType()).getWidth() + "))");
		println(ir, 6, ")");
	}

	private void genResource(PrintStream ir, SchedulerItem item, int id){
		if(item.getOp() == Op.METHOD_EXIT) return;
		if(item.getOp() == Op.METHOD_ENTRY) return;
		if(item.getOp() == Op.RETURN) return;
		if(item.getOp() == Op.JP) return;
		resources.put(item, id);
		println(ir, 6, "(RESOURCE " + id + " " + convOp(item.getOp()));
		println(ir, 6, "  (UINT 32 UINT 32) (UINT 32)");
		println(ir, 6, "  (PARAMS)");
		println(ir, 6, ")");
	}

	private void genResources(PrintStream ir, SchedulerBoard b){
		int resource_id = 1;
		println(ir, 4, "(RESOURCES");

		// basic state transition
		println(ir, 6, "(RESOURCE " + (resource_id++) + " tr");
		println(ir, 6, "  () ()");
		println(ir, 6, "  (PARAMS)");
		println(ir, 6, ")");

		// method request
		println(ir, 6, "(RESOURCE " + (resource_id++) + " ext_input");
		println(ir, 6, "  () ()");
		println(ir, 6, "  (PARAMS (INPUT " + b.getName() + "_req" + ")");
		println(ir, 6, "   (WIDTH " + 0 + "))");
		println(ir, 6, ")");

		// method busy
		println(ir, 6, "(RESOURCE " + (resource_id++) + " ext_output");
		println(ir, 6, "  () ()");
		println(ir, 6, "  (PARAMS (OUTPUT " + b.getName() + "_busy" + ")");
		println(ir, 6, "   (WIDTH " + 0 + "))");
		println(ir, 6, ")");

		// method return
		if(b.getReturnType() instanceof PrimitiveTypeKind){
			println(ir, 6, "(RESOURCE " + (resource_id++) + " ext_output");
			println(ir, 6, "  () ()");
			println(ir, 6, "  (PARAMS (OUTPUT " + b.getName() + "_return" + ")");
			println(ir, 6, "   (WIDTH " + ((PrimitiveTypeKind)b.getReturnType()).getWidth() + "))");
			println(ir, 6, ")");
		}

		// method parameters
		ArrayList<VariableOperand> params = getMethodParams(b);
		for(VariableOperand v: params){
			genResource(ir, v, resource_id++);
		}

		// required operations for this method
		for(SchedulerSlot s: b.getSlots()){
			for(SchedulerItem i: s.getItems()){
				genResource(ir, i, resource_id++);
			}
		}

		println(ir, 4, ")");
	}

	private void genRegisters(PrintStream ir, SchedulerBoard b){
		Operand[] vars = b.getVarList().toArray(new Operand[]{});
		int register_id = 1;
		println(ir, 4, "(REGISTERS ");

		// method request internal wire
		println(ir, 6, "(REGISTER " + (register_id++) + " " + b.getName() + "_req_wire");
		println(ir, 6, "  WIRE UINT 0 ()");
		println(ir, 6, ")");

		// method request internal wire
		println(ir, 6, "(REGISTER " + (register_id++) + " SJR_CONST_ZERO");
		println(ir, 6, "  CONST UINT 0 0");
		println(ir, 6, ")");

		for(Operand v: vars){
			if(!(v instanceof VariableOperand)) continue; // skip
			VariableOperand vo = (VariableOperand)v;
			genRegister(ir, v, register_id++);
		}
		println(ir, 4, ")");
	}

	private void genRegister(PrintStream ir, Operand v, int id){
		if(!(v.getType() instanceof PrimitiveTypeKind)) return;
		registers.put(v, id);
		println(ir, 6, "(REGISTER " + id + " " + v.getName());
		println(ir, 6, "  REG INT " + ((PrimitiveTypeKind)v.getType()).getWidth() + " ()");
		println(ir, 6, ")");
	}
}
