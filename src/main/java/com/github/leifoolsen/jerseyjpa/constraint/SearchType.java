package com.github.leifoolsen.jerseyjpa.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Checks whether the requested search type is allowed.
 */
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Pattern(regexp = "(isbn|title|author|publisher-name)")
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface SearchType {

    String message() default "{com.github.leifoolsen.jerseyjpa.constraint.SearchType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
