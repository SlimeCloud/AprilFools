package de.slimecloud.april.config.engine;

public class ValidationException extends RuntimeException {
	public ValidationException(Exception e) {
		super(e);
	}
}
