package com.github.leifoolsen.jerseyjpa.constraint;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@NotBlank
@Pattern(regexp = "\\d{13}")
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface Isbn {

    String message() default "{com.github.leifoolsen.jerseyjpa.constraint.Isbn.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
