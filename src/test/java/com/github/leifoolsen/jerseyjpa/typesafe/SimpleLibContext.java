package com.github.leifoolsen.jerseyjpa.typesafe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLibContext {
    private static final Logger logger = LoggerFactory.getLogger(SimpleLibContext.class);

    private Config config;

    // we have a constructor allowing the app to provide a custom Config
    public SimpleLibContext(Config config) {
        this.config = config;
        // This verifies that the Config is sane and has our
        // reference config. Importantly, we specify the "simple-lib"
        // path so we only validate settings that belong to this
        // library. Otherwise, we might throw mistaken errors about
        // settings we know nothing about.
        config.checkValid(ConfigFactory.defaultReference(), "simple-lib");
    }

    // This uses the standard default Config, if none is provided,
    // which simplifies apps willing to use the defaults
    public SimpleLibContext() {
        this(ConfigFactory.load());
    }

    // this is the amazing functionality provided by simple-lib
    public void printSetting(String path) {
        logger.debug("The setting '{} is: {}", path, config.getString(path));
    }
}
