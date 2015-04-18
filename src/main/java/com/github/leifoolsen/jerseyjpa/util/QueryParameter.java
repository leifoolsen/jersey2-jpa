package com.github.leifoolsen.jerseyjpa.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class QueryParameter {
    private Map<String, Object> queryParameters = Maps.newHashMap();

    private QueryParameter(String name, Object value) {
        queryParameters.put(name, value);
    }

    public static QueryParameter with( String name, Object value) {
        return new QueryParameter(name, value);
    }

    public QueryParameter and(String name, Object value) {
        this.queryParameters.put(name, value);
        return this;
    }
    public Map<String, Object> parameters() {
        return queryParameters;
    }
}
