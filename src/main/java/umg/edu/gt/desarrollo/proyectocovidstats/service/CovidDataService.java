package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.model.ReportExecution;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportExecutionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CovidDataService {
    private static final Logger logger = LogManager.getLogger(CovidDataService.class);

    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    // Internal HashMaps for temporary work
    private final Map<String, Region> isoRegionMap = new HashMap<>();
    private final Map<String, Province> nameProvinceMap = new HashMap<>();
    private final ReportExecutionRepository reportExecutionRepository;


    public CovidDataService(RegionRepository regionRepository,
                            ProvinceRepository provinceRepository,
                            ReportRepository reportRepository, ReportExecutionRepository reportExecutionRepository) {
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = new ObjectMapper();
        this.reportExecutionRepository = reportExecutionRepository;
    }


    public void saveRegions(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Region> regionsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            String iso = node.get("iso").asText();

            List<Region> existingList = regionRepository.findByIso(iso);
            Region region;

            if (!existingList.isEmpty()) {
                // If it exists, we update the data
                region = existingList.get(0);
                region.setName(node.get("name").asText());
            } else {
                //If it doesn't exist, we create a new one
                region = new Region();
                region.setIso(iso);
                region.setName(node.get("name").asText());
                regionsToSave.add(region);
            }

            isoRegionMap.put(region.getIso(), region);
        }

        // We only save the new regions
        if (!regionsToSave.isEmpty()) {
            regionRepository.saveAll(regionsToSave);
        }
    }

    public void saveProvinces(String json, String isoCode) throws Exception {
        Region region = isoRegionMap.get(isoCode);
        if (region == null) {
            List<Region> existingList = regionRepository.findByIso(isoCode);
            if (existingList.isEmpty()) {
                throw new Exception("Region not found for ISO code: " + isoCode);
            }
            region = existingList.get(0);
            isoRegionMap.put(isoCode, region);
        }

        JsonNode root = objectMapper.readTree(json).get("data");
        List<Province> provincesToSave = new ArrayList<>();

        // Clear the province map for this region
        for (Iterator<Map.Entry<String, Province>> it = nameProvinceMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Province> entry = it.next();
            if (entry.getValue().getRegion().getId().equals(region.getId())) {
                it.remove();
            }
        }

        for (JsonNode node : root) {
            String provinceName = node.get("province").asText().isEmpty() ? "N/A" : node.get("province").asText();
            String iso = node.has("iso") ? node.get("iso").asText() : null;
            Double lat = node.has("lat") ? node.get("lat").asDouble() : null;
            Double lon = node.has("long") ? node.get("long").asDouble() : null;

            Province province = provinceRepository.findByNameAndRegion(provinceName, region);

            if (province == null) {
                province = new Province();
                province.setName(provinceName);
                province.setRegion(region);
                province.setIso(iso);
                province.setLat(lat);
                province.setLon(lon);
                provincesToSave.add(province);
            } else {
                // Update fields if the province already exists
                if (iso != null) province.setIso(iso);
                if (lat != null) province.setLat(lat);
                if (lon != null) province.setLon(lon);
                provincesToSave.add(province);
            }

            nameProvinceMap.put(provinceName, province);
            nameProvinceMap.put(provinceName.toLowerCase(), province);

            if (node.has("name")) {
                String altName = node.get("name").asText();
                if (!altName.isEmpty() && !altName.equals(provinceName)) {
                    nameProvinceMap.put(altName, province);
                    nameProvinceMap.put(altName.toLowerCase(), province);
                }
            }
        }

        if (!provincesToSave.isEmpty()) {
            provinceRepository.saveAll(provincesToSave);
            logger.info("Saved " + provincesToSave.size() + " provinces (new or updated)");
        }

        logger.info("Total number of provinces on the temporary map: " + nameProvinceMap.size());
    }

    @Transactional
    public void saveReports(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Report> reportsToSave = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (JsonNode node : root) {
            try {

                String dateStr = node.get("date").asText();
                LocalDate reportDate = LocalDate.parse(dateStr, dateFormatter);

                JsonNode region = node.get("region");
                String iso = region.get("iso").asText();
                String location = region.get("name").asText();
                String provinceName = region.has("province") ? region.get("province").asText() : "";


                Report report = reportRepository.findByDateAndIsoAndProvince(reportDate, iso, provinceName)
                        .orElseGet(() -> {
                            Report newReport = new Report();
                            newReport.setDate(reportDate);
                            newReport.setIso(iso);
                            newReport.setProvince(provinceName);
                            return newReport;
                        });


                report.setLocation(location);
                report.setConfirmed(node.get("confirmed").asInt());
                report.setDeaths(node.get("deaths").asInt());
                report.setRecovered(node.get("recovered").asInt());


                if (!provinceName.isEmpty()) {
                    provinceRepository.findByName(provinceName)
                            .ifPresent(report::setProvinceEntity);
                }

                reportsToSave.add(report);
                logger.debug("Ready to store: {}" + report.toString());

            } catch (Exception e) {
                logger.error("Error processing node: {}" + node.toString(), e);
            }
        }

        if (!reportsToSave.isEmpty()) {
            List<Report> savedReports = reportRepository.saveAll(reportsToSave);
            logger.info("Saved reports: {}" + savedReports.size());
            for (Report report : savedReports) {
                logger.debug("Saved report: {}" + report.toString());
            }
        }
    }

    @Transactional
    public void registerExecution(LocalDate date, String countryIso) {
        if (reportExecutionRepository.existsByExecutionDateAndCountryIso(date, countryIso)) {
            throw new IllegalStateException("The country " + countryIso + " has already been processed by this date " + date);
        }

        ReportExecution execution = new ReportExecution();
        execution.setExecutionDate(date);
        execution.setCountryIso(countryIso);
        reportExecutionRepository.save(execution);
    }

}
