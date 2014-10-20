package synthesijer.scheduler;

public enum Op {
	
	METHOD_ENTRY(true),
	METHOD_EXIT,
	ASSIGN,
	NOP,
	ADD,
	UNDEFINED;
	
	public final boolean isBranch; 
	public final int latency;
	
	private Op(boolean flag, int latency){
		isBranch = flag;
		this.latency = latency;
	}

	private Op(int latency){
		this(false, latency);
	}

	private Op(boolean flag){
		this(flag, 0);
	}

	private Op(){
		this(false, 0);
	}
	
	public static Op get(synthesijer.ast.Op o){
		switch(o){
		case PLUS: return ADD;
		default:
			System.out.println("undefiend:" + o);
			return UNDEFINED;
		}
	}

}
