package com.github.leifoolsen.jerseyjpa.rest.exception;

import com.google.common.collect.Lists;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessage {
    private String id = UUID.randomUUID().toString();
    private int status;
    private Integer code;
    private String message;
    private String link;
    private String messageTemplate;
    private String path;
    private List<PropertyErrorMessage> propertyErrorMessages;

    /**
     * Create a {@code ErrorMessage} instance. Constructor for JAXB providers.
     */
    protected ErrorMessage() {}


    /**
     *
     */
    /**
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
     */
    public ErrorMessage(int status, int code, String message, String link, String messageTemplate, String path, List<PropertyErrorMessage> propertyErrorMessages) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.link = link;
        this.messageTemplate = messageTemplate;
        this.path = path;
        this.propertyErrorMessages = propertyErrorMessages;
    }


    public void addPropertyErrorMessage(PropertyErrorMessage propertyErrorMessage) {
        if(propertyErrorMessage != null) {
            if (propertyErrorMessages == null) {
                propertyErrorMessages = Lists.newArrayList();
            }
            propertyErrorMessages.add(propertyErrorMessage);
        }
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

    public String getLink() {
        return link;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String getPath() {
        return path;
    }
}
