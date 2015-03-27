package com.github.leifoolsen.jerseyjpa.util;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class JAXBHelper {

    private JAXBHelper() {}

    public static <T> String marshall(final T entity, boolean JAXBFormattedOutput) {

        if(entity == null ) {
            throw new IllegalArgumentException("Entity to marshall may not be null.");
        }
        try {
            // Create marshaller
            JAXBContext jc = JAXBContextFactory.createContext(new Class[]{entity.getClass()}, null);
            Marshaller marshaller = jc.createMarshaller();

            // Set it to true if you need to include the JSON root element in the JSON output
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, JAXBFormattedOutput);

            // Set the Marshaller media type to JSON or XML
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON); // MediaType.APPLICATION_XML

            // Marshal the object to JSON
            StringWriter sw = new StringWriter();
            marshaller.marshal(entity, sw);
            return sw.toString();
        }
        catch (JAXBException e) {
            throw new IllegalArgumentException("Failed to marshall entity: " + entity.getClass().getSimpleName() +
                    ". Errorcode: " + e.getErrorCode() + ". Message: " + e.getMessage(), e);
        }
    }

    public static <T> T unMarshall(final Class<T> entityClass, final String document) {
        if(entityClass == null ) {
            throw new IllegalArgumentException("Entity class to unmarshall may not be null.");
        }
        try {
            JAXBContext jc = JAXBContextFactory.createContext(new Class[] {entityClass}, null);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON); // MediaType.APPLICATION_XML
            return (T) unmarshaller.unmarshal(new StringReader(document));
        }
        catch (JAXBException e) {
            throw new IllegalArgumentException("Failed to unmarshall entity: " + entityClass.getSimpleName() +
                    ". Errorcode: " + e.getErrorCode() + ". Message: " + e.getMessage(), e);
        }
    }
}
