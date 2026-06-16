package tn.bibliotheque.model;

/**
 * Adhérent de la bibliothèque.
 */
public class Adherent extends Personne {

    public Adherent(String id, String nom, String prenom, String email) {
        super(id, nom, prenom, email);
    }

    public static Adherent fromCsv(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Ligne adhérent invalide : " + line);
        }
        return new Adherent(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String getType() {
        return "ADHERENT";
    }

    @Override
    public String toCsvLine() {
        return Persistable.escape(getId()) + ";" + Persistable.escape(getNom()) + ";"
                + Persistable.escape(getPrenom()) + ";" + Persistable.escape(getEmail());
    }
}
