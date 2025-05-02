package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.util.List;


@Entity
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String iso;
    private String name;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Province> provinces;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIso() {
        return iso;
    }
    public void setIso(String iso) {
        this.iso = iso;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Province> getProvinces() {
        return provinces;
    }
    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
}
