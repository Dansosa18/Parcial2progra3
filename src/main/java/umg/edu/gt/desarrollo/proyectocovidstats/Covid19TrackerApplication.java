package umg.edu.gt.desarrollo.proyectocovidstats;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.service.ApiService;


@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class Covid19TrackerApplication {

    private static final Logger logger = LogManager.getLogger(Covid19TrackerApplication.class);

    private final ApiService apiService;

    @Value("${app.initialDelay}")
    private long initialDelay;

    @Autowired
    public Covid19TrackerApplication(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostConstruct
    public void fetchData() {
        try {
            logger.info("Waiting {} seconds to start loading data...", initialDelay / 1000);
            Thread.sleep(initialDelay);  // Using the initialDelay value from the properties
            logger.info("Starting data loading from the API");
            apiService.fetchCovidData();
            logger.info("Data upload completed successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting before executing fetchData: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Covid19TrackerApplication.class, args);
    }
}