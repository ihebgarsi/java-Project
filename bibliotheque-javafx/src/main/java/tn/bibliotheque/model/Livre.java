package tn.bibliotheque.model;

/**
 * Livre : titre, auteur, ISBN, disponibilité, quantité.
 */
public class Livre extends Ressource {

    private String auteur;
    private String isbn;
    private int quantite;

    public Livre(String isbn, String titre, String auteur, boolean disponible, int quantite) {
        super(isbn, titre, disponible);
        this.isbn = isbn;
        this.auteur = auteur;
        this.quantite = quantite;
    }

    public Livre(String isbn, String titre, String auteur, boolean disponible) {
        this(isbn, titre, auteur, disponible, 1);
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        setId(isbn);
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = Math.max(0, quantite);
        if (quantite <= 0) {
            marquerEmprunte();
        } else {
            marquerRetourne();
        }
    }

    public void decrementerQuantite() {
        setQuantite(quantite - 1);
    }

    public void incrementerQuantite() {
        setQuantite(quantite + 1);
    }

    public static Livre fromCsv(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Ligne livre invalide : " + line);
        }
        boolean dispo = parts.length < 5 || Boolean.parseBoolean(parts[4]);
        int quantite = (parts.length < 6) ? 1 : Integer.parseInt(parts[5]);
        return new Livre(parts[0], parts[1], parts[2], dispo, quantite);
    }

    @Override
    public String getDescription() {
        return getTitre() + " — " + auteur;
    }

    @Override
    public String toCsvLine() {
        return Persistable.escape(isbn) + ";" + Persistable.escape(getTitre()) + ";"
                + Persistable.escape(auteur) + ";" + isbn + ";" + estDisponible() + ";" + quantite;
    }
}
