package com.github.leifoolsen.jerseyjpa.util;


import com.google.common.base.Objects;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

public class CollectionJsonTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void minimalCollection() {
        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");
        assertThat(collectionJson.collection().version(), equalTo("1.0"));
        assertThat(collectionJson.collection().href(), equalTo("http://example.org/friends/"));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void itemRepresentation() {
        TestDTO testDTO = new TestDTO("1", "foo", "bar", "baz");

        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");

        collectionJson.collection()
                .addLink("feed", "http://example.org/friends/rss")
                .addLink("next", "http://example.org/friends?page=2");

        CollectionJson.Item item = new CollectionJson.Item("http://example.org/friends/1");
        item.addData("id", testDTO.id, "Id")
            .addData("foo", testDTO.foo, "Foo")
            .addData("bar", testDTO.bar, "Bar")
            .addData("baz", testDTO.baz, "Baz");
        item.addLink("rel-foo", "http://example.org/friends/foo", "Related foo");

        collectionJson.collection().addItem(item);


        assertThat(collectionJson.collection().links(), hasSize(2));

        assertThat(collectionJson.collection().items(), hasSize(1));

        TestDTO dto = collectionJson.collection().item(0).unMarshalData(TestDTO.class);
        assertThat(testDTO, equalTo(dto));

        assertThat(collectionJson.collection().item(0).links(), hasSize(1));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void itemsRepresentation() {
        TestDTO dto1 = new TestDTO("1", "foo", "bar", "baz");
        TestDTO dto2 = new TestDTO("2", "ole", "dole", "doff");

        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");

        collectionJson.collection()
                .addLink("feed", "http://example.org/friends/rss")
                .addLink("next", "http://example.org/friends?page=2");

        CollectionJson.Item item = new CollectionJson.Item("http://example.org/friends/1");
        item.addData("id", dto1.id, "Id")
                .addData("foo", dto1.foo, "Foo")
                .addData("bar", dto1.bar, "Bar")
                .addData("baz", dto1.baz, "Baz");
        item.addLink("rel-foo", "http://example.org/friends/foo", "Related foo");
        collectionJson.collection().addItem(item);

        item = new CollectionJson.Item("http://example.org/friends/2");
        item.addData("id", dto2.id, "Id")
                .addData("foo", dto2.foo, "Foo")
                .addData("bar", dto2.bar, "Bar")
                .addData("baz", dto2.baz, "Baz");
        collectionJson.collection().addItem(item);


        assertThat(collectionJson.collection().links(), hasSize(2));

        assertThat(collectionJson.collection().items(), hasSize(2));

        TestDTO dto = collectionJson.collection().item(0).unMarshalData(TestDTO.class);
        assertThat(dto1, equalTo(dto));

        dto = collectionJson.collection().item(1).unMarshalData(TestDTO.class);
        assertThat(dto2, equalTo(dto));

        //logger.debug(collectionJson.toString());
    }

    @Test
    public void queriesRepresentation() {
        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");
        CollectionJson.Query q = new CollectionJson.Query("search", "http://example.org/friends/search", "Search");
        q.addQueryData("search", "");
        collectionJson.collection().addQuery(q);

        assertThat(collectionJson.collection().queries(), hasSize(1));
        //logger.debug(collectionJson.toString());
    }

    @Test
    public void templateRepresentation() {
        TestDTO testDTO = new TestDTO("1", "foo", "bar", "baz");
        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");
        CollectionJson.Template template = new CollectionJson.Template();
        template.addData("id", testDTO.id)
                .addData("foo", testDTO.foo)
                .addData("bar", testDTO.bar)
                .addData("baz", testDTO.baz);
        collectionJson.collection().addTemplate(template);
        TestDTO dto = collectionJson.collection().template().unMarshalData(TestDTO.class);

        assertThat(testDTO, equalTo(dto));
        //logger.debug(collectionJson.toString());
    }

    @Test
    public void writeTemplateRepresentation() {
        TestDTO testDTO = new TestDTO("1", "foo", "bar", "baz");

        CollectionJson collectionJson = CollectionJson.newTemplate();

        collectionJson.template()
                .addData("id", testDTO.id)
                .addData("foo", testDTO.foo)
                .addData("bar", testDTO.bar)
                .addData("baz", testDTO.baz);


        TestDTO dto = collectionJson.template().unMarshalData(TestDTO.class);
        assertThat(testDTO, equalTo(dto));
        //logger.debug(collectionJson.toString());
    }

    @Test
    public void  errorRepresentation() {
        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");

        CollectionJson.Error error = new CollectionJson.Error(
                "Server Error", "X1C2", "The server have encountered an error, please wait and try again.");
        collectionJson.collection().addError(error);

        assertNotNull(collectionJson.collection().error());
        CollectionJson.Error e = collectionJson.collection().error();

        assertEquals("Server Error", e.title());
        assertEquals("X1C2", e.code());
        assertThat(e.message(), startsWith("The server have"));
        //logger.debug(collectionJson.toString());
    }

    @Test
    public void allFeatures() {
        TestDTO dto1 = new TestDTO("1", "foo", "bar", "baz");
        TestDTO dto2 = new TestDTO("2", "ole", "dole", "doff");
        TestDTO dto3 = new TestDTO("3", "Abby lane", "binke bane", "ole dole doff!");


        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");

        collectionJson.collection()
                .addLink("feed", "http://example.org/friends/rss")
                .addLink("next", "http://example.org/friends?page=2");



        collectionJson.collection()
                .addLink("feed", "http://example.org/friends/rss")
                .addLink("next", "http://example.org/friends?page=2");

        CollectionJson.Item item = new CollectionJson.Item("http://example.org/friends/1");
        item.addData("id", dto1.id, "Id")
                .addData("foo", dto1.foo, "Foo")
                .addData("bar", dto1.bar, "Bar")
                .addData("baz", dto1.baz, "Baz");
        item.addLink("rel-foo", "http://example.org/friends/foo", "Related foo");
        collectionJson.collection().addItem(item);

        item = new CollectionJson.Item("http://example.org/friends/2");
        item.addData("id", dto2.id, "Id")
                .addData("foo", dto2.foo, "Foo")
                .addData("bar", dto2.bar, "Bar")
                .addData("baz", dto2.baz, "Baz");
        collectionJson.collection().addItem(item);

        item = new CollectionJson.Item("http://example.org/friends/3");
        item.addData("id", dto3.id, "Id")
                .addData("foo", dto3.foo, "Foo")
                .addData("bar", dto3.bar, "Bar")
                .addData("baz", dto3.baz, "Baz");
        collectionJson.collection().addItem(item);
        item.addLink("rel-foo", "http://example.org/friends/foo", "Related foo");

        CollectionJson.Query q = new CollectionJson.Query("search", "http://example.org/friends/search", "Search");
        q.addQueryData("search", "");
        collectionJson.collection().addQuery(q);


        CollectionJson.Template template = new CollectionJson.Template();
        template.addData("id", dto1.id)
                .addData("foo", dto1.foo)
                .addData("bar", dto1.bar)
                .addData("baz", dto1.baz);
        collectionJson.collection().addTemplate(template);


        CollectionJson.Error error = new CollectionJson.Error(
                "Server Error", "X1C2", "The server have encountered an error, please wait and try again.");
        collectionJson.collection().addError(error);


        assertThat(collectionJson.collection().items(), hasSize(3));
        //logger.debug(collectionJson.toString());
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TestDTO {
        private String id;
        private String foo;
        private String bar;
        private String baz;

        protected TestDTO() {}

        public TestDTO(String id, String foo, String bar, String baz) {
            this.id  = id;
            this.foo = foo;
            this.bar = bar;
            this.baz = baz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestDTO testDTO = (TestDTO) o;
            return Objects.equal(id, testDTO.id) &&
                    Objects.equal(foo, testDTO.foo) &&
                    Objects.equal(bar, testDTO.bar) &&
                    Objects.equal(baz, testDTO.baz);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, foo, bar, baz);
        }

        @Override
        public String toString() {
            return "TestDTO{" +
                    "id='" + id + '\'' +
                    ", foo='" + foo + '\'' +
                    ", bar='" + bar + '\'' +
                    ", baz='" + baz + '\'' +
                    '}';
        }
    }

}
