package utilities;

import java.io.InputStream;
import java.util.Properties;

public class constants {

    private static final Properties properties
            = new Properties();

    static {

        try {

            InputStream inputStream
                    = constants.class
                            .getClassLoader()
                            .getResourceAsStream(
                                    "config.properties");

            if (inputStream == null) {

                throw new RuntimeException(
                        "config.properties not found");
            }

            properties.load(inputStream);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load config.properties", e);
        }
    }

    public static final String BASE_URL
            = properties.getProperty("base.url");

    public static final String EMAIL
            = properties.getProperty("email");

    public static final String PASSWORD
            = properties.getProperty("password");

    public static final String RESUME_PATH
            = properties.getProperty("resume.path");

    public static final String CHROME_PROFILE_PATH
            = properties.getProperty("chrome.profile.path");
}
