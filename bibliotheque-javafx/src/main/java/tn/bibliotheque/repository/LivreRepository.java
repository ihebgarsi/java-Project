package tn.bibliotheque.repository;

import tn.bibliotheque.model.Livre;

import java.util.List;
import java.util.stream.Collectors;

public class LivreRepository extends Repository<Livre, String> {

    public List<Livre> rechercherParMotCle(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            return listerTous();
        }
        String lower = motCle.toLowerCase().trim();
        return elements.stream()
                .filter(l -> l.getTitre().toLowerCase().contains(lower)
                        || l.getAuteur().toLowerCase().contains(lower)
                        || l.getIsbn().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Livre> listerDisponibles() {
        return elements.stream()
                .filter(Livre::estDisponible)
                .collect(Collectors.toList());
    }
}
