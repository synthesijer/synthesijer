package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.PrimitiveTypeKind;

public class NewClassExpr extends Expr{

	private String clazz;
	private ArrayList<Expr> params = new ArrayList<>();

	public NewClassExpr(Scope scope){
		super(scope);
	}

	public void setClassName(String str){
		clazz = str;
	}

	public void addParam(Expr expr){
		params.add(expr);
	}

	public String getClassName(){
		return clazz;
	}

	public ArrayList<Expr> getParameters(){
		return params;
	}

	public void accept(SynthesijerExprVisitor v){
		v.visitNewClassExpr(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Type getType() {
		return PrimitiveTypeKind.DECLARED;
	}

	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Expr expr: params){
			for(Variable var: expr.getSrcVariables()) list.add(var);
		}
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables(){
		return new Variable[]{};
	}

	@Override
	public boolean hasMethodInvocation() {
		for(Expr expr: params){
			if(expr.hasMethodInvocation()) return true;
		}
		return false;
	}
}
