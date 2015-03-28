#Jersey-2, MOXy, JPA-2, Embedded Jetty
Maven project demonstrating how to run a JAX-RS 2 project in Embedded Jetty-9 with Servlet-3.1 annotation based configuration,
using Jersey-2 with JSON binding via MOXy, and JPA-2 persistence.

## Steps to run this project
* Fork, Clone or Download ZIP
* Build project: mvn clean install -U
* Start Jetty: mvn exec:java, or execute main method in com.github.leifoolsen.jerseyjpa.main.JettyStarter.java
* Application.wadl: http://localhost:8080/api/application.wadl
* Example usage: http://localhost:8080/api/books
* Import project into your favourite IDE
* Open BookResourceTest.java to start exploring code
