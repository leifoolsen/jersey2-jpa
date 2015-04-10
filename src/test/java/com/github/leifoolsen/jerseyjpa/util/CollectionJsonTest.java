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
import static org.junit.Assert.assertThat;

public class CollectionJsonTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void oneItemWithOneLink() {
        TestDTO testDTO = new TestDTO("1", "foo", "bar", "baz");

        CollectionJson collectionJson = CollectionJson.newCollection("1.0", "http://example.org/friends/");

        CollectionJson.Link link = new CollectionJson.Link("feed", "http://example.org/friends/rss");
        collectionJson.collection().addLink(link);

        CollectionJson.Item item = new CollectionJson.Item("http://example.org/friends/1");

        item.addData("id",  testDTO.id,  "Id")
            .addData("foo", testDTO.foo, "Foo")
            .addData("bar", testDTO.bar, "Bar")
            .addData("baz", testDTO.baz, "Baz");
        item.addLink("rel-foo", "http://example.org/friends/foo", "Related foo");

        collectionJson.collection().addItem(item);


        assertThat(collectionJson.collection().links(), hasSize(1));

        assertThat(collectionJson.collection().items(), hasSize(1));

        TestDTO dto = collectionJson.collection().item(0).unMarshalData(TestDTO.class);
        assertThat(testDTO, equalTo(dto));

        assertThat(collectionJson.collection().item(0).links(), hasSize(1));

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
    }

}
