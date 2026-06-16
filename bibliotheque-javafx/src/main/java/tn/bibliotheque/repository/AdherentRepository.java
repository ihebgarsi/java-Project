package tn.bibliotheque.repository;

import tn.bibliotheque.model.Adherent;

import java.util.List;
import java.util.stream.Collectors;

public class AdherentRepository extends Repository<Adherent, String> {

    public List<Adherent> rechercherParNom(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            return listerTous();
        }
        String lower = motCle.toLowerCase().trim();
        return elements.stream()
                .filter(a -> a.getNom().toLowerCase().contains(lower)
                        || a.getPrenom().toLowerCase().contains(lower)
                        || a.getId().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
