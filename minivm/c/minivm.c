#include <stdio.h>

#include "minivm.h"
#include "util.h"

long
ld(long *resources, long *stack, int fp, struct operand o)
{
  if(o.field == MODULE_RESOURCE){
    return resources[o.ptr];
  }else if(o.field == LOCAL_STACK){
    return stack[fp + o.ptr];
  }else{ // internal error
    minivm_error("[minivm] ld: unknown operand kind");
  }
}

void
st(long *resources, long *stack, int fp, struct operand o, long value)
{
  if(o.field == MODULE_RESOURCE){
    resources[o.ptr] = value;
  }else if(o.field == LOCAL_STACK){
    stack[fp + o.ptr] = value;
  }else{ // internal error
    minivm_error("[minivm] ld: unknown operand kind");
  }
}

void
binary_op(long *resources, long *stack, int fp, struct instruction_code code)
{
}

void
unary_op(long *resources, long *stack, int fp, struct instruction_code code)
{
}

int
minivm_fsm(struct instruction_code *code,
	   long *resources,
	   long *stack,
	   int *gtable,
	   int method_entry_pc
	   )
{

  int pc = method_entry_pc;
  int fp = 0;
  int dptr = 0;

  long s0, s1;
  
  for(;;){
    struct instruction_code c = code[pc];
    switch(c.op){
    case METHOD_ENTRY :
      break;
    case METHOD_EXIT :
      return;
    case ASSIGN :
      s0 = ld(resources, stack, fp, c.src0);
      st(resources, stack, fp, c.dest, s0);
      break;
    case NOP :
      break;
    case ADD :
    case SUB :
    case MUL32:
    case MUL64:
    case DIV32:
    case DIV64:
    case MOD32:
    case MOD64:
    case LT:
    case LEQ:
    case GT:
    case GEQ:
    case COMPEQ:
    case NEQ:
    case SIMPLE_LSHIFT32:
    case SIMPLE_LOGIC_RSHIFT32:
    case SIMPLE_ARITH_RSHIFT32:
    case SIMPLE_LSHIFT64:
    case SIMPLE_LOGIC_RSHIFT64:
    case SIMPLE_ARITH_RSHIFT64:
    case LSHIFT32:
    case LOGIC_RSHIFT32:
    case ARITH_RSHIFT32:
    case LSHIFT64:
    case LOGIC_RSHIFT64:
    case ARITH_RSHIFT64:
    case AND:
    case LAND:
    case OR:
    case LOR:
    case XOR:
    case FADD32:
    case FSUB32:
    case FMUL32:
    case FDIV32:
    case FADD64:
    case FSUB64:
    case FMUL64:
    case FDIV64:
      binary_op(resources, stack, fp, c);
      break;
    case JP:
      break;
    case JT:
    case SELECT:
      s0 = ld(resources, stack, fp, c.src0);
      pc = (int)(resources[s0]);
      break;
    case RETURN:
      pc = (int)(stack[fp]);
      fp = (int)(stack[fp+1]);
      break;
    case NOT:
    case LNOT:
    case MSB_FLAP:
    case CAST:
    case CONV_F2I:
    case CONV_I2F:
    case CONV_D2L:
    case CONV_L2D:
    case CONV_F2D:
    case CONV_D2F:
    case FLT32:
    case FLEQ32:
    case FGT32:
    case FGEQ32:
    case FCOMPEQ32:
    case FNEQ32:
    case FLT64:
    case FLEQ64:
    case FGT64:
    case FGEQ64:
    case FCOMPEQ64:
    case FNEQ64:
      unary_op(resources, stack, fp, c);
      break;
    case ARRAY_ACCESS:
      s0 = ld(resources, stack, fp, c.src0); // array address
      s1 = ld(resources, stack, fp, c.src1); // array index
      dptr = s0 + s1;
      if(c.src0.field == MODULE_RESOURCE){ // module array
	st(resources, stack, fp, c.dest, resources[dptr]);
      }else if(c.src0.field == LOCAL_STACK){
	st(resources, stack, fp, c.dest, stack[dptr]);
      }
      break;
    case ARRAY_INDEX:
      s0 = ld(resources, stack, fp, c.src0); // array address
      s1 = ld(resources, stack, fp, c.src1); // array index
      dptr = s0 + s1;
      st(resources, stack, fp, c.dest, dptr);
      break;
    case CALL:
    case EXT_CALL:
      pc = ld(resources, stack, fp, c.src0);
      break;
    case FIELD_ACCESS:
      break;
    case BREAK:
    case CONTINUE:
      break;
    case COND:
      break;
    case UNDEFINED:
      break;
    default:
      break;    
    }
  }

}
