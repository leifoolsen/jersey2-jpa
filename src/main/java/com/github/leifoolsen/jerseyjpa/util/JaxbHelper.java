package com.github.leifoolsen.jerseyjpa.util;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.json.JsonStructureSource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class JaxbHelper {

    private JaxbHelper() {}

    public static <T> String marshal(final T entity, boolean prettyPrint) {

        if(entity == null ) {
            throw new IllegalArgumentException("Entity to marshal may not be null.");
        }
        try {
            // Create marshaller
            JAXBContext jc = JAXBContextFactory.createContext(new Class[]{entity.getClass()}, null);
            Marshaller marshaller = jc.createMarshaller();

            // Set it to true if you need to include the JSON root element in the JSON output
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);

            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);

            // Set the Marshaller media type to JSON or XML
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON); // MediaType.APPLICATION_XML

            // Marshal the object to JSON
            StringWriter sw = new StringWriter();
            marshaller.marshal(entity, sw);
            return sw.toString();
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Failed to marshal entity: " + entity.getClass().getSimpleName() +
                    ". Message: " + t.getMessage(), t);
        }
    }

    public static <T> T unMarshal(final Class<T> entityClass, final String document) {
        if(entityClass == null ) {
            throw new IllegalArgumentException("Entity class to unmarshall may not be null.");
        }
        try {
            // See: http://eclipse.dzone.com/articles/eclipselink-moxy-and-java-api
            JAXBContext jc = JAXBContextFactory.createContext(new Class[] {entityClass}, null);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON); // MediaType.APPLICATION_XML
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
            //unmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, Boolean.TRUE);
            //unmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
            //unmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR, ':');

            StringReader reader = new StringReader(document);
            JsonReader jsonReader = Json.createReader(reader);
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            JsonStructureSource objectSource = new JsonStructureSource(jsonObject);

            return (T) unmarshaller.unmarshal(objectSource, entityClass).getValue();
        }
        catch (Throwable t) {
            throw new IllegalArgumentException("Failed to unmarshall entity: " + entityClass.getSimpleName() +
                    ". Message: " + t.getMessage(), t);
        }
    }
}
