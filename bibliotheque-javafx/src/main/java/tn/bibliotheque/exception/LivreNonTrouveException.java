package tn.bibliotheque.exception;

public class LivreNonTrouveException extends BibliothequeException {

    public LivreNonTrouveException(String isbn) {
        super("Livre introuvable pour l'ISBN : " + isbn);
    }
}
