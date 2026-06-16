package tn.bibliotheque.model;

/**
 * Classe abstraite de base pour les personnes (adhérents).
 */
public abstract class Personne implements Identifiable, Persistable {

    private String id;
    private String nom;
    private String prenom;
    private String email;

    protected Personne(String id, String nom, String prenom, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return getNomComplet() + " (" + id + ")";
    }
}
