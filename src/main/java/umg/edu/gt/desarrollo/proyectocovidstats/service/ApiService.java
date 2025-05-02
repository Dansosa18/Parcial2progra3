package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportExecutionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;



@Service
public class ApiService {

    private static final Logger logger = LogManager.getLogger(ApiService.class);
    private final ApiClient apiClient;
    private final CovidDataService covidDataService;
    private final AppConfig appConfig;

    private final ReportExecutionRepository reportExecutionRepository;


    public ApiService(ApiClient apiClient, CovidDataService covidDataService, AppConfig appConfig, ReportExecutionRepository reportExecutionRepository) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
        this.appConfig = appConfig;
        this.reportExecutionRepository = reportExecutionRepository;
    }

    @Transactional
    public void fetchCovidData() {
        logger.info("Fetching COVID-19 data...");

        String countryIso = appConfig.getCountryIso();
        String reportDate = appConfig.getReportDate();
        LocalDate reportDateSTR = LocalDate.parse(reportDate);

        logger.info("Starting processing for country {} on date {}", countryIso, reportDate);
        // Check if the report has already been run for the date and country
        if (reportExecutionRepository.existsByExecutionDateAndCountryIso(reportDateSTR, countryIso)) {
            logger.info("⏭️ Country {} has already been processed for {}. Skipping execution.", countryIso, reportDate);
            return;
        }

        logger.info("countryIso: '{}'", countryIso);
        logger.info("reportDate: '{}'", reportDate);


        try {
            // 🔹 Get regions
            String regions = apiClient.getRegions();
            logger.info("Regions: " + regions);
            covidDataService.saveRegions(regions); // ✅ Save to database

            // 🔹 Get Provinces
            String provinces = apiClient.getProvinces(countryIso);
            logger.info("Provinces for {}: {}", countryIso, provinces);
            covidDataService.saveProvinces(provinces, countryIso);

            // 🔹 Get Report
            String report = apiClient.getReport(reportDate);
            logger.info("Report for all on {}: {}", reportDate, report);
            covidDataService.saveReports(report);

            // 🔹 Get Report by Country and Date
            covidDataService.registerExecution(reportDateSTR, countryIso);
            logger.info("✅ Report saved in the database.");

        } catch (Exception e) {
            logger.error("❌ Error consuming the API or saving to the DB:");

        }
    }
}
