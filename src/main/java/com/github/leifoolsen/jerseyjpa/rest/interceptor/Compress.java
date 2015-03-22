package com.github.leifoolsen.jerseyjpa.rest.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;


/**
 * @Compress annotation is the name binding annotation
 * See: https://jersey.java.net/documentation/latest/user-guide.html#filters-and-interceptors
 * See: http://www.codingpedia.org/ama/how-to-compress-responses-in-java-rest-api-with-gzip-and-jersey/
 */

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Compress {}