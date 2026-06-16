package tn.bibliotheque.model;

/**
 * Classe abstraite pour les ressources de la bibliothèque (livres, etc.).
 */
public abstract class Ressource implements Identifiable, Empruntable, Persistable {

    private String id;
    private String titre;
    private boolean disponible;

    protected Ressource(String id, String titre, boolean disponible) {
        this.id = id;
        this.titre = titre;
        this.disponible = disponible;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public boolean estDisponible() {
        return disponible;
    }

    @Override
    public void marquerEmprunte() {
        disponible = false;
    }

    @Override
    public void marquerRetourne() {
        disponible = true;
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return titre + " [" + getId() + "]";
    }
}
