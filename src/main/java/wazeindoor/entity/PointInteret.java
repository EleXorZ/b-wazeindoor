package wazeindoor.entity;

import jakarta.persistence.*;
import wazeindoor.utils.TypePOI;

@Entity
public class PointInteret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Enumerated(EnumType.STRING)
    private TypePOI type;

    private double x;
    private double y;

    @ManyToOne
    @JoinColumn(name = "espace_id")
    private Espace espace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypePOI getType() {
        return type;
    }

    public void setType(TypePOI type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Espace getEspace() {
        return espace;
    }

    public void setEspace(Espace espace) {
        this.espace = espace;
    }
}
