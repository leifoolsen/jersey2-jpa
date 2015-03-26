package com.github.leifoolsen.jerseyjpa.rest.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyErrorMessage {

    private String message;
    private String messageTemplate;
    private String path;
    private String invalidValue;

    /**
     * Create a {@code PropertyErrorMessage} instance. Constructor for JAXB providers.
     */
    protected PropertyErrorMessage() {}

    /**
     * Create a {@code PropertyErrorMessage} instance.
     *
     * @param message interpolated error message.
     * @param messageTemplate non-interpolated error message.
     * @param path property path.
     * @param invalidValue value that failed to pass constraints.
     */
    public PropertyErrorMessage(String message, String messageTemplate, String path, String invalidValue) {
        this.message = message;
        this.messageTemplate = messageTemplate;
        this.path = path;
        this.invalidValue = invalidValue;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String getPath() {
        return path;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
