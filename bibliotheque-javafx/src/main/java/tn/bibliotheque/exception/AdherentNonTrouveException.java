package tn.bibliotheque.exception;

public class AdherentNonTrouveException extends BibliothequeException {

    public AdherentNonTrouveException(String id) {
        super("Adhérent introuvable : " + id);
    }
}
