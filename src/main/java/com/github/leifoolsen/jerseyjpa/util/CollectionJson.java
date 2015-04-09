package com.github.leifoolsen.jerseyjpa.util;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionJson {
    public static final String MEDIA_TYPE_STRING = "application/vnd.collection+json";
    public static final MediaType MEDIA_TYPE = MediaType.valueOf(MEDIA_TYPE_STRING);

    private Collection collection;

    protected CollectionJson() {}
    protected CollectionJson(Collection collection) { this.collection = collection; }

    public static CollectionJson newCollection(final String version, final String href) {
        return new CollectionJson(new Collection(version, href));
    }

    public Collection collection() { return collection; }

    @Override
    public String toString() {
        return JaxbHelper.marshal(this, true);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Collection {
        private String version;
        private String href;
        private List<Link> links;
        private List<Item> items;

        protected Collection() {}
        protected Collection(String version, String href) {
            this.version = version;
            this.href = href;
        }
        public Collection addLink(Link link) {
            if(link == null) links = new ArrayList<>();
            links.add(link);
            return this;
        }
        public Collection addItem(Item item) {
            if(items == null) items = new ArrayList<>();
            items.add(item);
            return this;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Link {
        private String rel;
        private String href;

        protected Link() {}
        protected Link(String rel, String href) {
            this.rel = rel;
            this.href = href;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        private String href;
        private List<ItemData> data;
        private List<ItemLink> links;

        protected Item() {}
        public Item(String href) {
            this.href = href;
        }
        public Item addData(final ItemData d) {
            if(data == null) data = new ArrayList<>();
            data.add(d);
            return this;
        }
        public Item addData(final String name, final String value, final String prompt) {
            return addData(new ItemData(name, value, prompt));
        }
        public Item addLink(final ItemLink link) {
            if(links == null) links = new ArrayList<>();
            links.add(link);
            return this;
        }
        public Item addLink(final String rel, final String href, final String prompt) {
            return addLink(new ItemLink(rel, href, prompt));
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ItemData {
        private String name;
        private String value;
        private String prompt;

        protected ItemData() {}
        protected ItemData(String name, String value, String prompt) {
            this.name = name;
            this.value = value;
            this.prompt = prompt;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ItemLink {
        private String rel;
        private String href;
        private String prompt;

        protected ItemLink() {}
        protected ItemLink(String rel, String href, String prompt) {
            this.rel = rel;
            this.href = href;
            this.prompt = prompt;
        }
    }
}
