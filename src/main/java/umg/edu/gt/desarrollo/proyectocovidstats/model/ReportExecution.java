package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "executed_reports")
public class ReportExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_date", nullable = false)
    private LocalDate executionDate;

    @Column(name = "country_iso", nullable = false, length = 3)
    private String countryIso;



    public Long getId() {
        return id;
    }
    public LocalDate getExecutionDate() {
        return executionDate;
    }
    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }
    public String getCountryIso() {
        return countryIso;
    }
    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

}
