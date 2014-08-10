package synthesijer.ast2hdl;

import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Op;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.SynthesijerExprVisitor;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.ast.type.StringType;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class GenerateHDLExprVisitor implements SynthesijerExprVisitor{
		
	private final GenerateHDLModuleVisitor parent;
	private final SequencerState state;
	
	private HDLExpr result;
	
	public GenerateHDLExprVisitor(GenerateHDLModuleVisitor parent, SequencerState state){
		this.parent = parent;
		this.state = state;
	}
	
	public HDLExpr getResult(){
		return result;
	}
	
	private HDLExpr stepIn(Expr expr){
		GenerateHDLExprVisitor visitor = new GenerateHDLExprVisitor(parent, state);
		expr.accept(visitor);
		return visitor.getResult();
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		HDLSignal addr = null, we = null, oe = null;
		if(o.getIndexed() instanceof Ident){
			Ident id = (Ident)o.getIndexed();
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			HDLInstance inst = (HDLInstance)var;
			
			addr = inst.getSignalForPort("raddress"); // see synthsijer.lib.BlockRAM
			we = inst.getSignalForPort("we"); // see synthsijer.lib.BlockRAM
			oe = inst.getSignalForPort("oe"); // see synthsijer.lib.BlockRAM
			//System.out.println("mem:"+inst.getSignalForPort("oe"));
			result = inst.getSignalForPort("dout"); // see synthsijer.lib.BlockRAM
		}else if(o.getIndexed() instanceof FieldAccess){
			FieldAccess fa = (FieldAccess)o.getIndexed();
			Ident id = (Ident)(fa.getSelected());
			Ident sym = fa.getIdent();
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			HDLInstance inst = (HDLInstance)var;
			addr = inst.getSignalForPort(sym.getSymbol() + "_raddress");
			we = inst.getSignalForPort(sym.getSymbol() + "_we");
			//System.out.println("field.mem:"+sym.getSymbol() + "_oe");
			oe = inst.getSignalForPort(sym.getSymbol() + "_oe");
			result = inst.getSignalForPort(sym.getSymbol() + "_dout");
			//System.out.println(addr);
			//System.out.println(we);
			//System.out.println(result);
		}else{
			SynthesijerUtils.error(String.format("%s(%s) cannot convert to HDL.", o.getIndexed(), o.getIndexed().getClass()));
			System.exit(0);
		}
		addr.setAssign(state, 0, stepIn(o.getIndex()));
		we.setAssign(state, HDLPreDefinedConstant.LOW);
		if(oe != null){
			oe.setAssign(state, 0, HDLPreDefinedConstant.HIGH);
			oe.setDefaultValue(HDLPreDefinedConstant.LOW);
		}
		if(state != null){
			state.setMaxConstantDelay(2);
		}else{
			SynthesijerUtils.warn("Array acceess in non-state region is not supported yet: " + o.getIndexed() + "[" + o.getIndex() + "]");
		}
	}
		
	private HDLOp convOp(Op op){
		switch(op){
		case PLUS : return HDLOp.ADD;
		case MINUS : return HDLOp.SUB;
		case AND : return HDLOp.AND;
		case LAND : return HDLOp.AND;		
		case LOR : return HDLOp.OR;
		case OR : return HDLOp.OR;
		case XOR : return HDLOp.XOR;
		case COMPEQ : return HDLOp.EQ;
		case NEQ : return HDLOp.NEQ;
		case GT : return HDLOp.GT;
		case GEQ : return HDLOp.GEQ;
		case LT : return HDLOp.LT;
		case LEQ : return HDLOp.LEQ;
		case INC : return HDLOp.ADD;
		case DEC : return HDLOp.SUB;
		case LNOT : return HDLOp.NOT;
		case ARITH_RSHIFT : return HDLOp.ARITH_RSHIFT;
		case LOGIC_RSHIFT : return HDLOp.LOGIC_RSHIFT;
		case LSHIFT : return HDLOp.LSHIFT;
		default:
			SynthesijerUtils.warn("undefined op:" + op);
			return HDLOp.UNDEFINED;
		}
	}
	
	@Override
	public void visitAssignExpr(AssignExpr o) {
		HDLExpr expr = stepIn(o.getRhs());
		Expr lhs = o.getLhs();
		if(lhs instanceof Ident){
			Ident id = (Ident)lhs;
			HDLVariable sig = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			sig.setAssign(state, expr);
		}else if(lhs instanceof ArrayAccess){
			ArrayAccess aa = (ArrayAccess)lhs;
			HDLSignal addr = null, we = null, din = null;
			if(aa.getIndexed() instanceof Ident){
				Ident id = (Ident)aa.getIndexed();
				HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
				HDLInstance inst = (HDLInstance)var;
				addr = inst.getSignalForPort("waddress"); // see synthsijer.lib.BlockRAM
				we = inst.getSignalForPort("we"); // see synthsijer.lib.BlockRAM
				din = inst.getSignalForPort("din"); // see synthsijer.lib.BlockRAM
			}else if(aa.getIndexed() instanceof FieldAccess){
				FieldAccess fa = (FieldAccess)aa.getIndexed();
				Ident id = (Ident)(fa.getSelected());
				Ident sym = fa.getIdent();
				HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
				HDLInstance inst = (HDLInstance)var;
				addr = inst.getSignalForPort(sym.getSymbol() + "_waddress");
				we = inst.getSignalForPort(sym.getSymbol() + "_we");
				din = inst.getSignalForPort(sym.getSymbol() + "_din");
			}else{
				SynthesijerUtils.warn("Unsupported array access for non-Ident/FieldAccess: " + aa.getIndexed());
				System.exit(0);
			}
			addr.setAssign(state, stepIn(aa.getIndex()));
			we.setAssign(state, HDLPreDefinedConstant.HIGH);
			we.setDefaultValue(HDLPreDefinedConstant.LOW);
			din.setAssign(state, expr);
			
			
		}else if(lhs instanceof FieldAccess){
			HDLExpr l = stepIn(lhs);
			if(l instanceof HDLSignal){
				((HDLSignal)l).setAssign(state, expr);
			}else{
				throw new RuntimeException("unsupported yet: " + o);
			}
		}else{
			throw new RuntimeException("unsupported yet: " + o);
		}
		result = expr;
	}
	
	@Override
	public void visitAssignOp(AssignOp o) {
		HDLExpr expr = parent.module.newExpr(convOp(o.getOp()), stepIn(o.getLhs()), stepIn(o.getRhs()));
		Ident id = (Ident)o.getLhs();
		HDLVariable sig = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
		sig.setAssign(state, expr);
		result = expr;
	}
		
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		HDLExpr lhs = stepIn(o.getLhs());
		HDLExpr rhs = stepIn(o.getRhs());
		result = parent.module.newExpr(convOp(o.getOp()), lhs, rhs);
		//HDLSignal sig = parent.module.newSignal("binaryexpr_result_" + this.hashCode(), HDLPrimitiveType.genVectorType(32));
		//sig.setAssign(null, parent.module.newExpr(convOp(o.getOp()), lhs, rhs));
		//result = sig;
	}
	
	@Override
	public void visitFieldAccess(FieldAccess o) {
		//System.out.println(o);
		Ident id = (Ident)o.getSelected();
		if(o.getScope().search(id.getSymbol()) != null){
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			HDLInstance inst = (HDLInstance)var;
			HDLSignal sig = inst.getSignalForPort(o.getIdent().getSymbol());
			if(sig == null){
				sig = inst.getSignalForPort("field_" + o.getIdent().getSymbol() + "_output");
			}
			result = sig.getResultExpr();
		}else{ // search global static field
			Module m = Manager.INSTANCE.searchModule(id.getSymbol());
			Variable v = m.search(o.getIdent().getSymbol());
			if(v.isGlobalConstant()){
				result = stepIn(v.getInitExpr());
			}
		}
	}
	
	@Override
	public void visitIdent(Ident o) {
		Variable v = o.getScope().search(o.getSymbol());
		result = parent.getHDLVariable(v);
	}
	
	private HDLPrimitiveType convToHDLType(Type type){
		if(type instanceof PrimitiveTypeKind){
			switch((PrimitiveTypeKind)type){
			case BOOLEAN: return HDLPrimitiveType.genBitType();
			case BYTE:    return HDLPrimitiveType.genSignedType(8);
			case CHAR:    return HDLPrimitiveType.genVectorType(16);
			case SHORT:   return HDLPrimitiveType.genSignedType(16);
			case INT:     return HDLPrimitiveType.genSignedType(32);
			case LONG:    return HDLPrimitiveType.genSignedType(64);
			case DOUBLE:  return HDLPrimitiveType.genVectorType(64);
			case FLOAT:   return HDLPrimitiveType.genVectorType(32);
			default: return HDLPrimitiveType.genUnknowType();
			}
		}else if(type instanceof StringType){
			return HDLPrimitiveType.genStringType();
		}else{
			return HDLPrimitiveType.genUnknowType();
		}
	}
	
	@Override
	public void visitLitral(Literal o) {
		//result = new HDLValue(o.getValueAsStr(), convToHDLType(o.getType()));
		result = parent.module.newExpr(HDLOp.ID, new HDLValue(o.getValueAsStr(), convToHDLType(o.getType())));
		//System.out.println(o + " -> " + result);
	}
	
	@Override
	public void visitMethodInvocation(MethodInvocation o) {

		HDLInstance inst = null;
		Method method = null;
		boolean localFlag = false;
		if(o.getMethod() instanceof Ident){ // local method
			method = o.getTargetMethod();
			localFlag = true;
		}else if(o.getMethod() instanceof FieldAccess){
			FieldAccess fa = (FieldAccess)o.getMethod();
			if(fa.getSelected() instanceof Ident == false){
				throw new RuntimeException("Unsupported mulit-link method invocation: " + o.getMethod());
			}
			Ident id = (Ident)(fa.getSelected());
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			inst = (HDLInstance)var;
			method = o.getTargetMethod();
		}else{
			throw new RuntimeException("Unsupported method invocation: " + o.getMethod());
		}

		for(int i = 0; i < o.getParameters().size(); i++){
			HDLSignal s;
			if(localFlag){
				String arg = o.getMethodName() + "_" + method.getArgs()[i].getVariable().getName();
				s = parent.module.getSignal(arg);
			}else{
				String arg = o.getMethodName() + "_" + method.getArgs()[i].getVariable().getName() + "_in";
				s = inst.getSignalForPort(arg);
			}
			s.setAssign(state, 0, stepIn(o.getParameters().get(i)));
		}

		// method request
		HDLSignal req;
		if(localFlag){
			req = parent.module.getSignal(o.getMethodName() + "_req_local");
		}else{
			req = inst.getSignalForPort(o.getMethodName() + "_req");
		}
		req.setAssign(state, 0, HDLPreDefinedConstant.HIGH);
		req.setDefaultValue(HDLPreDefinedConstant.LOW);

		// method busy, to wait for method execution
		HDLSignal busy;
		if(localFlag){
			busy = parent.module.getSignal(o.getMethodName() + "_busy_sig");
		}else{
			busy = inst.getSignalForPort(o.getMethodName() + "_busy");
		}
		HDLSignal flag = parent.module.newSignal(String.format("%s_%04d", busy.getName(), parent.module.getExprUniqueId()), HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
		flag.setAssign(null,
				parent.module.newExpr(HDLOp.EQ,
						parent.module.newExpr(HDLOp.AND,
								parent.module.newExpr(HDLOp.EQ, busy, HDLPreDefinedConstant.LOW),
								parent.module.newExpr(HDLOp.EQ, req, HDLPreDefinedConstant.LOW)),
								HDLPreDefinedConstant.HIGH));
		state.setMaxConstantDelay(1);
		state.setStateExitFlag(flag);
		
		if(method.getType() == PrimitiveTypeKind.VOID){
			result = null;
		}else{
			if(localFlag){
				result = parent.module.getSignal(o.getMethodName() + "_return_sig");
			}else{
				result = inst.getSignalForPort(o.getMethodName() + "_return");
			}
		}
	}
	
	@Override
	public void visitNewArray(NewArray o) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visitParenExpr(ParenExpr o) {
		result = stepIn(o.getExpr());
	}
	
	@Override
	public void visitTypeCast(TypeCast o) {
		HDLExpr expr = stepIn(o.getExpr());
		
		HDLPrimitiveType target = convToHDLType(o.getType());
		HDLPrimitiveType given = (HDLPrimitiveType)(expr.getType());
		
		if(target.getWidth() == given.getWidth()){
			result = expr; // as is
		}else{
			//HDLSignal tmp = parent.module.newSignal("cast_tmp_" + this.hashCode(), given, HDLSignal.ResourceKind.WIRE);
			//tmp.setAssign(null, expr);
			//HDLSignal cast = parent.module.newSignal("cast_result_" + this.hashCode(), target, HDLSignal.ResourceKind.WIRE);

			if(target.getWidth() > given.getWidth()){
				result = parent.module.newExpr(HDLOp.PADDINGHEAD, expr, new HDLValue(String.valueOf(target.getWidth()-given.getWidth()), HDLPrimitiveType.genIntegerType()));
			}else{
				result = parent.module.newExpr(HDLOp.DROPHEAD, expr, new HDLValue(String.valueOf(given.getWidth()-target.getWidth()), HDLPrimitiveType.genIntegerType()));
			}
			//result = cast;
		}
		//System.out.println(result);
	}
	
	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		HDLExpr arg = stepIn(o.getArg());
		HDLVariable sig = (HDLVariable)arg;
		HDLExpr expr;
		if(o.getOp() == Op.INC || o.getOp() == Op.DEC){
			expr = parent.module.newExpr(convOp(o.getOp()), arg, HDLPreDefinedConstant.INTEGER_ONE);
			sig.setAssign(state, expr);
		}else{
			expr = parent.module.newExpr(convOp(o.getOp()), arg);
		}
		result = expr;
	}
	
}
