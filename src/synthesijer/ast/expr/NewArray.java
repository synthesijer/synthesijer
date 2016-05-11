package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.PrimitiveTypeKind;

public class NewArray extends Expr{
	
	private ArrayList<Expr> dimExpr = new ArrayList<>();
	private ArrayList<Expr> elemExpr = new ArrayList<>();
	
	public NewArray(Scope scope){
		super(scope);
	}

	public void addDimExpr(Expr expr){
		if(expr instanceof Literal){
			dimExpr.add(expr);
		}else if(expr instanceof Ident){
			String sym = ((Ident)expr).getSymbol();
			Variable var = getScope().search(sym);
            dimExpr.add(var.getInitExpr());
		}else{
		    dimExpr.add(expr);
		}
	}

	public ArrayList<Expr> getDimExpr(){
		return dimExpr;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitNewArray(this);
	}
	
	public void addElem(Expr expr){
		elemExpr.add(expr);
	}
	
	public ArrayList<Expr> getElems(){
		return elemExpr;
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
		return PrimitiveTypeKind.ARRAY;
	}
	
	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Expr expr: dimExpr){
			for(Variable var: expr.getSrcVariables()) list.add(var);
		}
		for(Expr expr: elemExpr){
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
		for(Expr expr: dimExpr){
			if(expr.hasMethodInvocation()) return true;
		}
		for(Expr expr: elemExpr){
			if(expr.hasMethodInvocation()) return true;
		}
		return false;
	}
}
