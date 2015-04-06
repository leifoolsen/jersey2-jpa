package com.github.leifoolsen.jerseyjpa.typesafe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypesafeConfigTest {
    private static final Logger logger = LoggerFactory.getLogger(TypesafeConfigTest.class);

    /*
     *  See: https://github.com/typesafehub/config
     *  See: https://marcinkubala.wordpress.com/2013/10/09/typesafe-config-hocon/
     *  See: http://blog.michaelhamrah.com/2014/02/leveraging-typesafes-config-library-across-environments/
     *  See: https://marcinkubala.wordpress.com/2013/10/09/typesafe-config-hocon/
     *  See: http://javaeeconfig.blogspot.no/2014/08/overview-of-existing-configuration.html
     *  See: http://vastdevblog.vast.com/blog/2012/06/16/creating-named-guice-bindings-for-typesafe-config-properties/
     *
     */

    @Test
    public void typeSafeConfigTake1() {

        // example of how system properties override; note this
        // must be set before the config lib is used
        System.setProperty("simple-lib.whatever", "This value comes from a system property");

        // Load our own config values from the default location,
        // application.conf
        Config conf = ConfigFactory.load();
        logger.debug("The answer is: {}", conf.getString("simple-app.answer"));

        // In this simple app, we're allowing SimpleLibContext() to
        // use the default config in application.conf ; this is exactly
        // the same as passing in ConfigFactory.load() here, so we could
        // also write "new SimpleLibContext(conf)" and it would be the same.
        // (simple-lib is a library in this same examples/ directory).
        // The point is that SimpleLibContext defaults to ConfigFactory.load()
        // but also allows us to pass in our own Config.

        SimpleLibContext context = new SimpleLibContext();
        context.printSetting("simple-lib.foo");
        context.printSetting("simple-lib.hello");
        context.printSetting("simple-lib.whatever");

    }
}
