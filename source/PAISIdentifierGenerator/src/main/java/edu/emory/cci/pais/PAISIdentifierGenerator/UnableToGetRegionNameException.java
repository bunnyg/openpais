package edu.emory.cci.pais.PAISIdentifierGenerator;

public class UnableToGetRegionNameException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UnableToGetRegionNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToGetRegionNameException(String message) {
		super(message);
	}
}
