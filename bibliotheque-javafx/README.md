# Gestion de Bibliothèque — Projet Java + JavaFX

Application conforme à la **Grille d'évaluation projet Java** :
gestion des livres (titre, auteur, ISBN, disponibilité), des adhérents, des emprunts et des retours.

## Critères couverts

| Critère | Implémentation |
|--------|----------------|
| Java fondamental | POO, encapsulation, `String`, tableaux (`Validateur.decouperMots`, rapport) |
| POO avancée | Héritage (`Personne` → `Adherent`, `Ressource` → `Livre`), polymorphisme, classes abstraites, interfaces |
| Exceptions | `BibliothequeException` et sous-classes, gestion dans l'UI |
| Généricité | `Repository<T, ID>` |
| Collections | `List`, `Set` (ISBN uniques), `Map` (emprunts en cours, historique) |
| Innovation | Sauvegarde/chargement **CSV** dans `data/`, interface **JavaFX** |
| Fonctionnalité | CRUD livres/adhérents, emprunt, retour, recherche, rapport |

## Prérequis

- **JDK 21+** (JavaFX téléchargé automatiquement par `run.ps1`)

## Lancer l'application

### Option 1 — Script PowerShell (recommandé)

```powershell
cd bibliotheque-javafx
.\run.ps1
```

### Option 2 — Maven (si installé)

```bash
mvn javafx:run
```

## Structure du projet

```
src/main/java/tn/bibliotheque/
  model/       # Entités, interfaces, classes abstraites
  repository/  # Repository générique + implémentations
  service/     # Logique métier
  exception/   # Exceptions personnalisées
  util/        # Fichiers CSV, validation
  ui/          # MainApp, MainController (JavaFX)
data/          # Fichiers CSV (créés à la sauvegarde)
```

## Utilisation

1. **Livres** : ajouter, modifier, supprimer, rechercher.
2. **Adhérents** : gérer les membres de la bibliothèque.
3. **Emprunts** : emprunter un livre disponible, retourner un emprunt en cours.
4. **Fichiers** : sauvegarder/charger les données CSV, générer un rapport.

Les données sont sauvegardées automatiquement à la fermeture dans le dossier `data/`.

## Présentation orale — points clés

- **Héritage** : `Livre extends Ressource`, `Adherent extends Personne`
- **Interfaces** : `Empruntable`, `Identifiable`, `Persistable`
- **Map** : `empruntsEnCours` (ISBN → Emprunt)
- **Set** : `isbnEnregistres` pour l'unicité des ISBN
- **List** : stockage des livres et adhérents dans les repositories
- **Généricité** : `Repository<T extends Identifiable, ID>`
- **Fichiers** : format CSV avec en-têtes `#...`
