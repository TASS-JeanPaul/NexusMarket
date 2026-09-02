package application.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        Properties properties = new Properties();

        try (InputStream file = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (file == null) {
                throw new IllegalStateException("application.properties file not found");
            }
            properties.load(file);

        } catch (IOException e) {
            throw new IllegalStateException("Could not read the database configuration", e);
        }

        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
