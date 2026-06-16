package tn.bibliotheque.repository;

import tn.bibliotheque.model.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository générique pour la gestion des entités en mémoire.
 *
 * @param <T> type d'entité
 * @param <ID> type de l'identifiant
 */
public abstract class Repository<T extends Identifiable, ID> {

    protected final List<T> elements = new ArrayList<>();

    public void ajouter(T element) {
        if (element == null) {
            throw new IllegalArgumentException("L'élément ne peut pas être null.");
        }
        if (trouverParId((ID) element.getId()).isPresent()) {
            throw new IllegalArgumentException("Un élément avec cet identifiant existe déjà : " + element.getId());
        }
        elements.add(element);
    }

    public boolean supprimer(ID id) {
        return elements.removeIf(e -> e.getId().equals(String.valueOf(id)));
    }

    public Optional<T> trouverParId(ID id) {
        String key = String.valueOf(id);
        return elements.stream()
                .filter(e -> e.getId().equals(key))
                .findFirst();
    }

    public List<T> listerTous() {
        return new ArrayList<>(elements);
    }

    public void remplacerTous(List<T> nouveaux) {
        elements.clear();
        if (nouveaux != null) {
            elements.addAll(nouveaux);
        }
    }

    public int taille() {
        return elements.size();
    }

    public void vider() {
        elements.clear();
    }
}
