package com.github.leifoolsen.jerseyjpa.typesafe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypesafeConfigTest {
    private static final Logger logger = LoggerFactory.getLogger(TypesafeConfigTest.class);

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
