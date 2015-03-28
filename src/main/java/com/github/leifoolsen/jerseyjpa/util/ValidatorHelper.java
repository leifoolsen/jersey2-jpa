package com.github.leifoolsen.jerseyjpa.util;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

public class ValidatorHelper {

    private ValidatorHelper() {}

    public static <T> void validate(final T entity) {
        if(entity == null) {
            throw new ConstraintViolationException("Entity may not be null.", new HashSet<ConstraintViolation<?>>());
        }
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if(!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException("Bean Validation constraint(s) violated.",
                    new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
    }
}
