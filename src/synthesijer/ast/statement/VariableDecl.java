package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSignal;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class VariableDecl extends ExprContainStatement{
	
	private Variable var;
	private final Expr init;
	
	public VariableDecl(Scope scope, String name, Type type, Expr init){
		super(scope);
		this.init = init;
		var = new Variable(name, type, scope.getMethod());
		scope.registrate(var);
	}
	
	public String getName(){
		return var.getName();
	}
	
	public Expr getExpr(){
		return init;
	}
	
	public Variable getVariable(){
		return var;
	}
	
	public Type getType(){
		return var.getType();
	}
	
	public boolean hasInitExpr(){
		return (init != null);
	}
	
	public Expr getInitExpr(){
		return init;
	}
	
	public void makeCallGraph(){
		if(init != null) init.makeCallGraph();
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<var name=\"%s\">\n", var.getName());
		var.getType().dumpAsXML(dest);
		if(init != null) init.dumpAsXML(dest);
		dest.printf("</var>");
	}

	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		if(init != null){
			State s = m.newState("var_init");
			s.setBody(this);
			s.addTransition(dest);
			return s;
		}else{
			return dest;
		}
	}
	
	public void genHDLSignal(HDLModule m){
		HDLSignal s = var.genHDLSignal(m);
		if(var.getType() instanceof PrimitiveTypeKind){
			if(init != null){ s.setResetValue(init.getHDLExprResult()); }
		}else if(var.getType() instanceof ArrayType){
		}else if(var.getType() instanceof ComponentType){
		}else{
		}
	}

	public void genHDLPort(HDLModule m){
		var.genHDLPort(m, HDLPort.DIR.IN);
	}

	@Override
	public void generateHDL(HDLModule m) {
		genHDLSignal(m);
	}

	
}
