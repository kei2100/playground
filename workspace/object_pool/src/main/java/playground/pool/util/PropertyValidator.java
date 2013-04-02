package playground.pool.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


public enum PropertyValidator {

	INSTANCE;
	
	private Validator validator;
	
	private PropertyValidator() {
		try {
			validator = Validation.buildDefaultValidatorFactory().getValidator();
		} catch (Exception e) {
			// TODO Logger
			e.printStackTrace();
		}
	}
	
	public <T> void validate(T object) throws ValidationException {
		if (validator == null) {
			// TODO warning Logger
			return;
		}
		
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		
		if (violations.isEmpty()) return;
		
		StringBuilder messages = new StringBuilder();
		for (ConstraintViolation<T> violation : violations) {			
			messages.append(violation.getPropertyPath().toString())
			        .append(": ")
			        .append(violation.getMessage())
			        .append(".");
		}
		throw new ValidationException(messages.toString());
	}
}
