package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class VariableDecl extends ExprContainStatement{
	
    private Variable var;
    private final Expr init;
    private boolean flagGlobalConstant = false;
    private boolean flagPublic = false;
    private boolean flagVolatile = false;
    private boolean flagMethodParam = false;
	
    public VariableDecl(Scope scope, String name, Type type, Expr init){
		super(scope);
		this.init = init;
		var = new Variable(name, type, scope.getMethod(), init);
    }
	
    public String getName(){
		return var.getName();
    }
	
    public Expr getExpr(){
		return init;
    }
	
    public void setGlobalConstant(boolean f){
		flagGlobalConstant = f;
		var.setGlobalConstant(f);
    }
	
    public boolean isGlobalConstant(){
		return flagGlobalConstant;
    }

    public void setPublic(boolean f){
		flagPublic = f;
		var.setPublic(f);
    }
	
    public boolean isPublic(){
		return flagPublic;
    }

    public void setVolatile(boolean f){
		flagVolatile = f;
		var.setVolatile(f);
    }
	
    public boolean isVolatile(){
		return flagVolatile;
    }

    public void setDebug(boolean f){
		var.setDebug(f);
    }
	
    public boolean isDebug(){
		return var.isDebug();
    }

    public void setMethodParam(boolean f){
		flagMethodParam = f;
		var.setMethodParam(f);
    }
	
    public boolean isMethodParam(){
		return flagMethodParam;
    }

    public Variable getVariable(){
		return var;
    }
	
    public Type getType(){
		return var.getType();
    }
	
    public boolean hasInitExpr(){
		return init != null;
    }
	
    public Expr getInitExpr(){
		return init;
    }
		
    public void accept(SynthesijerAstVisitor v){
		v.visitVariableDecl(this);
    }
	
    public String toString(){
		return "VariableDecl: " + var; 
    }
	
    @Override
    public Variable[] getSrcVariables(){
		if(getExpr() != null){
			return getExpr().getSrcVariables();
		}else{
			return new Variable[]{};
		}
    }
	
    @Override
    public Variable[] getDestVariables(){
		return new Variable[]{var};
    }
}
