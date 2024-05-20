package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnector() {
        Properties envProperties = loadEnvProperties();
        this.url = envProperties.getProperty("DATABASE_URL");
        this.user = envProperties.getProperty("DATABASE_USER");
        this.password = envProperties.getProperty("DATABASE_PASSWORD");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private Properties loadEnvProperties() {
        Properties envProperties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/env.properties")) {
            if (input == null) {
                throw new IOException("File not found");
            }
            envProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading environment properties: " + e.getMessage(), e);
        }
        return envProperties;
    }
}
