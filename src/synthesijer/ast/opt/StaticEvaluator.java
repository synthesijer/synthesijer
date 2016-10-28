package synthesijer.ast.opt;

import java.util.Hashtable;

import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.SimpleEvaluator;
import synthesijer.ast.Type;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.CondExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.ast.type.StringType;

public class StaticEvaluator {

    private Hashtable<String, Expr> identTable = new Hashtable<>();
    
    public Module conv(Module m){
        Module newM = new Module(m.getParentScope(), m.getName(), m.getImportTable(), m.getExtending(), m.getImplementingList());
        newM.setSynthesijerHDL(m.isSynthesijerHDL());
        for(VariableDecl v: m.getVariableDecls()){
            VariableDecl newV = conv(newM, v);
            newM.addVariableDecl(newV);
        }

        // update
        for(VariableDecl v: newM.getVariableDecls()){
            if(identTable.containsKey(v.getVariable().getName())){
                Expr e = identTable.get(v.getVariable().getName());
                v.setInitExpr(e);
            }
        }
        
        // methods are copied as is
        for(Method method: m.getMethods()){
            newM.addMethod(method);
        }
        return newM;
    }
        
    private VariableDecl conv(Scope parent, VariableDecl v){
        Expr newInitExpr = conv(parent, v.getInitExpr());
        VariableDecl newV = new VariableDecl(parent, v.getName(), v.getType(), newInitExpr);
        newV.setGlobalConstant(v.isGlobalConstant());
        newV.setPublic(v.isPublic());
        newV.setVolatile(v.isVolatile());
        newV.setMethodParam(v.isMethodParam());
        if(newInitExpr != null){
            identTable.put(v.getVariable().getName(), newInitExpr);
        }
        return newV;
    }
    
    /*
     *  convert expressions
     */
    public Expr conv(Scope scope, Expr expr){
        if(expr == null){
            return null;
        }else if(expr instanceof ArrayAccess){
            return conv(scope, (ArrayAccess)expr);
        }else if(expr instanceof AssignExpr){
            return conv(scope, (AssignExpr)expr);
        }else if(expr instanceof AssignOp){
            return conv(scope, (AssignOp)expr);
        }else if(expr instanceof BinaryExpr){
            return conv(scope, (BinaryExpr)expr);
        }else if(expr instanceof CondExpr){
            return conv(scope, (CondExpr)expr);
        }else if(expr instanceof FieldAccess){
            return conv(scope, (FieldAccess)expr);
        }else if(expr instanceof Ident){
            return conv(scope, (Ident)expr);
        }else if(expr instanceof Literal){
            return conv(scope, (Literal)expr);
        }else if(expr instanceof MethodInvocation){
            return conv(scope, (MethodInvocation)expr);
        }else if(expr instanceof NewArray){
            return conv(scope, (NewArray)expr);
        }else if(expr instanceof NewClassExpr){
            return conv(scope, (NewClassExpr)expr);
        }else if(expr instanceof ParenExpr){
            return conv(scope, (ParenExpr)expr);
        }else if(expr instanceof TypeCast){
            return conv(scope, (TypeCast)expr);
        }else if(expr instanceof UnaryExpr){
            return conv(scope, (UnaryExpr)expr);
        }else{
            throw new RuntimeException("[Internal Error] no convesion rule for " + expr);
        }
    }

    private Expr conv(Scope scope, ArrayAccess expr){
        ArrayAccess newE = new ArrayAccess(scope);
        newE.setIndex(conv(scope, expr.getIndex()));
        newE.setIndexed(conv(scope, expr.getIndexed()));
        return newE;
    }
    
    private Expr conv(Scope scope, AssignExpr expr){
        Expr rhs = conv(scope, expr.getRhs());
        Expr lhs = conv(scope, expr.getRhs());
        if(lhs instanceof Ident){
            identTable.put(((Ident)lhs).getSymbol(), rhs);
        }
        return rhs;
    }

    private Expr conv(Scope scope, AssignOp expr){
        Expr rhs = conv(scope, expr.getRhs());
        Expr lhs = conv(scope, expr.getLhs());
        Expr ret = null;
        if(lhs instanceof Literal && rhs instanceof Literal){
            Literal a = (Literal)lhs;
            Literal b = (Literal)rhs;
            try{
                Literal newL = SimpleEvaluator.eval(expr.getOp(), a.getType(), a.getValueAsStr(), b.getType(), b.getValueAsStr());
                ret = newL;
            }catch(Exception e){
                System.out.println("eval failuer:" + expr);
            }
        }else{
            AssignOp newE = new AssignOp(scope);
            newE.setRhs(rhs);
            newE.setLhs(lhs);
            newE.setOp(expr.getOp());
            ret = newE;
        }
        if(lhs instanceof Ident){
            identTable.put(((Ident)lhs).getSymbol(), rhs);
        }
        return ret;
    }
    
    private boolean isConstant(Expr e){
        if(e instanceof Literal) return true;
        else if(e instanceof Ident){
            return isConstant(identTable.get(((Ident)e).getSymbol()));
        }else{
            return false;
        }
    }

    private Literal getLiteral(Expr e){
        if(e instanceof Literal) return (Literal)e;
        else{
            return getLiteral(identTable.get(((Ident)e).getSymbol()));
        }
    }

    private Expr conv(Scope scope, BinaryExpr expr){
        Expr rhs = conv(scope, expr.getRhs());
        Expr lhs = conv(scope, expr.getLhs());
        if(isConstant(lhs) && isConstant(rhs)){
            Literal a = getLiteral(lhs);
            Literal b = getLiteral(rhs);
            try{
                Literal newL = SimpleEvaluator.eval(expr.getOp(), a.getType(), a.getValueAsStr(), b.getType(), b.getValueAsStr());
                return newL;
            }catch(Exception e){
                System.out.println("eval failuer:" + expr);
            }
        }
        BinaryExpr newE = new BinaryExpr(scope);
        newE.setRhs(rhs);
        newE.setLhs(lhs);
        newE.setOp(expr.getOp());
        return newE;
    }

    private Expr conv(Scope scope, CondExpr expr){
        Expr cond = conv(scope, expr.getCond());
        Expr truePart = conv(scope, expr.getTruePart());
        Expr falsePart = conv(scope, expr.getFalsePart());
        if(cond instanceof Literal && ((Literal) cond).isBoolean()){
            if(Boolean.parseBoolean(((Literal)cond).getValueAsStr())){
                return truePart;
            }else{
                return falsePart;
            }
        }else{
            CondExpr newE = new CondExpr(scope);
            newE.setCond(cond);
            newE.setTruePart(truePart);
            newE.setFalsePart(falsePart);
            return newE;
        }
    }

    private Expr conv(Scope scope, FieldAccess expr){
        FieldAccess newE = new FieldAccess(scope);
        Expr ident = conv(scope, expr.getIdent());
        if(!(ident instanceof Ident)){
            throw new RuntimeException("FieildAccess should be Ident: " + ident);
        }
        newE.setIdent((Ident)(conv(scope, expr.getIdent())));
        newE.setSelected(conv(scope, expr.getSelected()));
        return newE;
    }

    private Expr conv(Scope scope, Ident expr){
        Ident newE = new Ident(scope);
        newE.setIdent(expr.getSymbol());
        return newE;
    }

    private Expr conv(Scope scope, Literal expr){
        Type t = expr.getType();
        Literal newE = new Literal(scope);
        if(t == PrimitiveTypeKind.UNDEFINED){
            // nothing to do 
        }else if(t == PrimitiveTypeKind.BOOLEAN){
            newE.setValue(Boolean.parseBoolean(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.BYTE){
            newE.setValue(Byte.parseByte(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.CHAR){
            newE.setValue((char)(Integer.parseInt(expr.getValueAsStr())));
        }else if(t == PrimitiveTypeKind.SHORT){
            newE.setValue(Short.parseShort(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.INT){
            newE.setValue(Integer.parseInt(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.LONG){
            newE.setValue(Long.parseLong(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.DOUBLE){
            newE.setValue(Double.parseDouble(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.FLOAT){
            newE.setValue(Float.parseFloat(expr.getValueAsStr()));
        }else if(t == PrimitiveTypeKind.NULL){
            newE.setNull();
        }else if(t instanceof StringType){
            newE.setValue(expr.getValueAsStr());
        }else{
            SynthesijerUtils.warn("unsupported type for Literal: " + t);
        }
        return newE;
    }

    private Expr conv(Scope scope, MethodInvocation expr){
        MethodInvocation newE = new MethodInvocation(scope);
        newE.setMethod(conv(scope, expr.getMethod()));
        for(Expr e: expr.getParameters()){
            newE.addParameter(conv(scope, e));
        }
        return newE;
    }

    private Expr conv(Scope scope, NewArray expr){
        NewArray newE = new NewArray(scope);
        for(Expr e: expr.getDimExpr()){
            newE.addDimExpr(conv(scope, e));
        }
        for(Expr e: expr.getElems()){
            newE.addElem(conv(scope, e));
        }
        return newE;
    }

    private Expr conv(Scope scope, NewClassExpr expr){
        NewClassExpr newE = new NewClassExpr(scope);
        for(Expr e: expr.getParameters()){
            newE.addParam(conv(scope, e));
        }
        newE.setClassName(expr.getClassName());
        return newE;
    }
    
    private Expr conv(Scope scope, ParenExpr expr){
        Expr e = expr.getExpr();
        if(e instanceof Literal){
            return e;
        }else{
            ParenExpr newE = new ParenExpr(scope);
            newE.setExpr(e);
            return newE;
        }
    }
    
    private Expr conv(Scope scope, TypeCast expr){
        Expr e = conv(scope, expr.getExpr());
        Type t = expr.getType();
        if(e instanceof Literal){
            ((Literal)e).castType(t);
            return e;
        }else{
            TypeCast newE = new TypeCast(scope);
            newE.setExpr(conv(scope, expr.getExpr()));
            newE.setTargetType(expr.getType());
            return newE;
        }
    }
    
    private Expr conv(Scope scope, UnaryExpr expr){
        Expr arg = conv(scope, expr.getArg());
        if(isConstant(arg)){
            Literal l = getLiteral(arg);
            try{
                Literal newL = SimpleEvaluator.eval(expr.getOp(), l.getType(), l.getValueAsStr());
                if(expr.getOp() == Op.INC || expr.getOp() == Op.DEC){
                    identTable.put(((Ident)arg).getSymbol(), newL); // update
                }
                if(expr.isPostfix()){
                    return l.copy(scope); // as is
                }else{
                    return newL; // updated value
                }
            }catch(Exception e){
                System.out.println("eval failuer:" + expr);
            }
        }
        UnaryExpr newE = new UnaryExpr(scope);
        newE.setArg(arg);
        newE.setOp(expr.getOp());
        newE.setPostfix(expr.isPostfix());
        return newE;
    }
    
}
