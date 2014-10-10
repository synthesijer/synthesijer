package synthesijer;

/**
 * Compilation state labels for each source file.
 * 
 * @author miyo
 *
 */
public enum CompileState{
	
	/**
	 * just loaded.
	 */
	INITIALIZE(0),
	
	/**
	 * an HDL corresponding the source file has been generated. 
	 */
	GENERATE_HDL(100);
	
	/**
	 * for comparing, bigger value corresponds to later state.
	 */
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
