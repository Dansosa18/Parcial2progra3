package umg.edu.gt.desarrollo.proyectocovidstats.adittional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class CleanerDB {
    private static final Logger logger = LogManager.getLogger(CleanerDB.class);

    public static void main(String[] args) {
        new CleanerDB().cleanDatabase();
    }

    public void cleanDatabase() {
        Properties props = loadProperties();
        if (props == null) {
            return;
        }

        String url = props.getProperty("spring.datasource.url");
        String user = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");

        // SQL scripts to execute
        String[] scripts = {
                "SET SQL_SAFE_UPDATES = 0",
                "DELETE FROM report",
                "DELETE FROM executed_reports",
                "ALTER TABLE report AUTO_INCREMENT = 1",
                "ALTER TABLE executed_reports AUTO_INCREMENT = 1",
                "SET SQL_SAFE_UPDATES = 1"
        };

        // Execute SQL scripts
        executeSqlScripts(url, user, password, scripts);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("\033[1;31mApplication.properties not found in the classpath\033[0m");
                return null;
            }
            props.load(input);
            return props;
        } catch (Exception e) {
            logger.error("\033[1;31mError reading application.properties\033[0m");
            e.printStackTrace();
            return null;
        }
    }

    private void executeSqlScripts(String url, String user, String password, String[] scripts) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("\033[1;32m✅ Connection established with the database\033[0m");

            for (String script : scripts) {
                try (Statement statement = connection.createStatement()) {
                    int rowsAffected = statement.executeUpdate(script);
                    System.out.printf("\033[1;36m✓ Executed: %s (%d rows affected)\033[0m%n",
                            shortenScript(script), rowsAffected);
                } catch (SQLException e) {
                    System.out.printf("\033[1;31m✗ Error executing: %s\033[0m%n", shortenScript(script));
                    System.out.println("\033[1;31mMessage SQL: " + e.getMessage() + "\033[0m");
                }
            }

            System.out.println("\033[1;32m✨ Database cleaned successfully\033[0m");

        } catch (SQLException e) {
            System.out.println("\033[1;31m❌ Error connecting to the database\033[0m");
            e.printStackTrace();
        }
    }

    private String shortenScript(String script) {
        // Shorten long scripts for better display
        return script.length() > 50 ? script.substring(0, 50) + "..." : script;
    }
}