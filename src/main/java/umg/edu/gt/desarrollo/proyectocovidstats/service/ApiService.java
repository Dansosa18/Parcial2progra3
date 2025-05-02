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
        logger.info("🔄 Starting the process to fetch COVID-19 data...");

        String countryIso = appConfig.getCountryIso();
        String reportDate = appConfig.getReportDate();
        LocalDate reportDateSTR = LocalDate.parse(reportDate);

        logger.info("📅 Processing data for country: '{}' on date: '{}'", countryIso, reportDate);

        if (reportExecutionRepository.existsByExecutionDateAndCountryIso(reportDateSTR, countryIso)) {
            logger.warn("⏭️ Skipping execution: Data for country '{}' on '{}' has already been processed.", countryIso, reportDate);
            return;
        }

        logger.debug("🔍 Configuration details - countryIso: '{}', reportDate: '{}'", countryIso, reportDate);

        try {
            logger.info("🌍 Fetching regions data...");
            String regions = apiClient.getRegions();
            logger.debug("Regions data received: {}", regions);
            covidDataService.saveRegions(regions);

            logger.info("📍 Fetching provinces data for country: '{}'...", countryIso);
            String provinces = apiClient.getProvinces(countryIso);
            logger.debug("Provinces data received: {}", provinces);
            covidDataService.saveProvinces(provinces, countryIso);

            logger.info("📊 Fetching report data for date: '{}'...", reportDate);
            String report = apiClient.getReport(reportDate);
            logger.debug("Report data received: {}", report);
            covidDataService.saveReports(report);

            logger.info("🗂️ Registering execution for country: '{}' on date: '{}'", countryIso, reportDate);
            covidDataService.registerExecution(reportDateSTR, countryIso);
            logger.info("✅ Data successfully saved in the database.");

        } catch (Exception e) {
            logger.error("❌ An error occurred while fetching data or saving to the database: {}", e.getMessage(), e);
        }
    }
}