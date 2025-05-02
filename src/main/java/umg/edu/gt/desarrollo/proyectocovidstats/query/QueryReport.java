package umg.edu.gt.desarrollo.proyectocovidstats.query;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class QueryReport {
    private static final Logger logger = LogManager.getLogger(QueryReport.class);

    public static void main(String[] args) {
        Properties props = new Properties();

        try (InputStream input = QueryReport.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("The config.properties file could not be found");
                return;
            }
            props.load(input);
        } catch (IOException ex) {
            logger.error("Error loading configuration file", ex);
            return;
        }


        String url = props.getProperty("spring.datasource.url");
        String user = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");
        String reportDate = props.getProperty("app.report-date");


        Scanner scanner = new Scanner(System.in);
        System.out.print("\033[1;34mEnter the country ISO code: \033[0m"); // Blue text
        String countryIso = scanner.nextLine();

        String query = "SELECT province, SUM(confirmed) as total_confirmed, " +
                "SUM(deaths) as total_deaths, SUM(recovered) as total_recovered " +
                "FROM report " +
                "WHERE iso = ? AND date = ? " +
                "GROUP BY province " +
                "ORDER BY province";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, countryIso);
            stmt.setString(2, reportDate);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n\033[1;32m=== COVID-19 REPORT ===\033[0m");
            System.out.printf("\033[1;33m%-15s: %s\033[0m%n", "Country", countryIso);
            System.out.printf("\033[1;33m%-15s: %s\033[0m%n", "Date", reportDate);
            System.out.println("\033[1;32m===========================\033[0m");
            System.out.printf("\033[1;36m%-15s | %-18s | %-8s | %-11s\033[0m%n",
                    "PROVINCE", "CONFIRMED CASES", "DEATHS", "RECOVERED");
            System.out.println("\033[1;32m--------------------------------------------------\033[0m");

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                String province = rs.getString("province");
                int confirmedCases = rs.getInt("total_confirmed");
                int deaths = rs.getInt("total_deaths");
                int recovered = rs.getInt("total_recovered");

                System.out.printf("%-15s | %-18d | %-8d | %-11d%n",
                        province, confirmedCases, deaths, recovered);
            }

            if (!hasResults) {
                System.out.println("\033[1;31mNo data found for the specified date and country.\033[0m");
            }

            System.out.println("\033[1;32m===========================\033[0m");

        } catch (SQLException e) {
            logger.error("Error querying the database:" + e.getMessage());
        }
    }
}