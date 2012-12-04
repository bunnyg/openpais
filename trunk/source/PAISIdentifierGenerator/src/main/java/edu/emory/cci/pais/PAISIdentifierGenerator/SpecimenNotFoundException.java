package edu.emory.cci.pais.PAISIdentifierGenerator;

public class SpecimenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SpecimenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpecimenNotFoundException(String message) {
		super(message);
	}

	
}
