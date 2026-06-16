package tn.bibliotheque.model;

/**
 * Contrat pour les ressources pouvant être empruntées.
 */
public interface Empruntable {

    boolean estDisponible();

    void marquerEmprunte();

    void marquerRetourne();
}
