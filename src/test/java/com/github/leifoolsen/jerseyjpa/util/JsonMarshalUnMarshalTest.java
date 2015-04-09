package com.github.leifoolsen.jerseyjpa.util;

import com.github.leifoolsen.jerseyjpa.domain.Book;
import com.github.leifoolsen.jerseyjpa.domain.Publisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class JsonMarshalUnMarshalTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonMarshalUnMarshalTest.class);

    @Test
    public void moxyMarshal() {

        Book book = createBook();

        String json = JaxbHelper.marshal(book, true);
        assertThat(json, not(isEmptyOrNullString()));

        JsonReader r = Json.createReader(new StringReader(json));
        JsonObject bookAsJsonObject = r.readObject();
        r.close();

        String id = bookAsJsonObject.getString("id");
        assertThat(book.getId(), equalTo(id));

        String isbn = bookAsJsonObject.getString("isbn");
        assertThat(book.getISBN(), equalTo(isbn));
    }

    @Test
    public void moxyUnMarshal() {

        JsonObject bookAsJsonObject = createBookAsJsonObject();
        Book book = JaxbHelper.unMarshal(Book.class, bookAsJsonObject.toString());

        assertThat(book, not(is(nullValue())));
        assertThat(book.getISBN(), not(isEmptyOrNullString()));

        String isbn = bookAsJsonObject.getString("isbn");
        assertThat(isbn, equalTo(book.getISBN()));

        assertThat(new GregorianCalendar(2013, 0, 1).getTime(), equalTo(book.getPublished()));
    }

    @Test
    public void gsonMarshal() {

        Book book = createBook();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create();

        final String json = gson.toJson(book);
        assertThat(json, not(isEmptyOrNullString()));

        JsonReader r = Json.createReader(new StringReader(json));
        JsonObject bookAsJsonObject = r.readObject();
        r.close();

        String id = bookAsJsonObject.getString("id");
        assertThat(book.getId(), equalTo(id));

        String isbn = bookAsJsonObject.getString("isbn");
        assertThat(book.getISBN(), equalTo(isbn));
    }

    @Test
    public void gsonUnmarshal() {

        JsonObject bookAsJsonObject = createBookAsJsonObject();


        // See: https://sites.google.com/site/gson/gson-type-adapters-for-common-classes
        // See: https://sites.google.com/site/gson/gson-type-adapters-for-common-classes-1
        // See: http://www.javacreed.com/gson-typeadapter-example/
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                if(json != null) {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                        return df.parse(json.getAsString());
                    } catch (ParseException e) {
                        try {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            return df.parse(json.getAsString());
                        } catch (ParseException e2) {
                            throw new IllegalArgumentException(e2);
                        }
                    }
                }
                else {
                    return null;
                }
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser)
                .create();

        Book book = gson.fromJson(bookAsJsonObject.toString(), Book.class);

        assertThat(book, not(is(nullValue())));
        assertThat(book.getISBN(), not(isEmptyOrNullString()));

        String isbn = bookAsJsonObject.getString("isbn");
        assertThat(isbn, equalTo(book.getISBN()));

        assertThat(new GregorianCalendar(2013, 0, 1).getTime(), equalTo(book.getPublished()));
    }

    private static Book createBook() {

        Publisher publisher = new Publisher("12345", "The publisher");
        return Book.with("9780752495620")
                .title("Guide to Middle Earth: Tolkien and The Lord of the Rings")
                .author("Duriez, Colin")
                .publisher(publisher)
                .published(new GregorianCalendar(2013, 0, 1).getTime())
                .summary("An illuminating guide to Middle-earth and the man who created it.")
                .build();
    }

    private static JsonObject createBookAsJsonObject() {

        JsonReader reader = Json.createReader(
                new StringReader(("{'id':'a38a61e8-38d1-48fa-aa87-356437655647', " +
                        "'version':0, 'code':'12345', 'name':'The Publisher'}").replace('\'', '"')));

        JsonObject publisherAsJsonObject = reader.readObject();
        reader.close();

        // See: http://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        return factory.createObjectBuilder()
                .add("id", "8343e028-1380-4822-8a29-55a2308f2b29")
                .add("version", 0L)
                .add("isbn", "9780752495620")
                .add("title", "Guide to Middle Earth: Tolkien and The Lord of the Rings")
                .add("published", "2013-01-01")
                .add("author", "Duriez, Colin")
                .add("publisher", publisherAsJsonObject)
                .add("summary", "An illuminating guide to Middle-earth and the man who created it.")
                .build();

        //logger.debug("{}", bookAsJsonObject.toString());

        //return bookAsJsonObject;
    }
}
