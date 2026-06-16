package tn.bibliotheque.exception;

/**
 * Exception de base pour l'application bibliothèque.
 */
public class BibliothequeException extends Exception {

    public BibliothequeException(String message) {
        super(message);
    }

    public BibliothequeException(String message, Throwable cause) {
        super(message, cause);
    }
}
