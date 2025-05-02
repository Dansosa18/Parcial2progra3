package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.date = :date AND r.iso = :iso AND " +
            "(r.province = :province OR (:province IS NULL AND r.province IS NULL))")
    Optional<Report> findByDateAndIsoAndProvince(
            @Param("date") LocalDate date,
            @Param("iso") String iso,
            @Param("province") String province);

}

