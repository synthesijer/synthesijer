package synthesijer.hdl.expr;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLType;

public class HDLCombinationExpr implements HDLExpr {

    private final int uid;
    private final HDLOp op;
    private final HDLExpr[] args;

    private final HDLSignal result;

    public HDLCombinationExpr(HDLModule m, int uid, HDLOp op, HDLExpr... args) {
        this.uid = uid;
        this.op = op;
        this.args = args;
        for (HDLExpr expr : args) {
            if (expr == null)
                throw new RuntimeException("An argument of HDLCombinationExpr is null.");
        }
        // System.out.println(this);
        HDLType type = decideExprType(op, this.args);
        result = m.newSignal(String.format("tmp_%04d", uid), type, HDLSignal.ResourceKind.WIRE, this, true);
        // System.out.println(result);
    }

    public HDLType getType() {
        return result.getType();
    }

    public HDLOp getOp() {
        return op;
    }

    private HDLType getPriorType(HDLType t1, HDLType t2) {
        if (t1 == null && t2 != null)
            return t2;
        if (t1 != null && t2 == null)
            return t1;
        HDLType t = null;
        if (t1.getKind().hasWidth() && t1.getKind().isPrimitive() && t2.getKind().hasWidth()
                && t2.getKind().isPrimitive()) {
            boolean signFlag = false;
            if (t1.isSigned() || t2.isSigned())
                signFlag = true;
            HDLType tmp = ((HDLPrimitiveType) t1).getWidth() > ((HDLPrimitiveType) t2).getWidth() ? t1 : t2;
            if (signFlag) {
                t = HDLPrimitiveType.genSignedType(((HDLPrimitiveType) tmp).getWidth());
            } else {
                t = HDLPrimitiveType.genVectorType(((HDLPrimitiveType) tmp).getWidth());
            }
        } else if (t1.getKind().hasWidth() && t1.getKind().isPrimitive()) {
            t = t1;
        } else if (t1.getKind().hasWidth() && t1.getKind().isPrimitive()) {
            t = t2;
        } else if (t1.getKind() == HDLType.KIND.BIT && t2.getKind() == HDLType.KIND.BIT) {
            t = t1;
        } else {
        }
        return t;
    }

    private String getArgsString(HDLExpr[] args) {
        String s = "";
        for (HDLExpr a : args) {
            s += a.toString() + " ";
        }
        return s;
    }

    private HDLType getConcatType(HDLPrimitiveType t0, HDLPrimitiveType t1) {
        return HDLPrimitiveType.genVectorType(t0.getWidth() + t1.getWidth());
    }

    private HDLType getDropHeadType(HDLPrimitiveType t, HDLValue v) {
        if (t.isVector()) {
            return HDLPrimitiveType.genVectorType(t.getWidth() - Integer.parseInt(v.getValue()));
        } else {
            return HDLPrimitiveType.genSignedType(t.getWidth() - Integer.parseInt(v.getValue()));
        }
    }

    private HDLType getPaddingHeadType(HDLPrimitiveType t, HDLValue v) {
        if (t.isSigned()) {
            return HDLPrimitiveType.genSignedType(t.getWidth() + Integer.parseInt(v.getValue()));
        } else {
            return HDLPrimitiveType.genVectorType(t.getWidth() + Integer.parseInt(v.getValue()));
        }
    }

    private HDLType getTakeType(HDLPrimitiveType t, HDLValue v) {
        if (t.isSigned()) {
            return HDLPrimitiveType.genSignedType(Integer.parseInt(v.getValue()));
        } else {
            return HDLPrimitiveType.genVectorType(Integer.parseInt(v.getValue()));
        }
    }

    private HDLType decideExprType(HDLOp op, HDLExpr[] args) {
        if (op.isInfix()) {
            return getPriorType(args[0].getType(), args[1].getType());
        } else if (op.isCompare()) {
            return HDLPrimitiveType.genBitType();
        } else {
            switch (op) {
            case NOT:
            case MSB_FLAP:
                return args[0].getType();
            case REF:
                return HDLPrimitiveType.genBitType();
            case IF:
                return getPriorType(args[1].getType(), args[2].getType());
            case CONCAT:
                return getConcatType((HDLPrimitiveType) args[0].getType(), (HDLPrimitiveType) args[1].getType());
            case DROPHEAD:
                return getDropHeadType((HDLPrimitiveType) args[0].getType(), (HDLValue) args[1]);
            case TAKE:
                return getTakeType((HDLPrimitiveType) args[0].getType(), (HDLValue) args[1]);
            case PADDINGHEAD:
            case PADDINGHEAD_ZERO:
                return getPaddingHeadType((HDLPrimitiveType) args[0].getType(), (HDLValue) args[1]);
            case ARITH_RSHIFT:
            case LOGIC_RSHIFT:
            case LSHIFT:
                return args[0].getType();
            case ARITH_RSHIFT32:
            case LOGIC_RSHIFT32:
            case LSHIFT32:
                return HDLPrimitiveType.genSignedType(32);
            case ARITH_RSHIFT64:
            case LOGIC_RSHIFT64:
            case LSHIFT64:
                return HDLPrimitiveType.genSignedType(64);
            case ID:
                return args[0].getType();
            case HDLMUL: {
                int w0 = ((HDLPrimitiveType) (args[0].getType())).getWidth();
                int w1 = ((HDLPrimitiveType) (args[0].getType())).getWidth();
                return HDLPrimitiveType.genSignedType(w0 + w1);
            }
            default:
                return HDLPrimitiveType.genUnknowType();
            }

        }
    }

    @Override
    public void accept(HDLTreeVisitor v) {
        v.visitHDLExpr(this);
    }

    public String toString() {
        return "HDLCombination::(" + op + " " + getArgsString(args) + ")";
    }

    // TODO experimental code
    private String toSigned(HDLExpr expr) {
        if (expr instanceof HDLPreDefinedConstant)
            return expr.getVHDL();
        if (expr instanceof HDLValue)
            return expr.getVHDL();
        if (expr.getType().isVector()) {
            return String.format("signed(%s)", expr.getVHDL());
        } else {
            return expr.getVHDL();
        }
    }

    // TODO experimental code
    private String toStdLogicVector(HDLExpr e) {
        String s = e.getResultExpr().getVHDL();
        if (e.getType().isSigned()) {
            return "std_logic_vector(" + s + ")";
        }
        return s;
    }

    private int toImmValue(HDLExpr expr) {
        int value = 0;
        if (expr instanceof HDLValue) {
            value = Integer.parseInt(((HDLValue) expr).getValue());
        } else {
            value = Integer.parseInt(((HDLValue) ((HDLCombinationExpr) (args[1])).args[0]).getValue());
        }
        return value;
    }

    @Override
    public String getVHDL() {
        boolean arith_shift_mode = false;

        if (op.isInfix()) {
            String s = String.format("%s %s %s", toSigned(args[0].getResultExpr()), op.getVHDL(),
                    toSigned(args[1].getResultExpr()));
            if (getResultExpr().getType().isVector())
                s = "std_logic_vector(" + s + ")";
            return s;
        } else if (op.isCompare()) {
            if (args[0] instanceof HDLValue && args[0].getType().isBit()) {
                if(args[0].getVHDL().equals("\'1\'")){
                    if(op == HDLOp.EQ) return args[1].getResultExpr().getVHDL();
                    if(op == HDLOp.NEQ) return String.format("not %s", args[1].getResultExpr().getVHDL());
                }else if(args[0].getVHDL().equals("\'0\'")){
                    if(op == HDLOp.EQ) return String.format("not %s", args[1].getResultExpr().getVHDL());
                    if(op == HDLOp.NEQ) return args[1].getResultExpr().getVHDL();
                }else{
                    SynthesijerUtils.warn("constant bit operation will be error:" + this);
                }
            }
            return String.format("'1' when %s %s %s else '0'", toSigned(args[0].getResultExpr()), op.getVHDL(),
                    toSigned(args[1].getResultExpr()));
        } else {
            switch (op) {
            case NOT:
                return String.format("%s %s", op.getVHDL(), args[0].getResultExpr().getVHDL());
            case MSB_FLAP: {
                String v = args[0].getResultExpr().getVHDL();
                HDLPrimitiveType t = (HDLPrimitiveType) (args[0].getResultExpr().getType());
                return String.format("(not %s(%d-1)) & %s(%d-2 downto 0)", v, t.getWidth(), v, t.getWidth());
            }
            case REF:
                return String.format("%s(%s)", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
            case IF: {
                HDLType t = getResultExpr().getType();
                String arg1, arg2;
                arg1 = (t.isSigned() && args[1].getResultExpr().getType().isVector())
                        ? toSigned(args[1].getResultExpr()) : args[1].getResultExpr().getVHDL();
                arg2 = (t.isSigned() && args[2].getResultExpr().getType().isVector())
                        ? toSigned(args[2].getResultExpr()) : args[2].getResultExpr().getVHDL();
                String r = String.format("%s when %s = '1' else %s", arg1, args[0].getResultExpr().getVHDL(), arg2);
                return r;
            }
            case CONCAT: {
                String arg0 = toStdLogicVector(args[0].getResultExpr());
                String arg1 = toStdLogicVector(args[1].getResultExpr());
                return String.format("%s & %s", arg0, arg1);
            }
            case DROPHEAD: {
                HDLPrimitiveType t = (HDLPrimitiveType) args[0].getResultExpr().getType();
                return String.format("%s(%d - %s - 1 downto 0)", args[0].getResultExpr().getVHDL(), t.getWidth(),
                        ((HDLValue) args[1]).getValue());
            }
            case TAKE: {
                HDLPrimitiveType t = (HDLPrimitiveType) getResultExpr().getType();
                return String.format("%s(%d - 1 downto 0)", args[0].getResultExpr().getVHDL(), t.getWidth());
            }
            case PADDINGHEAD: {
                HDLPrimitiveType t0 = (HDLPrimitiveType) args[0].getResultExpr().getType();
                HDLPrimitiveType t1 = (HDLPrimitiveType) decideExprType(op, args);
                return String.format("(%d-1 downto %d => %s(%d)) & %s", t1.getWidth(), t0.getWidth(),
                        args[0].getResultExpr().getVHDL(), t0.getWidth() - 1, args[0].getResultExpr().getVHDL());
            }
            case PADDINGHEAD_ZERO: {
                HDLPrimitiveType t0 = (HDLPrimitiveType) args[0].getResultExpr().getType();
                HDLPrimitiveType t1 = (HDLPrimitiveType) decideExprType(op, args);
                return String.format("(%d-1 downto %d => '0') & %s", t1.getWidth(), t0.getWidth(),
                        args[0].getResultExpr().getVHDL(), t0.getWidth() - 1, args[0].getResultExpr().getVHDL());
            }
            case ARITH_RSHIFT:
            case ARITH_RSHIFT32:
            case ARITH_RSHIFT64:
                arith_shift_mode = true; // update
            case LOGIC_RSHIFT:
            case LOGIC_RSHIFT32:
            case LOGIC_RSHIFT64: {
                String str;
                HDLPrimitiveType t0 = (HDLPrimitiveType) result.getType();
                int shift = toImmValue(args[1]);
                if (args[0] instanceof HDLValue) {
                    int value = toImmValue(args[0]);
                    HDLValue tmp = arith_shift_mode
                            ? new HDLValue(String.valueOf(value >> shift), (HDLPrimitiveType) result.getType())
                            : new HDLValue(String.valueOf(value >>> shift), (HDLPrimitiveType) result.getType());
                    str = tmp.getVHDL();
                }
                HDLPrimitiveType ta = (HDLPrimitiveType) (args[0].getResultExpr().getType());
                String padding = arith_shift_mode
                        ? String.format("%s(%d)", args[0].getResultExpr().getVHDL(), ta.getWidth() - 1) : "'0'";

                // int msb = t0.getWidth()-shift-1;
                int msb = t0.getWidth() - 1;
                // if(msb > ta.getWidth()-1) msb = ta.getWidth()-1;
                int lsb = shift;
                if (lsb > ta.getWidth() - 1)
                    lsb = 0;

                if (shift >= ta.getWidth()) {
                    //str = String.format("(%d-1 downto 0 => %s)", t0.getWidth(), padding);
                    str = args[0].getResultExpr().getVHDL(); // as is
                } else if (shift >= 1 && shift < ta.getWidth()) {
                    str = String.format("(%d-1 downto %d => %s) & %s(%d downto %d)", t0.getWidth(), (msb - lsb + 1), // shift,
                            padding, args[0].getResultExpr().getVHDL(), msb, lsb);
                } else { // shift == 0
                    if (t0.getWidth() == ta.getWidth()) { // as is
                        str = args[0].getResultExpr().getVHDL();
                    } else {
                        str = String.format("(%d-1 downto %d => %s) & %s", t0.getWidth(), msb + 1, padding,
                                args[0].getResultExpr().getVHDL());
                    }
                }
                return str;
            }
            case LSHIFT:
            case LSHIFT32:
            case LSHIFT64: {
                HDLPrimitiveType t0 = (HDLPrimitiveType) (result.getType());
                int shift = toImmValue(args[1]);
                if (args[0] instanceof HDLValue) {
                    int value = toImmValue(args[0]);
                    HDLValue tmp = new HDLValue(String.valueOf(value << shift), (HDLPrimitiveType) result.getType());
                    return tmp.getVHDL();
                }
                HDLPrimitiveType ta = (HDLPrimitiveType) (args[0].getResultExpr().getType());
                int msb = t0.getWidth() - shift - 1;
                if (msb > ta.getWidth() - 1)
                    msb = ta.getWidth() - 1;
                String str = "";

                if (shift >= ta.getWidth()) {
                    str = args[0].getResultExpr().getVHDL(); // as is
                }else if (shift >= 1) {
                    str += String.format("%s(%d downto %d) & (%d-1 downto %d => '0')",
                            args[0].getResultExpr().getVHDL(), msb, 0, shift, 0);
                } else {
                    str += args[0].getResultExpr().getVHDL();
                }

                if (msb + shift < t0.getWidth() - 1) {
                    str = String.format("(%d downto %d => '0') & ", t0.getWidth() - 1, msb + shift + 1) + str;
                }

                return str;
            }
            case ID:
                return args[0].getResultExpr().getVHDL();
            case HDLMUL: {
                String s = String.format("%s %s %s", toSigned(args[0].getResultExpr()), op.getVHDL(),
                        toSigned(args[1].getResultExpr()));
                if (getResultExpr().getType().isVector())
                    s = "std_logic_vector(" + s + ")";
                return s;
            }
            default:
                return "(" + op + " " + getArgsString(args) + ")";
            }
        }
    }

    private String getPaddingBitInVerilog(String key, int idx, int len) {
        String s = "";
        String sep = "";
        for (int i = 0; i < len; i++) {
            s += sep + key + "[" + idx + "]";
            sep = ",";
        }
        return s;
    }

    @Override
    public String getVerilogHDL() {
        boolean arith_shift_mode = false;
        if (op.isInfix()) {
            return String.format("%s %s %s", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(),
                    args[1].getResultExpr().getVerilogHDL());
        } else if (op.isCompare()) {
            return String.format("%s %s %s ? 1'b1 : 1'b0", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(),
                    args[1].getResultExpr().getVerilogHDL());
        } else {
            switch (op) {
            case NOT:
                return String.format("%s%s", op.getVerilogHDL(), args[0].getResultExpr().getVerilogHDL());
            case MSB_FLAP: {
                String v = args[0].getResultExpr().getVerilogHDL();
                HDLPrimitiveType t = (HDLPrimitiveType) (args[0].getResultExpr().getType());
                return String.format("{(~%s[%d-1]), %s[%d-2:0]}", v, t.getWidth(), v, t.getWidth());
            }
            case REF:
                return String.format("%s[%s]", args[0].getResultExpr().getVerilogHDL(),
                        args[1].getResultExpr().getVerilogHDL());
            case IF:
                return String.format("%s == 1'b1 ? %s : %s", args[0].getResultExpr().getVerilogHDL(),
                        args[1].getResultExpr().getVerilogHDL(), args[2].getResultExpr().getVerilogHDL());
            case CONCAT:
                return String.format("{%s, %s}", args[0].getResultExpr().getVerilogHDL(),
                        args[1].getResultExpr().getVerilogHDL());
            case DROPHEAD: {
                HDLPrimitiveType t = (HDLPrimitiveType) args[0].getResultExpr().getType();
                return String.format("%s[%d - %s - 1 : 0]", args[0].getResultExpr().getVerilogHDL(), t.getWidth(),
                        args[1].getResultExpr().getVerilogHDL());
            }
            case TAKE: {
                HDLPrimitiveType t = (HDLPrimitiveType) getResultExpr().getType();
                return String.format("%s[%d - 1 : 0]", args[0].getResultExpr().getVerilogHDL(), t.getWidth());
            }
            case PADDINGHEAD: {
                HDLPrimitiveType t0 = (HDLPrimitiveType) args[0].getResultExpr().getType();
                HDLPrimitiveType t1 = (HDLPrimitiveType) decideExprType(op, args);
                return String.format("{%s,%s}", getPaddingBitInVerilog(args[0].getResultExpr().getVerilogHDL(),
                        t0.getWidth() - 1, t1.getWidth() - t0.getWidth()), args[0].getResultExpr().getVerilogHDL());
            }
            case PADDINGHEAD_ZERO: {
                HDLPrimitiveType t0 = (HDLPrimitiveType) args[0].getResultExpr().getType();
                HDLPrimitiveType t1 = (HDLPrimitiveType) decideExprType(op, args);
                return String.format("{%d'b0, %s}", t1.getWidth() - t0.getWidth(),
                        args[0].getResultExpr().getVerilogHDL());
            }
            case ARITH_RSHIFT:
            case ARITH_RSHIFT32:
            case ARITH_RSHIFT64:
                arith_shift_mode = true; // overwrite
            case LOGIC_RSHIFT:
            case LOGIC_RSHIFT32:
            case LOGIC_RSHIFT64: {
                String str;
                HDLPrimitiveType t0 = (HDLPrimitiveType) result.getType();
                int shift = toImmValue(args[1]);
                if (args[0] instanceof HDLValue) {
                    int value = toImmValue(args[0]);
                    HDLValue tmp = arith_shift_mode
                            ? new HDLValue(String.valueOf(value >> shift), (HDLPrimitiveType) result.getType())
                            : new HDLValue(String.valueOf(value >>> shift), (HDLPrimitiveType) result.getType());
                    str = tmp.getVerilogHDL();
                }
                HDLPrimitiveType ta = (HDLPrimitiveType) (args[0].getResultExpr().getType());

                // int msb = t0.getWidth()-shift-1;
                // if(msb > ta.getWidth()-1) msb = ta.getWidth()-1;
                int msb = t0.getWidth() - 1;
                int lsb = shift;
                if (lsb > ta.getWidth() - 1)
                    lsb = 0;
                String key = args[0].getResultExpr().getVerilogHDL();
                if (shift >= ta.getWidth()) {
                    str = args[0].getResultExpr().getVerilogHDL(); // as is
                    /*
                    if (arith_shift_mode) {
                        str = String.format("{%s}", getPaddingBitInVerilog(key, ta.getWidth() - 1, t0.getWidth()));
                    } else {
                        str = String.format("%d'b0", t0.getWidth());
                    }
                    */
                } else if (shift >= 1 && shift < ta.getWidth()) {
                    String pad;
                    String val = String.format("%s[%d:%d]", key, msb, lsb);

                    if (arith_shift_mode) {
                        pad = getPaddingBitInVerilog(key, ta.getWidth() - 1, t0.getWidth() - (msb - lsb + 1));
                    } else {
                        pad = String.format("%d'b0", t0.getWidth() - (msb - lsb + 1));
                    }

                    str = String.format("{%s,%s}", pad, val);

                } else { // shift == 0
                    if (t0.getWidth() == ta.getWidth()) { // as is
                        str = key;
                    } else {
                        String pad;
                        if (arith_shift_mode) {
                            pad = getPaddingBitInVerilog(key, ta.getWidth() - 1, t0.getWidth() - (msb - lsb + 1));
                        } else {
                            pad = String.format("%d'b0", t0.getWidth() - (msb - lsb));
                        }
                        str = String.format("{%s,%s}", pad, key);
                    }
                }
                return str;
            }

            case LSHIFT:
            case LSHIFT32:
            case LSHIFT64: {
                // HDLPrimitiveType t0 =
                // (HDLPrimitiveType)args[0].getResultExpr().getType();
                HDLPrimitiveType t0 = (HDLPrimitiveType) (result.getType());
                // int shift =
                // Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
                int shift = toImmValue(args[1]);
                if (args[0] instanceof HDLValue) {
                    int value = toImmValue(args[0]);
                    // HDLValue tmp = new HDLValue(String.valueOf(value <<
                    // shift), (HDLPrimitiveType)args[0].getType());
                    HDLValue tmp = new HDLValue(String.valueOf(value << shift), (HDLPrimitiveType) result.getType());
                    return tmp.getVerilogHDL();
                }
                HDLPrimitiveType ta = (HDLPrimitiveType) (args[0].getResultExpr().getType());
                int msb = t0.getWidth() - shift - 1;
                if (msb > ta.getWidth() - 1)
                    msb = ta.getWidth() - 1;
                String str = "";
                if (shift >= ta.getWidth()) {
                    str = args[0].getResultExpr().getVerilogHDL(); // as is
                }else if (shift >= 1) {
                    str = String.format("%s[%d:%d],%d'b0", args[0].getResultExpr().getVerilogHDL(),
                            t0.getWidth() - shift - 1, 0, shift);
                } else {
                    str = args[0].getResultExpr().getVerilogHDL();
                }
                if (msb + shift < t0.getWidth() - 1) {
                    str = String.format("%d'b0,", t0.getWidth() - (msb + shift) - 1) + str;
                }
                str = "{" + str + "}";
                return str;
            }
            case ID:
                return args[0].getResultExpr().getVerilogHDL();
            case HDLMUL:
                return String.format("%s %s %s", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(),
                        args[1].getResultExpr().getVerilogHDL());
            default:
                return "(" + op + " " + getArgsString(args) + ")";
            }
        }
    }

    @Override
    public HDLExpr getResultExpr() {
        return result;
    }

    private void getSrcSignals(ArrayList<HDLSignal> list, HDLExpr arg) {
        HDLSignal[] src = arg.getSrcSignals();
        if (src != null) {
            for (HDLSignal s : src) {
                list.add(s);
            }
        }
        if (arg.getResultExpr() instanceof HDLSignal) {
            list.add((HDLSignal) arg.getResultExpr());
        }
    }

    @Override
    public HDLSignal[] getSrcSignals() {
        ArrayList<HDLSignal> list = new ArrayList<>();
        for (HDLExpr arg : args) {
            getSrcSignals(list, arg);
        }
        return list.toArray(new HDLSignal[] {});
    }

}
