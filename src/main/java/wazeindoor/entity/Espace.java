package wazeindoor.entity;

import jakarta.persistence.*;
import wazeindoor.utils.TypeEspace;

@Entity
public class Espace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private double largeur;
    private double hauteur;

    @Enumerated(EnumType.STRING)
    private TypeEspace type;

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

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur() {
        return hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public TypeEspace getType() {
        return type;
    }

    public void setType(TypeEspace type) {
        this.type = type;
    }
}
