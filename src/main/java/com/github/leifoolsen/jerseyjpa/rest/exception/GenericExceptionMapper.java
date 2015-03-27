package com.github.leifoolsen.jerseyjpa.rest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
//@Priority(Priorities.USER)
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private UriInfo uriInfo; // actual uri info provided by parent resource (threadsafe)

    public GenericExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(Throwable t) {
        ErrorMessage errorMessage = ErrorMessage.with(t).build();

        logger.debug(errorMessage.toString());

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .location(uriInfo.getAbsolutePath())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}


