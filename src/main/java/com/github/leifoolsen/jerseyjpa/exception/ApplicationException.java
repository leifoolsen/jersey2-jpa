package com.github.leifoolsen.jerseyjpa.exception;

/**
 * Application spesific runtime exception
 */
public class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 6748227846278696640L;

    private int responseStatusCode;
    private Integer errorCode;
    private String messageTemplate;

    /**
     * @param responseStatusCode the HTTP Status errorCode that should be returned by the server. <br />
     *        See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
     *        for list of responseStatusCode codes. Additional responseStatusCode codes can be added by applications
     *        by creating an implementation of {@link javax.ws.rs.core.Response.StatusType}.
     * @param errorCode an application specific error errorCode.
     * @param message the interpolated error message.
     * @param messageTemplate the non-interpolated error message.
     */
    public ApplicationException(int responseStatusCode, Integer errorCode, String message, String messageTemplate) {
        this(responseStatusCode, errorCode, message, messageTemplate, (Throwable) null);
    }

    /**
     * @param responseStatusCode the HTTP Status errorCode that should be returned by the server. <br />
     *        See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
     *        for list of responseStatusCode codes. Additional responseStatusCode codes can be added by applications
     *        by creating an implementation of {@link javax.ws.rs.core.Response.StatusType}.
     * @param errorCode an application specific error errorCode.
     * @param message the interpolated error message.
     * @param messageTemplate the non-interpolated error message.
     * @param cause  the underlying cause of the exception.
     */
    public ApplicationException(
            int responseStatusCode, Integer errorCode, String message, String messageTemplate, Throwable cause) {

        super(message, cause);
        this.responseStatusCode = responseStatusCode;
        this.errorCode = errorCode;
        this.messageTemplate = messageTemplate;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }
}
