package tn.bibliotheque.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Représente un emprunt en cours ou historisé.
 */
public class Emprunt implements Persistable {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String isbnLivre;
    private final String idAdherent;
    private final LocalDate dateEmprunt;
    private LocalDate dateRetour;

    public Emprunt(String isbnLivre, String idAdherent, LocalDate dateEmprunt) {
        this.isbnLivre = isbnLivre;
        this.idAdherent = idAdherent;
        this.dateEmprunt = dateEmprunt;
    }

    public Emprunt(String isbnLivre, String idAdherent, LocalDate dateEmprunt, LocalDate dateRetour) {
        this(isbnLivre, idAdherent, dateEmprunt);
        this.dateRetour = dateRetour;
    }

    public static Emprunt fromCsv(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Ligne emprunt invalide : " + line);
        }
        LocalDate emprunt = LocalDate.parse(parts[2], FORMAT);
        LocalDate retour = (parts.length >= 4 && !parts[3].isBlank())
                ? LocalDate.parse(parts[3], FORMAT)
                : null;
        return new Emprunt(parts[0], parts[1], emprunt, retour);
    }

    public String getIsbnLivre() {
        return isbnLivre;
    }

    public String getIdAdherent() {
        return idAdherent;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public boolean estEnCours() {
        return dateRetour == null;
    }

    @Override
    public String toCsvLine() {
        String retour = dateRetour != null ? dateRetour.format(FORMAT) : "";
        return Persistable.escape(isbnLivre) + ";" + Persistable.escape(idAdherent) + ";"
                + dateEmprunt.format(FORMAT) + ";" + retour;
    }

    @Override
    public String toString() {
        return isbnLivre + " → " + idAdherent + " (" + dateEmprunt + ")";
    }
}
