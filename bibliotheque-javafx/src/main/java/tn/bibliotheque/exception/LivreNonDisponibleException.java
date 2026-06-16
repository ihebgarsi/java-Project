package tn.bibliotheque.exception;

public class LivreNonDisponibleException extends BibliothequeException {

    public LivreNonDisponibleException(String isbn) {
        super("Le livre " + isbn + " n'est pas disponible pour l'emprunt.");
    }
}
