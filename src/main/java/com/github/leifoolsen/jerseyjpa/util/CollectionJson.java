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

/*
    Implemention of mediatype "collection+json". See: http://amundsen.com/media-types/collection/

    Sample collection object

    { "collection" :
      {
        "version" : "1.0",
        "href" : URI,
        "links" : [ARRAY],
        "items" : [ARRAY],
        "queries" : [ARRAY],
        "template" : {OBJECT},
        "error" : {OBJECT}
      }
    }


    A minimal Collection+JSON document. All responses MUST contain at least a valid collection object.

    { "collection" :
      {
        "version" : "1.0",

        "href" : "http://example.org/friends/"
      }
    }

    A typical Collection+JSON will contain a set of links, list of items, a queries collection, and a template object.
    { "collection" :
      {
        "version" : "1.0",
        "href" : "http://example.org/friends/",

        "links" : [
          {"rel" : "feed", "href" : "http://example.org/friends/rss"}
        ],

        "items" : [
          {
            "href" : "http://example.org/friends/jdoe",
            "data" : [
              {"name" : "full-name", "value" : "J. Doe", "prompt" : "Full Name"},
              {"name" : "email", "value" : "jdoe@example.org", "prompt" : "Email"}
            ],
            "links" : [
              {"rel" : "blog", "href" : "http://examples.org/blogs/jdoe", "prompt" : "Blog"},
              {"rel" : "avatar", "href" : "http://examples.org/images/jdoe", "prompt" : "Avatar", "render" : "image"}
            ]
          },

          {
            "href" : "http://example.org/friends/msmith",
            "data" : [
              {"name" : "full-name", "value" : "M. Smith", "prompt" : "Full Name"},
              {"name" : "email", "value" : "msmith@example.org", "prompt" : "Email"}
            ],
            "links" : [
              {"rel" : "blog", "href" : "http://examples.org/blogs/msmith", "prompt" : "Blog"},
              {"rel" : "avatar", "href" : "http://examples.org/images/msmith", "prompt" : "Avatar", "render" : "image"}
            ]
          },

          {
            "href" : "http://example.org/friends/rwilliams",
            "data" : [
              {"name" : "full-name", "value" : "R. Williams", "prompt" : "Full Name"},
              {"name" : "email", "value" : "rwilliams@example.org", "prompt" : "Email"}
            ],
            "links" : [
              {"rel" : "blog", "href" : "http://examples.org/blogs/rwilliams", "prompt" : "Blog"},
              {"rel" : "avatar", "href" : "http://examples.org/images/rwilliams", "prompt" : "Avatar", "render" : "image"}
            ]
          }
        ],

        "queries" : [
          {"rel" : "search", "href" : "http://example.org/friends/search", "prompt" : "Search",
            "data" : [
              {"name" : "search", "value" : ""}
            ]
          }
        ],

        "template" : {
          "data" : [
            {"name" : "full-name", "value" : "", "prompt" : "Full Name"},
            {"name" : "email", "value" : "", "prompt" : "Email"},
            {"name" : "blog", "value" : "", "prompt" : "Blog"},
            {"name" : "avatar", "value" : "", "prompt" : "Avatar"}

          ]
        }
      }
    }
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionJson {
    public static final String MEDIA_TYPE_COLLECTION_JSON = "application/vnd.collection+json";
    public static final MediaType MEDIA_TYPE = MediaType.valueOf(MEDIA_TYPE_COLLECTION_JSON);

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
        private String      version;
        private String      href;
        private List<Link>  links   = new ArrayList<>();
        private List<Item>  items   = new ArrayList<>();
        private List<Query> queries = new ArrayList<>();
        private Template    template;
        private Error       error;

        protected Collection() {}
        protected Collection(String version, String href) {
            this.version = version;
            this.href = href;
        }
        public Collection addLink(String rel, String href) {
            return addLink(new Link(rel, href));
        }
        public Collection addLink(Link link) {
            links.add(link);
            return this;
        }
        public Collection addItem(Item item) {
            items.add(item);
            return this;
        }
        public Collection addQuery(Query q) {
            queries.add(q);
            return this;
        }
        public Collection addQueries(List<Query> qs) {
            if(qs != null) {
                for(Query q : qs) {
                    queries.add(q);
                }
            }
            return this;
        }
        public Collection addTemplate(Template template) {
            this.template = template;
            return this;
        }
        public Collection addError(Error error) {
            this.error = error;
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
        public List<Link> links(String rel) {
            List<Link> result = new ArrayList<>();
            for (Link link : links) {
                if(link.rel.equals(rel)) {
                    result.add(link);
                }
            }
            return result;
        }
        public List<Item> items() {
            return items;
        }
        public List<Query> queries() {
            return queries;
        }
        public Template template() {
            return template;
        }
        public Error error() {
            return error;
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

    /*
        Item Representation:
        An item response will usually look like a collection representation, but contain only one item.
        The server MAY not return the queries or template properties within a response, but include annotated
        links instead.

        { "collection" :
          {
            "version" : "1.0",
            "href" : "http://example.org/friends/",

            "links" : [
              {"rel" : "feed", "href" : "http://example.org/friends/rss"},
              {"rel" : "queries", "href" : "http://example.org/friends/?queries"},
              {"rel" : "template", "href" : "http://example.org/friends/?template"}
            ],

            "items" : [
              {
                "href" : "http://example.org/friends/jdoe",
                "data" : [
                  {"name" : "full-name", "value" : "J. Doe", "prompt" : "Full Name"},
                  {"name" : "email", "value" : "jdoe@example.org", "prompt" : "Email"}
                ],
                "links" : [
                  {"rel" : "blog", "href" : "http://examples.org/blogs/jdoe", "prompt" : "Blog"},
                  {"rel" : "avatar", "href" : "http://examples.org/images/jdoe", "prompt" : "Avatar", "render" : "image"}
                ]
              }
            ]
          }
        }
     */

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


    /*
        The links array is an OPTIONAL child property of the items array. It SHOULD contain one or more anonymous
        objects. Each has five possible properties: href (REQUIRED), rel (REQURIED), name (OPTIONAL), render (OPTIONAL),
         and prompt, (OPTIONAL).

        Sample links array:

        {
          "collection" :
          {
            "version" : "1.0",
            "href" : URI,
            "items" :
            [
              {
                "href" : URI,
                "data" : [ARRAY],
                "links" :
                [
                  {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING, "render" : "image"},
                  {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING, "render" : "link"},
                  ...
                  {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING}
                ]
              }
            ]
          }
        }
    */

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


    /*
        The template object contains all the input elements used to add or edit collection "records." This is an
        OPTIONAL object and there MUST NOT be more than one template object in a Collection+JSON document.
        It is a top-level document property.

        The template object SHOULD have a data array child property.
        {
          "template" :
          {
            "data" : [ARRAY]
          }
        }


        Template Representation:
        To reduce the size of the response representation, servers MAY return a link to the template object for a
        collection. Clients can then request the template representation directly.

        { "collection" :
          {
            "version" : "1.0",
            "href" : "http://example.org/friends/",

            "template" : {
              "data" : [
                {"name" : "full-name", "value" : "", "prompt" : "Full Name"},
                {"name" : "email", "value" : "", "prompt" : "Email"},
                {"name" : "blog", "value" : "", "prompt" : "Blog"},
                {"name" : "avatar", "value" : "", "prompt" : "Avatar"}
              ]
            }
          }
        }

        Template Write Representation:
        When sending data to the server, clients should fill in the template object and use that as the body
        of an HTTP POST (for "create") or HTTP PUT ("update") request.

        { "template" : {
            "data" : [
              {"name" : "full-name", "value" : "W. Chandry"},
              {"name" : "email", "value" : "wchandry@example.org"},
              {"name" : "blog", "value" : "http://example.org/blogs/wchandry"},
              {"name" : "avatar", "value" : "http://example.org/images/wchandry"}
            ]
          }
        }
     */

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Template {
        private List<TemplateData> data;

        public Template() {}
        public Template addData(String name, String value, String prompt) {
            if(data == null) data = new ArrayList<>();
            data.add(new TemplateData(name, value, prompt));
            return this;
        }

        List<TemplateData> data() {
            return data;
        }

        TemplateData data(int index) {
            return data.get(index);
        }

        public <T> T unMarshalData(final Class<T> entityClass) {
            JsonBuilderFactory factory = Json.createBuilderFactory(null);
            JsonObjectBuilder builder = factory.createObjectBuilder();

            for (TemplateData d : data) {
                builder.add(d.name, d.value);
            }
            JsonObject jsonObject = builder.build();
            return JaxbHelper.unMarshal(entityClass, jsonObject);
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TemplateData {
        private String name;
        private String value;
        private String prompt;

        protected TemplateData() {}
        protected TemplateData(String name, String value, String prompt) {
            this.name = name;
            this.value = value;
            this.prompt = prompt;
        }
    }

    /*
        The queries array is an OPTIONAL top-level property of the Collection+JSON document.

        The queries array SHOULD contain one or more anonymous objects. Each object composed of five possible
        properties: href (REQUIRED), rel (REQUIRED), name (OPTIONAL), prompt (OPTIONAL), and a data array (OPTIONAL).

        If present, the data array represents query parameters for the associated href property of the same object.
        See Query Templates for details.

        {
          "queries" :
          [
            {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING},
            {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING,
              "data" :
              [
                {"name" : STRING, "value" : VALUE}
              ]
            },
            ...
            {"href" : URI, "rel" : STRING, "prompt" : STRING, "name" : STRING}
          ]
        }


        Queries Representation:
        To reduce the size of the response representation, servers MAY return a link to the queries array for a
        collection. Clients can then request the queries representation directly.

        { "collection" :
          {
            "version" : "1.0",
            "href" : "http://example.org/friends/",

            "queries" : [
              {"rel" : "search", "href" : "http://example.org/friends/search", "prompt" : "Search"
                "data" : [
                  {"name" : "search", "value" : ""}
                ]
              }
            ]
          }
        }
     */

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Query {
        private String rel;
        private String href;
        private String prompt;
        private List<QueryData> data;

        protected Query() {}
        public Query(String rel, String href, String prompt) {
            this.rel = rel;
            this.href = href;
            this.prompt = prompt;
        }
        public Query addQueryData(String name, String value) {
            if(data == null) data = new ArrayList<>();
            data.add( new QueryData(name, value));
            return this;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class QueryData {
        private String name;
        private String value;

        protected QueryData() {}
        protected QueryData(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }


    /*
        The error object contains addiitional information on the latest error condition reported by the server.
        This is an OPTIONAL object and there MUST NOT be more than one error object in a Collection+JSON document.
        It is a top-level document property.

        The following elements MAY appear as child properties of the error object: code message and title.

        {
          "error" :
          {
            "title" : STRING,
            "code" : STRING,
            "message" : STRING
          }
        }


        Error Representation:
        When the server encounters an error, it MAY return an error object.

        { "collection" :
          {
            "version" : "1.0",
            "href" : "http://example.org/friends/",

            "error" : {
              "title" : "Server Error",
              "code" : "X1C2",
              "message" : "The server have encountered an error, please wait and try again."
            }
          }
        }
     */

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Error {
        private String title;
        private String code;
        private String message;

        protected Error() {}
        public Error(String title, String code, String message) {
            this.title = title;
            this.code = code;
            this.message = message;
        }
        public String title() {
            return title;
        }
        public String code() {
            return code;
        }
        public String message() {
            return message;
        }

        public <T> T unMarshalError(final Class<T> entityClass) {
            JsonBuilderFactory factory = Json.createBuilderFactory(null);
            JsonObjectBuilder builder = factory.createObjectBuilder()
                    .add("title", title)
                    .add("code", code)
                    .add("message", message);
            JsonObject jsonObject = builder.build();
            return JaxbHelper.unMarshal(entityClass, jsonObject);
        }
    }
}
