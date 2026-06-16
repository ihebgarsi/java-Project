package tn.bibliotheque.model;

/**
 * Interface commune pour les entités identifiées par un identifiant unique.
 */
public interface Identifiable {

    String getId();

    void setId(String id);
}
