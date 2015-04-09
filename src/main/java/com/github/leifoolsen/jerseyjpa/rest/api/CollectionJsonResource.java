package com.github.leifoolsen.jerseyjpa.rest.api;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.Link;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Query;
import net.hamnaberg.json.Template;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Singleton
@Path("cj")
@Produces("application/vnd.collection+json")
public class CollectionJsonResource {

    private UriInfo uriInfo; // actual uri info provided by parent resource (threadsafe)
    private ResourceContext resourceContext;

    public CollectionJsonResource(@Context @NotNull UriInfo uriInfo, @Context @NotNull ResourceContext resourceContext) {
        this.uriInfo = uriInfo;
        this.resourceContext = resourceContext;
    }

    @GET
    public Response get() {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().clone().path("bam");
        Response.ResponseBuilder responseBuilder;

        Template tpl = Template.create(asList(Property.template("name"), Property.template("location")));

        Collection collection = Collection.create(uriInfo.getRequestUri(), loadLinks(), loadItems(), loadQueries(), tpl, null);

        EntityTag tag = new EntityTag("/foo/bar");

        responseBuilder = Response.ok(uriBuilder.build()).tag(tag);
        return responseBuilder.build();

    }

    private List<Query> loadQueries() {
        return emptyList();
    }


    private List<Link> loadLinks() {
        return emptyList();
    }

    private List<Item> loadItems() {
        return emptyList();
    }
}
