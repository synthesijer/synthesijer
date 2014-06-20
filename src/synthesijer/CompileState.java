package synthesijer;

public enum CompileState {
	
	INITIALIZE(0),
	GENERATE_HDL(1);
	
	private final int id;
	
	private CompileState(int id){
		this.id = id;
	}
	
	public boolean isAfter(CompileState s){
		return id > s.id;
	}
	
	public boolean isBefore(CompileState s){
		return id < s.id;
	}
	
}
