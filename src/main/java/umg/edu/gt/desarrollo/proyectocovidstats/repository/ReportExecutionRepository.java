package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.ReportExecution;

import java.time.LocalDate;

public interface ReportExecutionRepository extends JpaRepository<ReportExecution, Long> {
    boolean existsByExecutionDateAndCountryIso(LocalDate executionDate, String countryIso);
}
