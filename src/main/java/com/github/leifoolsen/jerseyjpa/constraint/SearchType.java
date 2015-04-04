package com.github.leifoolsen.jerseyjpa.constraint;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Checks whether the requested search type is allowed.
 */
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Pattern(regexp = "(any|isbn|title|author|publisher.code|publisher.name)")   // Not possible to get regexp from enum. Must rewrite from annotation to code
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface SearchType {

    String message() default "{com.github.leifoolsen.jerseyjpa.constraint.SearchType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    enum Type {
        ANY ("any"),  // any of ISBN, TITLE, AUTHOR ....
        ISBN ("isbn"),
        TITLE("title"),
        AUTHOR("author"),
        SUMMARY("summary"),
        PUBLISHER_CODE("publisher.code"),
        PUBLISHER_NAME("publisher.name");

        private final String type;

        private static final Map<String, Type> LOOKUP = new HashMap<>();
        public  static final String REGEXP;
        static {
            for (Type t : Type.values()) {
                LOOKUP.put(t.type, t);
            }
            REGEXP = "(" + Joiner.on("|").join(LOOKUP.keySet()) + ")";
        }

        Type(String type) { this. type = type; }

        public String type() { return type; }

        public static Type get(final String type) {
            Preconditions.checkNotNull(type, "Type to get for may not be null");
            return Preconditions.checkNotNull(LOOKUP.get(type), "Search type %s not found", type);
        }
    }
}
