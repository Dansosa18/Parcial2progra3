package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "report")
public class HealthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private LocalDate reportDate;

    private int totalCases;
    private int totalDeaths;

    private String countryCode;
    private String countryName;
    private String regionName;

    // Eliminado: @ManyToOne provinceEntity
    // Eliminado: int recovered

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }
}
