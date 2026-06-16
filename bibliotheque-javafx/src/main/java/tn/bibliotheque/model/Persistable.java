package tn.bibliotheque.model;

/**
 * Sérialisation texte pour la persistance fichier.
 */
public interface Persistable {

    String toCsvLine();

    static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(";", ",").replace("\n", " ").trim();
    }
}
