package synthesijer.scheduler;

public class UnsupportedException extends Exception {

	public UnsupportedException(String s){
		super(s);
	}

	public UnsupportedException(Throwable t){
		super(t);
	}

}
