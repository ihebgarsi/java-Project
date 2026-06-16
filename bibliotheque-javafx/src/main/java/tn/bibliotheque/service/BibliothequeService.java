package tn.bibliotheque.service;

import tn.bibliotheque.exception.*;
import tn.bibliotheque.model.Adherent;
import tn.bibliotheque.model.Emprunt;
import tn.bibliotheque.model.Livre;
import tn.bibliotheque.repository.AdherentRepository;
import tn.bibliotheque.repository.LivreRepository;
import tn.bibliotheque.util.FichierService;
import tn.bibliotheque.util.Validateur;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Service métier central : livres, adhérents, emprunts/retours.
 * Utilise List, Set et Map comme exigé par la grille d'évaluation.
 */
public class BibliothequeService {

    private final LivreRepository livreRepository = new LivreRepository();
    private final AdherentRepository adherentRepository = new AdherentRepository();

    /** Emprunts en cours : clé = ISBN du livre */
    private final Map<String, Emprunt> empruntsEnCours = new HashMap<>();

    /** Historique des emprunts par adhérent */
    private final Map<String, List<Emprunt>> historiqueParAdherent = new HashMap<>();

    /** Ensemble des ISBN déjà enregistrés (unicité) */
    private final Set<String> isbnEnregistres = new HashSet<>();

    private final FichierService fichierService;

    public BibliothequeService(Path dossierData) {
        this.fichierService = new FichierService(dossierData);
    }

    // ——— Livres ———

    public void ajouterLivre(String isbn, String titre, String auteur, int quantite) throws BibliothequeException {
        Validateur.verifierIsbn(isbn);
        Validateur.verifierNonVide(titre, "Titre");
        Validateur.verifierNonVide(auteur, "Auteur");

        if (quantite <= 0) {
            throw new BibliothequeException("La quantité doit être supérieure à 0.");
        }

        if (isbnEnregistres.contains(isbn.trim())) {
            throw new BibliothequeException("Un livre avec cet ISBN existe déjà.");
        }

        Livre livre = new Livre(isbn.trim(), titre.trim(), auteur.trim(), true, quantite);
        livreRepository.ajouter(livre);
        isbnEnregistres.add(livre.getIsbn());
    }

    public void ajouterLivre(String isbn, String titre, String auteur) throws BibliothequeException {
        ajouterLivre(isbn, titre, auteur, 1);
    }

    public void modifierLivre(String isbn, String titre, String auteur, int quantite) throws BibliothequeException {
        Livre livre = obtenirLivre(isbn);
        Validateur.verifierNonVide(titre, "Titre");
        Validateur.verifierNonVide(auteur, "Auteur");
        if (quantite <= 0) {
            throw new BibliothequeException("La quantité doit être supérieure à 0.");
        }
        livre.setTitre(titre.trim());
        livre.setAuteur(auteur.trim());
        livre.setQuantite(quantite);
    }

    public void modifierLivre(String isbn, String titre, String auteur) throws BibliothequeException {
        Livre livre = obtenirLivre(isbn);
        Validateur.verifierNonVide(titre, "Titre");
        Validateur.verifierNonVide(auteur, "Auteur");
        livre.setTitre(titre.trim());
        livre.setAuteur(auteur.trim());
    }

    public void supprimerLivre(String isbn) throws BibliothequeException {
        Livre livre = obtenirLivre(isbn);
        if (!livre.estDisponible()) {
            throw new BibliothequeException("Impossible de supprimer : le livre est emprunté.");
        }
        livreRepository.supprimer(isbn);
        isbnEnregistres.remove(isbn);
    }

    public List<Livre> listerLivres() {
        return livreRepository.listerTous();
    }

    public List<Livre> rechercherLivres(String motCle) {
        return livreRepository.rechercherParMotCle(motCle);
    }

    public Livre obtenirLivre(String isbn) throws LivreNonTrouveException {
        return livreRepository.trouverParId(isbn)
                .orElseThrow(() -> new LivreNonTrouveException(isbn));
    }

    // ——— Adhérents ———

    public void ajouterAdherent(String id, String nom, String prenom, String email) throws BibliothequeException {
        Validateur.verifierNonVide(id, "Identifiant");
        Validateur.verifierNonVide(nom, "Nom");
        Validateur.verifierNonVide(prenom, "Prénom");
        Validateur.verifierEmail(email);
        adherentRepository.ajouter(new Adherent(id.trim(), nom.trim(), prenom.trim(), email.trim()));
    }

    public void modifierAdherent(String id, String nom, String prenom, String email) throws BibliothequeException {
        Adherent a = obtenirAdherent(id);
        Validateur.verifierNonVide(nom, "Nom");
        Validateur.verifierNonVide(prenom, "Prénom");
        Validateur.verifierEmail(email);
        a.setNom(nom.trim());
        a.setPrenom(prenom.trim());
        a.setEmail(email.trim());
    }

    public void supprimerAdherent(String id) throws BibliothequeException {
        obtenirAdherent(id);
        boolean aDesEmprunts = empruntsEnCours.values().stream()
                .anyMatch(e -> e.getIdAdherent().equals(id));
        if (aDesEmprunts) {
            throw new BibliothequeException("Impossible de supprimer : l'adhérent a des emprunts en cours.");
        }
        adherentRepository.supprimer(id);
        historiqueParAdherent.remove(id);
    }

    public List<Adherent> listerAdherents() {
        return adherentRepository.listerTous();
    }

    public List<Adherent> rechercherAdherents(String motCle) {
        return adherentRepository.rechercherParNom(motCle);
    }

    public Adherent obtenirAdherent(String id) throws AdherentNonTrouveException {
        return adherentRepository.trouverParId(id)
                .orElseThrow(() -> new AdherentNonTrouveException(id));
    }

    // ——— Emprunts / retours ———

    public Emprunt emprunterLivre(String isbn, String idAdherent) throws BibliothequeException {
        Livre livre = obtenirLivre(isbn);
        obtenirAdherent(idAdherent);

        if (!livre.estDisponible() || livre.getQuantite() <= 0 || empruntsEnCours.containsKey(isbn)) {
            throw new LivreNonDisponibleException(isbn);
        }

        livre.decrementerQuantite();
        Emprunt emprunt = new Emprunt(isbn, idAdherent, LocalDate.now());
        empruntsEnCours.put(isbn, emprunt);
        historiqueParAdherent.computeIfAbsent(idAdherent, k -> new ArrayList<>()).add(emprunt);
        return emprunt;
    }

    public void retournerLivre(String isbn) throws BibliothequeException {
        Livre livre = obtenirLivre(isbn);
        Emprunt emprunt = empruntsEnCours.get(isbn);
        if (emprunt == null) {
            throw new EmpruntInvalideException("Aucun emprunt en cours pour l'ISBN : " + isbn);
        }
        emprunt.setDateRetour(LocalDate.now());
        livre.incrementerQuantite();
        empruntsEnCours.remove(isbn);
    }

    public Map<String, Emprunt> getEmpruntsEnCours() {
        return Collections.unmodifiableMap(empruntsEnCours);
    }

    public List<Emprunt> getHistoriqueAdherent(String idAdherent) {
        return historiqueParAdherent.getOrDefault(idAdherent, List.of());
    }

    public List<Emprunt> getTousLesEmprunts() {
        List<Emprunt> tous = new ArrayList<>(empruntsEnCours.values());
        historiqueParAdherent.values().forEach(tous::addAll);
        return tous;
    }

    // ——— Statistiques (tableaux + String) ———

    public String genererRapport() {
        int totalLivres = livreRepository.taille();
        int disponibles = livreRepository.listerDisponibles().size();
        int emprunts = empruntsEnCours.size();
        int adherents = adherentRepository.taille();

        String[] lignes = {
                "=== Rapport bibliothèque ===",
                "Livres enregistrés : " + totalLivres,
                "Livres disponibles : " + disponibles,
                "Emprunts en cours : " + emprunts,
                "Adhérents : " + adherents,
                "Généré le : " + LocalDate.now()
        };
        return String.join(System.lineSeparator(), lignes);
    }

    // ——— Persistance fichier ———

    public void sauvegarder() throws BibliothequeException {
        fichierService.sauvegarderLivres(listerLivres());
        fichierService.sauvegarderAdherents(listerAdherents());
        fichierService.sauvegarderEmprunts(getTousLesEmprunts());
    }

    public void charger() throws BibliothequeException {
        livreRepository.vider();
        adherentRepository.vider();
        empruntsEnCours.clear();
        historiqueParAdherent.clear();
        isbnEnregistres.clear();

        for (Livre l : fichierService.chargerLivres()) {
            livreRepository.ajouter(l);
            isbnEnregistres.add(l.getIsbn());
            if (!l.estDisponible()) {
                // sera recalculé via emprunts
            }
        }
        for (Adherent a : fichierService.chargerAdherents()) {
            adherentRepository.ajouter(a);
        }
        for (Emprunt e : fichierService.chargerEmprunts()) {
            if (e.estEnCours()) {
                empruntsEnCours.put(e.getIsbnLivre(), e);
                try {
                    Livre livre = obtenirLivre(e.getIsbnLivre());
                    livre.marquerEmprunte();
                } catch (LivreNonTrouveException ignored) {
                    // livre supprimé entre-temps
                }
            }
            historiqueParAdherent.computeIfAbsent(e.getIdAdherent(), k -> new ArrayList<>()).add(e);
        }
    }

    public void chargerDonneesDemo() {
        try {
            ajouterLivre("9782070368228", "Le Petit Prince", "Antoine de Saint-Exupéry", 3);
            ajouterLivre("9782253141032", "1984", "George Orwell", 2);
            ajouterLivre("9782070408509", "L'Étranger", "Albert Camus", 5);
            ajouterAdherent("A001", "Ben Ali", "Sami", "sami@email.tn");
            ajouterAdherent("A002", "Trabelsi", "Amira", "amira@email.tn");
        } catch (BibliothequeException e) {
            // données déjà présentes
        }
    }
}
