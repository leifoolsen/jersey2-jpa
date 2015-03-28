package com.github.leifoolsen.jerseyjpa.rest.exception;

import com.github.leifoolsen.jerseyjpa.util.JaxbHelper;
import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessage {

    private String id = UUID.randomUUID().toString();
    private int status;
    private Integer code;
    private String message;
    private String messageTemplate;
    private String location;

    @XmlTransient
    private String stackTrace;

    private List<ConstraintViolationMessage> constraintViolationMessages;

    protected ErrorMessage() {}

    private ErrorMessage(Builder builder) {
        status = builder.responseStatus;
        code = builder.code;
        message = builder.message;
        messageTemplate = builder.messageTemplate;
        location = builder.location;
        stackTrace = builder.stackTrace;
        constraintViolationMessages = builder.constraintViolationMessages;
    }

    public static Builder with(Throwable t, UriInfo uriInfo) {
        return new Builder(t, uriInfo);
    }

    public String getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String getLocation() {
        return location;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public List<ConstraintViolationMessage> getConstraintViolationMessages() {
        return constraintViolationMessages;
    }

    public String toJSON() {
        return JaxbHelper.marshall(this, false);
    }

    @Override
    public String toString() {

        try {
            return JaxbHelper.marshall(this, true);
        }
        catch (Exception e) {
            return "Marshalling failed with message: " + e.getMessage() +
                    "Fallback {" +
                    "id='" + id + '\'' +
                    ", status=" + status +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class Builder {
        private int responseStatus;
        private Integer code;
        private String message;
        private String messageTemplate;
        private String location;
        private String stackTrace;
        private List<ConstraintViolationMessage> constraintViolationMessages;

        private Builder(Throwable t, UriInfo uriInfo) {

            message = t.getMessage();
            stackTrace = Throwables.getStackTraceAsString(t);
            location = uriInfo.getAbsolutePath().toString();

            responseStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

            if(t instanceof ConstraintViolationException) {
                responseStatus = Response.Status.BAD_REQUEST.getStatusCode();
            }
            else if(t instanceof WebApplicationException) {
                responseStatus = ((WebApplicationException) t).getResponse().getStatus();
            }


            if(t instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException)t;

                if(MoreObjects.firstNonNull(message, "").trim().length() < 1) {
                    message = "Bean Validation constraint(s) violated.";
                }

                constraintViolationMessages = Lists.newArrayList();
                for (ConstraintViolation<?> constraintViolation : cve.getConstraintViolations()) {
                    constraintViolationMessages.add(
                            new ConstraintViolationMessage(
                                    constraintViolation.getMessage(),
                                    constraintViolation.getMessageTemplate(),
                                    constraintViolation.getPropertyPath(),
                                    constraintViolation.getInvalidValue()));
                }
            }
        }

        public ErrorMessage build() {
            return new ErrorMessage(this);
        }
    }
}




/*
 * Create a {@code ErrorMessage} instance.
 *
 * @param status  HTTP Status code returned by the server. <br />
 *                See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
 *                for list of status codes. Additional status codes can be added by applications
 *                by creating an implementation of {@link javax.ws.rs.core.Response.StatusType}.
 * @param code application specific error code
 * @param message interpolated error message.
 * @param link point to page where the error is documented.
 * @param messageTemplate non-interpolated error message.
 * @param path instance path.
 *
 */

/*
            try {
                // If the message itself is an ErrorMessage we'll use it to build a new message
                ErrorMessage errorMessage = JaxbHelper.unMarshall(ErrorMessage.class, message);
                responseStatus = errorMessage.status;
                code = errorMessage.code;
                message = errorMessage.message;
                messageTemplate = errorMessage.messageTemplate;
                location = errorMessage.location;
                constraintViolationMessages = errorMessage.constraintViolationMessages;
            }

 */
