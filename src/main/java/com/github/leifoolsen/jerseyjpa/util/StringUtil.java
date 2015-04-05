package com.github.leifoolsen.jerseyjpa.util;

import com.google.common.base.MoreObjects;

public class StringUtil {
    public StringUtil() {}

    public static String blankToNull(final String value) {
        String s = MoreObjects.firstNonNull(value, "").trim();
        return s.length() > 0 ? s : null;
    }
}
