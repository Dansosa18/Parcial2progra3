package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository  extends JpaRepository<Province, Long> {
    Province findByNameAndRegion(String name, Region region);

    @Query("SELECT p FROM Province p JOIN p.region r WHERE p.name = :name AND r.iso = :iso")
    Optional<Province> findByNameAndRegionIso(@Param("name") String name, @Param("iso") String iso);

    @Query("SELECT p FROM Province p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Province> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM Province p WHERE p.name = :name")
    Optional<Province> findByName(@Param("name") String name);

}
