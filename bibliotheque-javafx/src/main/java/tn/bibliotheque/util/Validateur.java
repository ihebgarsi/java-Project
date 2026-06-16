package tn.bibliotheque.util;

import tn.bibliotheque.exception.BibliothequeException;

/**
 * Validation des champs saisis (String, tableaux de contrôle).
 */
public final class Validateur {

    private Validateur() {
    }

    public static void verifierNonVide(String valeur, String nomChamp) throws BibliothequeException {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new BibliothequeException("Le champ « " + nomChamp + " » est obligatoire.");
        }
    }

    public static void verifierIsbn(String isbn) throws BibliothequeException {
        verifierNonVide(isbn, "ISBN");
        String clean = isbn.trim();
        if (clean.length() < 10 || clean.length() > 17) {
            throw new BibliothequeException("ISBN invalide (10 à 17 caractères) : " + isbn);
        }
    }

    public static void verifierEmail(String email) throws BibliothequeException {
        verifierNonVide(email, "Email");
        if (!email.contains("@") || !email.contains(".")) {
            throw new BibliothequeException("Format d'email invalide.");
        }
    }

    public static String[] decouperMots(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return new String[0];
        }
        return phrase.trim().split("\\s+");
    }
}
