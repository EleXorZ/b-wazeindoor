package wazeindoor.entity;

import jakarta.persistence.*;

@Entity
public class Chemin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poi_depart_id")
    private PointInteret poiDepart;

    @ManyToOne
    @JoinColumn(name = "poi_arrivee_id")
    private PointInteret poiArrivee;

    private double distance;  // En mètres

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PointInteret getPoiDepart() {
        return poiDepart;
    }

    public void setPoiDepart(PointInteret poiDepart) {
        this.poiDepart = poiDepart;
    }

    public PointInteret getPoiArrivee() {
        return poiArrivee;
    }

    public void setPoiArrivee(PointInteret poiArrivee) {
        this.poiArrivee = poiArrivee;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
