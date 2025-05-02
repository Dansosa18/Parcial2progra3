package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;

import java.util.List;


public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByIso(String iso);
}