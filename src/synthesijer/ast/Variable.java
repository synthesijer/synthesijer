package synthesijer.ast;

public class Variable {

    private final String name;
    private final Type type;
    private final Method method;
    private Expr init;
    private boolean flagGlobalConstant = false;
    private boolean flagPublic = false;
    private boolean flagVolatile = false;
    private boolean flagMethodParam = false;
    private String uniqName = null;
    private boolean flagDebug = false;

    public Variable(String n, Type t, Method method, Expr init) {
        this.name = n;
        this.type = t;
        this.method = method;
        this.init = init;
        // System.out.println("new Variable:" + n);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Method getMethod() {
        return method;
    }

    public void setGlobalConstant(boolean f) {
        flagGlobalConstant = f;
    }

    public boolean isGlobalConstant() {
        return flagGlobalConstant;
    }

    public void setPublic(boolean f) {
        flagPublic = f;
    }

    public boolean isPublic() {
        return flagPublic;
    }

    public void setVolatile(boolean f) {
        flagVolatile = f;
    }

    public boolean isVolatile() {
        return flagVolatile;
    }

    public void setDebug(boolean f) {
        flagDebug = f;
    }

    public boolean isDebug() {
        return flagDebug;
    }

    public void setMethodParam(boolean f) {
        flagMethodParam = f;
    }

    public boolean isMethodParam() {
        return flagMethodParam;
    }

    public Expr getInitExpr() {
        return init;
    }

    public void setInitExpr(Expr e) {
        init = e;
    }

    public String getUniqueName() {
        if (uniqName != null)
            return uniqName;
        if (method != null) {
            uniqName = method.getName() + "_" + name + "_" + method.getUniqId();
        } else {
            uniqName = name;
        }
        return uniqName;
    }

    public String toString() {
        return "Varialble: " + getUniqueName();
    }

}
