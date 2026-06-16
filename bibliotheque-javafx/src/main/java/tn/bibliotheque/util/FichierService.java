package tn.bibliotheque.util;

import tn.bibliotheque.exception.BibliothequeException;
import tn.bibliotheque.model.Adherent;
import tn.bibliotheque.model.Emprunt;
import tn.bibliotheque.model.Livre;
import tn.bibliotheque.model.Persistable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Sauvegarde et chargement des données en fichiers CSV (innovation : persistance fichier).
 */
public class FichierService {

    private final Path dossierData;

    public FichierService(Path dossierData) {
        this.dossierData = dossierData;
    }

    public void sauvegarderLivres(List<Livre> livres) throws BibliothequeException {
        sauvegarder("livres.csv", livres);
    }

    public void sauvegarderAdherents(List<Adherent> adherents) throws BibliothequeException {
        sauvegarder("adherents.csv", adherents);
    }

    public void sauvegarderEmprunts(List<Emprunt> emprunts) throws BibliothequeException {
        sauvegarder("emprunts.csv", emprunts);
    }

    private void sauvegarder(String nomFichier, List<? extends Persistable> lignes) throws BibliothequeException {
        try {
            Files.createDirectories(dossierData);
            Path fichier = dossierData.resolve(nomFichier);
            List<String> contenu = new ArrayList<>();
            contenu.add(entete(nomFichier));
            for (Persistable p : lignes) {
                contenu.add(p.toCsvLine());
            }
            Files.write(fichier, contenu, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BibliothequeException("Erreur lors de la sauvegarde de " + nomFichier, e);
        }
    }

    private String entete(String nomFichier) {
        return switch (nomFichier) {
            case "livres.csv" -> "#isbn;titre;auteur;isbn;disponible";
            case "adherents.csv" -> "#id;nom;prenom;email";
            case "emprunts.csv" -> "#isbn;idAdherent;dateEmprunt;dateRetour";
            default -> "#donnees";
        };
    }

    public List<Livre> chargerLivres() throws BibliothequeException {
        return charger("livres.csv", Livre::fromCsv);
    }

    public List<Adherent> chargerAdherents() throws BibliothequeException {
        return charger("adherents.csv", Adherent::fromCsv);
    }

    public List<Emprunt> chargerEmprunts() throws BibliothequeException {
        return charger("emprunts.csv", Emprunt::fromCsv);
    }

    private <T> List<T> charger(String nomFichier, LigneParser<T> parser) throws BibliothequeException {
        Path fichier = dossierData.resolve(nomFichier);
        if (!Files.exists(fichier)) {
            return new ArrayList<>();
        }
        try {
            List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
            List<T> resultat = new ArrayList<>();
            for (String ligne : lignes) {
                if (ligne.isBlank() || ligne.startsWith("#")) {
                    continue;
                }
                resultat.add(parser.parse(ligne));
            }
            return resultat;
        } catch (IOException | RuntimeException e) {
            throw new BibliothequeException("Erreur lors du chargement de " + nomFichier, e);
        }
    }

    @FunctionalInterface
    private interface LigneParser<T> {
        T parse(String ligne);
    }
}
