package umg.edu.gt.desarrollo.proyectocovidstats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationSettings {
    private String countryIso;
    private String reportDate;

    //delay
    private long initialDelay;

    public String getCountryIso() {return countryIso;}
    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }
    public String getHealthReportDate() {
        return reportDate;
    }
    public void setHealthReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
    public long getInitialDelay() {
        return initialDelay;
    }
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }
}

