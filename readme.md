#Jersey-2, MOXy, JPA-2, Embedded Jetty
Maven project demonstrating how to run a JAX-RS 2 project in Embedded Jetty-9 with Servlet-3.1 annotation based configuration,
using Jersey-2 with JSON binding via MOXy, and JPA-2 persistence. Responses are returned as Collection+JSON.

## Features implemented
* @WebServlet
    * Setting base uri using @ApplicationPath
    * Register resources
* @WebListener
    * Setting up SLF4JBridgeHandler to route all JUL log records to the SLF4J API
* @WebFilter
    * Implement unit of work idom for JPA/EntityManager
* Interceptor 
    * WriterInterceptor to enable GZIP compression on server side
    * ReaderInterceptor to deflate GZIP on client side
* ResponseFilter
    * Set default response header content-type to utf-8 
* ExceptionMapper
    * Mapping all exceptions to responses and sending a uniform ErrorMessage as JSON to the client
* @Context
    * Constructor injection of UriInfo and ResourceContext
* @BeanParam
    * To inject parameters from Form POST and PUT
* Collection+JSON - Document Format
    * 200 (OK) and 201 (CREATED) responses are returned as Collection+JSON hypermedia-type
    * Not yet completed:
        * Profile Media-Type Parameter: @Produces/@Consumes("application/vnd.collection+json")
        * @POST and @PUT using Collection+JSON format  
        * Return Error using Collection+JSON format
* Embedded database
    * H2 or HSQL
* JPA provider    
    * EclipseLink or Hibernate

## Steps to run this project
* Fork, Clone or Download ZIP
* Build project: mvn clean install -U
* Start Jetty: mvn exec:java, or execute main method in com.github.leifoolsen.jerseyjpa.main.JettyStarter.java
* Application.wadl: http://localhost:8080/api/application.wadl
* Example usage: http://localhost:8080/api/books
* Import project into your favourite IDE
* Open BookResourceTest.java to start exploring code
