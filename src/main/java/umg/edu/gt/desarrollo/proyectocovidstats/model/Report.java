package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private int confirmed;
    private int deaths;
    private int recovered;

    private String iso;
    private String location;
    private String province;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province provinceEntity;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }
    public int getDeaths() {
        return deaths;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    public int getRecovered() {
        return recovered;
    }
    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }
    public String getIso() {
        return iso;
    }
    public void setIso(String iso) {
        this.iso = iso;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public Province getProvinceEntity() {
        return provinceEntity;
    }
    public void setProvinceEntity(Province provinceEntity) {
        this.provinceEntity = provinceEntity;
    }
}
