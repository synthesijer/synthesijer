package synthesijer.ast.opt;

import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
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
import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.BreakStatement;
import synthesijer.ast.statement.ContinueStatement;
import synthesijer.ast.statement.DoWhileStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ForStatement;
import synthesijer.ast.statement.IfStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.SkipStatement;
import synthesijer.ast.statement.SwitchStatement;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.ast.type.StringType;

public class NullOptimizer {
    
    public Module conv(Module m){
        Module newM = new Module(m.getParentScope(), m.getName(), m.getImportTable(), m.getExtending(), m.getImplementingList());
        newM.setSynthesijerHDL(m.isSynthesijerHDL());
        for(VariableDecl v: m.getVariableDecls()){
            VariableDecl newV = conv(newM, v);
            newM.addVariableDecl(newV);
        }
        for(Method method: m.getMethods()){
            Method newMethod = conv(newM, method);
            newM.addMethod(newMethod);
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
        return newV;
    }
        
    private Method conv(Module parent, Method m){

        Method newMethod = new Method(parent, m.getName(), m.getType());
        
        newMethod.setUnsynthesizableFlag(m.isUnsynthesizable());
        newMethod.setAutoFlag(m.isAuto());
        newMethod.setAutoFlag(m.isSynchronized());
        newMethod.setPrivateFlag(m.isPrivate());
        newMethod.setRawFlag(m.isRaw());
        newMethod.setCombinationFlag(m.isCombination());
        newMethod.setCombinationFlag(m.isParallel());
        newMethod.setNoWaitFlag(m.isNoWait());
        newMethod.setConstructorFlag(m.isConstructor());
        newMethod.setCallStackFlag(m.hasCallStack());
        newMethod.setCallStackSize(m.getCallStackSize());
        
        newMethod.setArgs(m.getArgs());
        for(VariableDecl v: m.getVariableDecls()){
            VariableDecl newV = conv(newMethod, v);
            newMethod.addVariableDecl(newV);
        }

        // inner block
        for(VariableDecl v: m.getBody().getVariableDecls()){
            VariableDecl newV = conv(m.getBody(), v);
            newMethod.getBody().addVariableDecl(newV);
        }
        for(Statement s: m.getBody().getStatements()){
            Statement newS = conv(m.getBody(), s);
            newMethod.getBody().addStatement(newS);
        }

        return newMethod;
    }

    /*
     *  convert statements
     */
    public Statement conv(Scope scope, Statement stmt){
        if(stmt == null){
            return null;
        }else if(stmt instanceof BlockStatement){
            return conv(scope, (BlockStatement)stmt);
        }else if(stmt instanceof BreakStatement){
            return conv(scope, (BreakStatement)stmt);
        }else if(stmt instanceof ContinueStatement){
            return conv(scope, (ContinueStatement)stmt);
        }else if(stmt instanceof DoWhileStatement){
            return conv(scope, (DoWhileStatement)stmt);
        }else if(stmt instanceof ExprStatement){
            return conv(scope, (ExprStatement)stmt);
        }else if(stmt instanceof ForStatement){
            return conv(scope, (ForStatement)stmt);
        }else if(stmt instanceof IfStatement){
            return conv(scope, (IfStatement)stmt);
        }else if(stmt instanceof ReturnStatement){
            return conv(scope, (ReturnStatement)stmt);
        }else if(stmt instanceof SkipStatement){
            return conv(scope, (SkipStatement)stmt);
        }else if(stmt instanceof SwitchStatement){
            return conv(scope, (SwitchStatement)stmt);
        }else if(stmt instanceof SynchronizedBlock){
            return conv(scope, (SynchronizedBlock)stmt);
        }else if(stmt instanceof TryStatement){
            return conv(scope, (TryStatement)stmt);
        }else if(stmt instanceof VariableDecl){
            return conv(scope, (VariableDecl)stmt);
        }else if(stmt instanceof WhileStatement){
            return conv(scope, (WhileStatement)stmt);
        }else{
            throw new RuntimeException("[Internal Error] no convesion rule for " + stmt);
        }
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

    private BlockStatement conv(Scope scope, BlockStatement stmt){
        BlockStatement newS = new BlockStatement(scope);
        for(VariableDecl v: stmt.getVariableDecls()){
            newS.addVariableDecl(conv(newS, v));
        }
        for(Statement s: stmt.getStatements()){
            newS.addStatement(conv(newS, s));
        }
        return newS;
    }

    private BreakStatement conv(Scope scope, BreakStatement stmt){
        BreakStatement newS = new BreakStatement(scope);
        return newS;
    }
    
    private ContinueStatement conv(Scope scope, ContinueStatement stmt){
        ContinueStatement newS = new ContinueStatement(scope);
        return newS;
    }
    
    private DoWhileStatement conv(Scope scope, DoWhileStatement stmt){
        DoWhileStatement newS = new DoWhileStatement(scope);
        newS.setBody(conv(scope, stmt.getBody()));
        newS.setCondition(conv(scope, stmt.getCondition()));
        return newS;
    }
    
    private ExprStatement conv(Scope scope, ExprStatement stmt){
        Expr newE = conv(scope, stmt.getExpr());
        ExprStatement newS = new ExprStatement(scope, newE);
        return newS;
    }
    
    private ForStatement conv(Scope scope, ForStatement stmt){
        ForStatement newS = new ForStatement(scope);
        for(VariableDecl v: stmt.getVariableDecls()){
            newS.addVariableDecl(conv(newS, v));
        }
        for(Statement s: stmt.getInitializations()){
            newS.addInitialize(conv(newS, s));
        }
        newS.setCondition(conv(newS, stmt.getCondition()));
        for(Statement s: stmt.getUpdates()){
            newS.addUpdate(conv(newS, s));
        }
        newS.setBody(conv(newS, stmt.getBody()));
        return newS;
    }
    
    private IfStatement conv(Scope scope, IfStatement stmt){
        IfStatement newS = new IfStatement(scope);
        newS.setCondition(conv(scope, stmt.getCondition()));
        newS.setThenPart(conv(scope, stmt.getThenPart()));
        if(stmt.getElsePart() != null){
            newS.setElsePart(conv(scope, stmt.getElsePart()));
        }
        return newS;
    }
    
    private ReturnStatement conv(Scope scope, ReturnStatement stmt){
        ReturnStatement newS = new ReturnStatement(scope);
        newS.setExpr(conv(scope, stmt.getExpr()));
        return newS;
    }
    
    private SkipStatement conv(Scope scope, SkipStatement stmt){
        SkipStatement newS = new SkipStatement(scope);
        return newS;
    }
    
    private SwitchStatement conv(Scope scope, SwitchStatement stmt){
        SwitchStatement newS = new SwitchStatement(scope);
        newS.setSelector(conv(scope, stmt.getSelector()));
        for(SwitchStatement.Elem elem : stmt.getElements()){
            Expr pat = elem.getPattern();
            SwitchStatement.Elem newElem = newS.newElement(conv(scope, pat));
            for(Statement s: elem.getStatements()){
                newElem.addStatement(conv(scope, s));
            }
        }
        for(Statement s: stmt.getDefaultElement().getStatements()){
            newS.getDefaultElement().addStatement(conv(scope, s));
        }
        return newS;
    }
    
    private SynchronizedBlock conv(Scope scope, SynchronizedBlock stmt){
        SynchronizedBlock newS = new SynchronizedBlock(scope);
        for(VariableDecl v: stmt.getVariableDecls()){
            newS.addVariableDecl(conv(newS, v));
        }
        for(Statement s: stmt.getStatements()){
            newS.addStatement(conv(newS, s));
        }
        return newS;
    }
    
    private TryStatement conv(Scope scope, TryStatement stmt){
        TryStatement newS = new TryStatement(scope);
        newS.setBody(stmt.getBody());
        return newS;
    }
    
    private WhileStatement conv(Scope scope, WhileStatement stmt){
        WhileStatement newS = new WhileStatement(scope);
        newS.setCondition(conv(scope, stmt.getCondition()));
        newS.setBody(conv(scope, stmt.getBody()));
        return newS;
    }
    
    private Expr conv(Scope scope, ArrayAccess expr){
        ArrayAccess newE = new ArrayAccess(scope);
        newE.setIndex(conv(scope, expr.getIndex()));
        newE.setIndexed(conv(scope, expr.getIndexed()));
        return newE;
    }

    private Expr conv(Scope scope, AssignExpr expr){
        AssignExpr newE = new AssignExpr(scope);
        newE.setRhs(conv(scope, expr.getRhs()));
        newE.setLhs(conv(scope, expr.getLhs()));
        return newE;
    }

    private Expr conv(Scope scope, AssignOp expr){
        AssignOp newE = new AssignOp(scope);
        newE.setRhs(conv(scope, expr.getRhs()));
        newE.setLhs(conv(scope, expr.getLhs()));
        newE.setOp(expr.getOp());
        return newE;
    }

    private Expr conv(Scope scope, BinaryExpr expr){
        BinaryExpr newE = new BinaryExpr(scope);
        newE.setRhs(conv(scope, expr.getRhs()));
        newE.setLhs(conv(scope, expr.getLhs()));
        newE.setOp(expr.getOp());
        return newE;
    }

    private Expr conv(Scope scope, CondExpr expr){
        CondExpr newE = new CondExpr(scope);
        newE.setCond(conv(scope, expr.getCond()));
        newE.setTruePart(conv(scope, expr.getTruePart()));
        newE.setFalsePart(conv(scope, expr.getFalsePart()));
        return newE;
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
        Literal newE = expr.copy(scope);
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
        ParenExpr newE = new ParenExpr(scope);
        newE.setExpr(conv(scope, expr.getExpr()));
        return newE;
    }
    
    private Expr conv(Scope scope, TypeCast expr){
        TypeCast newE = new TypeCast(scope);
        newE.setExpr(conv(scope, expr.getExpr()));
        newE.setTargetType(expr.getType());
        return newE;
    }
    
    private Expr conv(Scope scope, UnaryExpr expr){
        UnaryExpr newE = new UnaryExpr(scope);
        newE.setArg(conv(scope, expr.getArg()));
        newE.setOp(expr.getOp());
        newE.setPostfix(expr.isPostfix());
        return newE;
    }
    
}
