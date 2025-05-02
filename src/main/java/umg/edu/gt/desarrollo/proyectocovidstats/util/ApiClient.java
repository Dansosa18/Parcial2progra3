package umg.edu.gt.desarrollo.proyectocovidstats.util;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;

@Component
@EnableConfigurationProperties(AppConfig.class)
public class ApiClient {

    private static final String API_URL = "https://covid-19-statistics.p.rapidapi.com/regions";
    private static final String API_KEY = "9f0f1ec744mshb600222f9c6063dp15733ajsnc806d7c223e2";

    // Method to fetch regions
    public String getRegions() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-host", "covid-19-statistics.p.rapidapi.com");
        headers.add("x-rapidapi-key", API_KEY);

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL, HttpMethod.GET, new org.springframework.http.HttpEntity<>(headers), String.class);

        return response.getBody();
    }

    // Method to fetch provinces based on ISO code
    public String getProvinces(String isoCode) {
        if (isoCode == null || isoCode.trim().isEmpty()) {
            throw new IllegalArgumentException("The ISO code cannot be null or empty");
        }

        String url = "https://covid-19-statistics.p.rapidapi.com/provinces?iso=" + isoCode;
        return fetchData(url);
    }

    public String getReport(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("The date  cannot be null or empty");
        }
        String url = "https://covid-19-statistics.p.rapidapi.com/reports?&date=" + date;
        return fetchData(url);
    }

    // Helper method to avoid code repetition for fetching data
    private String fetchData(String url) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-host", "covid-19-statistics.p.rapidapi.com");
        headers.add("x-rapidapi-key", API_KEY);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new org.springframework.http.HttpEntity<>(headers), String.class);

        return response.getBody();
    }
}
