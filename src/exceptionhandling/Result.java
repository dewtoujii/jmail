package exceptionhandling;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Sec;

public class Result<T> {
	@Sec("Owner")
	private T object;
	@Sec("Owner")
	private boolean isSuccess;
	@Sec("Owner")
	private Exception e;
	
	@Constraints({ "@0 <= Owner", "@1 <= Owner", "@2 <= Owner" })
	public Result(boolean success, T object, Exception e) {
		this.isSuccess = success;
		this.object = object;
		this.e = e;
	}
	
	@Constraints({ "@ret <= Owner" })
	public T getObject() {
		return object;
	}
	
	@Constraints({ "@ret <= Owner" })
	public boolean isSuccess() {
		return isSuccess;
	}
	
	@Constraints({ "@ret <= Owner" })
	public Exception getException() {
		return e;
	}
}
