package com.github.leifoolsen.jerseyjpa.util;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionJson {
    public static final String MEDIA_TYPE_STRING = "application/vnd.collection+json";
    public static final MediaType MEDIA_TYPE = MediaType.valueOf(MEDIA_TYPE_STRING);

    private Collection collection;
    private Template template;

    protected CollectionJson() {}
    protected CollectionJson(Collection collection) { this.collection = collection; }
    protected CollectionJson(Template template) { this.template = template; }

    public static CollectionJson newCollection(final String version, final String href) {
        return new CollectionJson(new Collection(version, href));
    }

    public static CollectionJson newTemplate() {
        return new CollectionJson(new Template());
    }

    public Collection collection() { return collection; }

    public Template template() { return template; }

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
            if(links == null) links = new ArrayList<>();
            links.add(link);
            return this;
        }
        public Collection addItem(Item item) {
            if(items == null) items = new ArrayList<>();
            items.add(item);
            return this;
        }
        public String version() {
            return version;
        }
        public String href() {
            return href;
        }
        public List<Link> links() {
            return links;
        }
        public List<Item> items() {
            return items;
        }
        public Item item(int index) {
            return items.get(index);
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
        public String rel() {
            return rel;
        }
        public String href() {
            return href;
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
        public String href() {
            return href;
        }
        public List<ItemData> data() {
            return data;
        }
        public ItemData data(String name) {
            for (ItemData d : data) {
                if(d.name.equals(name)) {
                    return d;
                }
            }
            return null;
        }
        public Map<String, String> nameValueItems() {
            Map<String, String> result = new HashMap<>();
            for (ItemData d : data) {
                result.put(d.name, d.value);
            }
            return result;
        }
        public <T> T unMarshalData(final Class<T> entityClass) {
            JsonBuilderFactory factory = Json.createBuilderFactory(null);
            JsonObjectBuilder builder = factory.createObjectBuilder();

            for (ItemData d : data) {
                builder.add(d.name, d.value);
            }
            JsonObject jsonObject = builder.build();
            return JaxbHelper.unMarshal(entityClass, jsonObject);
        }
        public Map<String, String> prompts() {
            Map<String, String> result = new HashMap<>();

            for (ItemData d : data) {
                result.put(d.name, d.prompt);
            }
            return result;
        }
        public List<ItemLink> links() {
            return links;
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
        public String name() {
            return name;
        }
        public String value() {
            return value;
        }
        public String prompt() {
            return prompt;
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
        public String rel() {
            return rel;
        }
        public String href() {
            return href;
        }
        public String prompt() {
            return prompt;
        }
    }


    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Template {
        private List<TemplateData> data;

        protected Template() {}
        public Template addData(TemplateData templateData) {
            if(data == null) data = new ArrayList<>();
            data.add(templateData);
            return this;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TemplateData {
        List<TemplateDataItem> items;

        protected TemplateData() {}
        protected TemplateData addItem(String name, String value) {
            if(items == null) items = new ArrayList<>();
            items.add(new TemplateDataItem(name, value));
            return this;
        }

        Map<String, String> nameValueItems() {
            Map<String, String> result = new HashMap<>();
            for (TemplateDataItem item : items) {
                result.put(item.name, item.value);
            }
            return result;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TemplateDataItem {
        private String name;
        private String value;

        protected TemplateDataItem() {}
        protected TemplateDataItem(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }
}
