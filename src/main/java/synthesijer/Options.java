package synthesijer;

public enum Options {

    INSTANCE;

    public boolean optimizing;

    public boolean chaining;

    public boolean bb2;

    public boolean bb;

    public boolean operation_strength_reduction;

    public boolean legacy_instance_variable_name;

    public boolean iroha;

    public boolean opencl = false;

    public boolean with_ssa = false;

	public boolean verbose = false;

	public boolean debug = false;
	
	public int develLevel = 0;

}
