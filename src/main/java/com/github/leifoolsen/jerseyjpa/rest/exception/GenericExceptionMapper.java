package com.github.leifoolsen.jerseyjpa.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericExceptionMapper implements ExceptionMapper {

    @Override
    public Response toResponse(Throwable exception) {
        return null;
    }
}
