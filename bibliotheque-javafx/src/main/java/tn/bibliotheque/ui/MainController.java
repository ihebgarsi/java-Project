package tn.bibliotheque.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.bibliotheque.exception.BibliothequeException;
import tn.bibliotheque.model.Adherent;
import tn.bibliotheque.model.Emprunt;
import tn.bibliotheque.model.Livre;
import tn.bibliotheque.service.BibliothequeService;

import java.util.Map;

/**
 * Interface graphique JavaFX principale (onglets Livres, Adhérents, Emprunts, Fichiers).
 */
public class MainController {

    private final BibliothequeService service;
    private final BorderPane root = new BorderPane();
    private final Label statusLabel = new Label("Prêt");

    private TableView<Livre> tableLivres;
    private TableView<Adherent> tableAdherents;
    private TableView<EmpruntRow> tableEmprunts;
    private Runnable refreshCombosEmprunts;

    public MainController(BibliothequeService service) {
        this.service = service;
        construireInterface();
        rafraichirTout();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void construireInterface() {
        Label titre = new Label("📚 Bibliothèque — Gestion des livres et emprunts");
        titre.getStyleClass().add("titre-app");
        BorderPane.setMargin(titre, new Insets(15, 20, 10, 20));

        TabPane onglets = new TabPane();
        onglets.getTabs().addAll(
                new Tab("📖 Livres", creerOngletLivres()),
                new Tab("👤 Adhérents", creerOngletAdherents()),
                new Tab("📤 Emprunts", creerOngletEmprunts()),
                new Tab("📋 Fichiers & Rapport", creerOngletFichiers()));
        onglets.getTabs().forEach(t -> t.setClosable(false));
        onglets.setStyle("-fx-font-size: 13px;");

        VBox topBox = new VBox(titre, onglets);
        topBox.setStyle("-fx-background-color: #1a1a2e;");
        root.setTop(topBox);
        VBox.setVgrow(onglets, Priority.ALWAYS);

        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(12, 20, 12, 20));
        statusBar.getStyleClass().add("status-bar");
        root.setBottom(statusBar);
    }

    // ——— Onglet Livres ———

    private VBox creerOngletLivres() {
        TextField tfIsbn = new TextField();
        tfIsbn.setPromptText("ISBN");
        TextField tfTitre = new TextField();
        tfTitre.setPromptText("Titre");
        TextField tfAuteur = new TextField();
        tfAuteur.setPromptText("Auteur");
        TextField tfQuantite = new TextField();
        tfQuantite.setPromptText("Quantité");
        tfQuantite.setText("1");
        TextField tfRecherche = new TextField();
        tfRecherche.setPromptText("Rechercher (titre, auteur, ISBN)...");

        tableLivres = creerTableLivres();

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRechercher = new Button("Rechercher");
        Button btnTout = new Button("Tout afficher");

        btnAjouter.setOnAction(e -> {
            try {
                int quantite = Integer.parseInt(tfQuantite.getText());
                service.ajouterLivre(tfIsbn.getText(), tfTitre.getText(), tfAuteur.getText(), quantite);
                viderChamps(tfIsbn, tfTitre, tfAuteur);
                tfQuantite.setText("1");
                rafraichirLivres();
                setStatus("Livre ajouté.");
            } catch (NumberFormatException ex) {
                afficherErreur("Ajout livre", "La quantité doit être un nombre.");
            } catch (BibliothequeException ex) {
                afficherErreur("Ajout livre", ex.getMessage());
            }
        });

        btnModifier.setOnAction(e -> {
            Livre sel = tableLivres.getSelectionModel().getSelectedItem();
            if (sel == null) {
                afficherErreur("Modification", "Sélectionnez un livre.");
                return;
            }
            try {
                int quantite = Integer.parseInt(tfQuantite.getText());
                service.modifierLivre(sel.getIsbn(), tfTitre.getText(), tfAuteur.getText(), quantite);
                rafraichirLivres();
                setStatus("Livre modifié.");
            } catch (NumberFormatException ex) {
                afficherErreur("Modification", "La quantité doit être un nombre.");
            } catch (BibliothequeException ex) {
                afficherErreur("Modification", ex.getMessage());
            }
        });

        btnSupprimer.setOnAction(e -> {
            Livre sel = tableLivres.getSelectionModel().getSelectedItem();
            if (sel == null) {
                afficherErreur("Suppression", "Sélectionnez un livre.");
                return;
            }
            try {
                service.supprimerLivre(sel.getIsbn());
                rafraichirLivres();
                setStatus("Livre supprimé.");
            } catch (BibliothequeException ex) {
                afficherErreur("Suppression", ex.getMessage());
            }
        });

        btnRechercher.setOnAction(e -> {
            tableLivres.setItems(FXCollections.observableArrayList(
                    service.rechercherLivres(tfRecherche.getText())));
        });

        btnTout.setOnAction(e -> rafraichirLivres());

        tableLivres.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfIsbn.setText(n.getIsbn());
                tfTitre.setText(n.getTitre());
                tfAuteur.setText(n.getAuteur());
                tfQuantite.setText(String.valueOf(n.getQuantite()));
            }
        });

        GridPane form = new GridPane(12, 10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #252b4a; -fx-border-color: #3a4060; -fx-border-radius: 6; -fx-border-width: 1;");
        form.addRow(0, new Label("ISBN:"), tfIsbn, new Label("Titre:"), tfTitre);
        form.addRow(1, new Label("Auteur:"), tfAuteur, new Label("Quantité:"), tfQuantite);
        form.add(tfRecherche, 0, 2, 4, 1);
        HBox boutons = new HBox(12, btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnTout);
        boutons.setAlignment(Pos.CENTER_LEFT);
        boutons.setPadding(new Insets(5, 0, 0, 0));
        form.add(boutons, 0, 3, 4, 1);

        VBox box = new VBox(10, form, new Separator(), tableLivres);
        VBox.setVgrow(tableLivres, Priority.ALWAYS);
        box.setPadding(new Insets(5));
        return box;
    }

    private TableView<Livre> creerTableLivres() {
        TableView<Livre> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        TableColumn<Livre, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        TableColumn<Livre, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        TableColumn<Livre, String> colAuteur = new TableColumn<>("Auteur");
        colAuteur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuteur()));
        TableColumn<Livre, String> colQuantite = new TableColumn<>("Quantité");
        colQuantite.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantite())));
        TableColumn<Livre, String> colDispo = new TableColumn<>("Disponible");
        colDispo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getQuantite() > 0 ? "Oui" : "Non"));
        table.getColumns().addAll(colIsbn, colTitre, colAuteur, colQuantite, colDispo);
        return table;
    }

    // ——— Onglet Adhérents ———

    private VBox creerOngletAdherents() {
        TextField tfId = new TextField();
        tfId.setPromptText("ID");
        TextField tfNom = new TextField();
        tfNom.setPromptText("Nom");
        TextField tfPrenom = new TextField();
        tfPrenom.setPromptText("Prénom");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email");
        TextField tfRecherche = new TextField();
        tfRecherche.setPromptText("Rechercher...");

        tableAdherents = new TableView<>();
        TableColumn<Adherent, String> c1 = new TableColumn<>("ID");
        c1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        TableColumn<Adherent, String> c2 = new TableColumn<>("Nom");
        c2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        TableColumn<Adherent, String> c3 = new TableColumn<>("Prénom");
        c3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        TableColumn<Adherent, String> c4 = new TableColumn<>("Email");
        c4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        c4.setPrefWidth(200);
        tableAdherents.getColumns().addAll(c1, c2, c3, c4);

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRechercher = new Button("Rechercher");

        btnAjouter.setOnAction(e -> {
            try {
                service.ajouterAdherent(tfId.getText(), tfNom.getText(), tfPrenom.getText(), tfEmail.getText());
                viderChamps(tfId, tfNom, tfPrenom, tfEmail);
                rafraichirAdherents();
                if (refreshCombosEmprunts != null) {
                    refreshCombosEmprunts.run();
                }
                setStatus("Adhérent ajouté.");
            } catch (BibliothequeException ex) {
                afficherErreur("Ajout adhérent", ex.getMessage());
            }
        });

        btnModifier.setOnAction(e -> {
            Adherent sel = tableAdherents.getSelectionModel().getSelectedItem();
            if (sel == null) {
                afficherErreur("Modification", "Sélectionnez un adhérent.");
                return;
            }
            try {
                service.modifierAdherent(sel.getId(), tfNom.getText(), tfPrenom.getText(), tfEmail.getText());
                rafraichirAdherents();
                if (refreshCombosEmprunts != null) {
                    refreshCombosEmprunts.run();
                }
                setStatus("Adhérent modifié.");
            } catch (BibliothequeException ex) {
                afficherErreur("Modification", ex.getMessage());
            }
        });

        btnSupprimer.setOnAction(e -> {
            Adherent sel = tableAdherents.getSelectionModel().getSelectedItem();
            if (sel == null) {
                afficherErreur("Suppression", "Sélectionnez un adhérent.");
                return;
            }
            try {
                service.supprimerAdherent(sel.getId());
                rafraichirAdherents();
                if (refreshCombosEmprunts != null) {
                    refreshCombosEmprunts.run();
                }
                setStatus("Adhérent supprimé.");
            } catch (BibliothequeException ex) {
                afficherErreur("Suppression", ex.getMessage());
            }
        });

        btnRechercher.setOnAction(e -> tableAdherents.setItems(FXCollections.observableArrayList(
                service.rechercherAdherents(tfRecherche.getText()))));

        tableAdherents.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tfId.setText(n.getId());
                tfNom.setText(n.getNom());
                tfPrenom.setText(n.getPrenom());
                tfEmail.setText(n.getEmail());
            }
        });

        GridPane form = new GridPane(12, 10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #252b4a; -fx-border-color: #3a4060; -fx-border-radius: 6; -fx-border-width: 1;");
        form.addRow(0, new Label("ID:"), tfId, new Label("Nom:"), tfNom);
        form.addRow(1, new Label("Prénom:"), tfPrenom, new Label("Email:"), tfEmail);
        HBox boutons = new HBox(12, btnAjouter, btnModifier, btnSupprimer, btnRechercher);
        boutons.setPadding(new Insets(5, 0, 0, 0));
        form.add(boutons, 0, 2, 4, 1);
        form.add(tfRecherche, 0, 3, 4, 1);

        VBox box = new VBox(15, form, new Separator(), tableAdherents);
        box.setPadding(new Insets(10));
        VBox.setVgrow(tableAdherents, Priority.ALWAYS);
        return box;
    }

    // ——— Onglet Emprunts ———

    private VBox creerOngletEmprunts() {
        ComboBox<String> cbLivre = new ComboBox<>();
        cbLivre.setPromptText("Livre (ISBN)");
        cbLivre.setPrefWidth(280);
        ComboBox<String> cbAdherent = new ComboBox<>();
        cbAdherent.setPromptText("Adhérent");
        cbAdherent.setPrefWidth(280);

        tableEmprunts = new TableView<>();
        TableColumn<EmpruntRow, String> c1 = new TableColumn<>("ISBN");
        c1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isbn));
        TableColumn<EmpruntRow, String> c2 = new TableColumn<>("Adhérent");
        c2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().adherent));
        TableColumn<EmpruntRow, String> c3 = new TableColumn<>("Date emprunt");
        c3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dateEmprunt));
        TableColumn<EmpruntRow, String> c4 = new TableColumn<>("Statut");
        c4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().statut));
        tableEmprunts.getColumns().addAll(c1, c2, c3, c4);

        refreshCombosEmprunts = () -> {
            cbLivre.getItems().setAll(service.listerLivres().stream()
                    .filter(l -> l.getQuantite() > 0)
                    .map(l -> l.getIsbn() + " — " + l.getTitre())
                    .toList());
            cbAdherent.getItems().setAll(service.listerAdherents().stream()
                    .map(a -> a.getId() + " — " + a.getNomComplet())
                    .toList());
        };

        Button btnEmprunter = new Button("Emprunter");
        Button btnRetourner = new Button("Retourner");
        btnEmprunter.setOnAction(e -> {
            String livreSel = cbLivre.getValue();
            String adhSel = cbAdherent.getValue();
            if (livreSel == null || adhSel == null) {
                afficherErreur("Emprunt", "Sélectionnez un livre et un adhérent.");
                return;
            }
            String isbn = livreSel.split(" — ")[0];
            String idAdh = adhSel.split(" — ")[0];
            try {
                service.emprunterLivre(isbn, idAdh);
                rafraichirEmprunts();
                rafraichirLivres();
                refreshCombosEmprunts.run();
                setStatus("Emprunt enregistré.");
            } catch (BibliothequeException ex) {
                afficherErreur("Emprunt", ex.getMessage());
            }
        });

        btnRetourner.setOnAction(e -> {
            EmpruntRow row = tableEmprunts.getSelectionModel().getSelectedItem();
            if (row == null || !"En cours".equals(row.statut)) {
                afficherErreur("Retour", "Sélectionnez un emprunt en cours.");
                return;
            }
            try {
                service.retournerLivre(row.isbn);
                rafraichirEmprunts();
                rafraichirLivres();
                refreshCombosEmprunts.run();
                setStatus("Retour enregistré.");
            } catch (BibliothequeException ex) {
                afficherErreur("Retour", ex.getMessage());
            }
        });

        HBox actions = new HBox(15, new Label("📖 Livre:"), cbLivre, new Label("👤 Adhérent:"), cbAdherent,
                btnEmprunter, btnRetourner);
        actions.setPadding(new Insets(20));
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setStyle("-fx-background-color: #252b4a; -fx-border-color: #3a4060; -fx-border-radius: 6; -fx-border-width: 1;");

        refreshCombosEmprunts.run();
        rafraichirEmprunts();

        VBox box = new VBox(15, actions, new Separator(), tableEmprunts);
        box.setPadding(new Insets(10));
        VBox.setVgrow(tableEmprunts, Priority.ALWAYS);
        return box;
    }

    // ——— Onglet Fichiers ———

    private VBox creerOngletFichiers() {
        TextArea rapport = new TextArea();
        rapport.setEditable(false);
        rapport.setPrefRowCount(12);

        Button btnSauvegarder = new Button("Sauvegarder (CSV)");
        Button btnCharger = new Button("Charger depuis fichiers");
        Button btnRapport = new Button("Générer rapport");
        Button btnDemo = new Button("Charger données démo");

        btnSauvegarder.setOnAction(e -> {
            try {
                service.sauvegarder();
                setStatus("Données sauvegardées dans le dossier data/.");
                new Alert(Alert.AlertType.INFORMATION, "Sauvegarde réussie dans data/").showAndWait();
            } catch (BibliothequeException ex) {
                afficherErreur("Sauvegarde", ex.getMessage());
            }
        });

        btnCharger.setOnAction(e -> {
            try {
                service.charger();
                rafraichirTout();
                setStatus("Données chargées.");
            } catch (BibliothequeException ex) {
                afficherErreur("Chargement", ex.getMessage());
            }
        });

        btnRapport.setOnAction(e -> rapport.setText(service.genererRapport()));

        btnDemo.setOnAction(e -> {
            service.chargerDonneesDemo();
            rafraichirTout();
            setStatus("Données de démonstration chargées.");
        });

        Label labelPersistence = new Label("💾 Persistance fichier (CSV)");
        labelPersistence.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #a8d8ff;");
        
        HBox bBox = new HBox(12, btnSauvegarder, btnCharger, btnRapport, btnDemo);
        bBox.setPadding(new Insets(10));
        bBox.setStyle("-fx-background-color: #252b4a; -fx-border-radius: 6; -fx-padding: 10;");
        
        Label labelRapport = new Label("📊 Rapport");
        labelRapport.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #a8d8ff;");

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.getChildren().addAll(
                labelPersistence,
                bBox,
                labelRapport,
                rapport);
        VBox.setVgrow(rapport, Priority.ALWAYS);
        return box;
    }

    // ——— Utilitaires UI ———

    private void rafraichirTout() {
        rafraichirLivres();
        rafraichirAdherents();
        rafraichirEmprunts();
    }

    private void rafraichirLivres() {
        tableLivres.setItems(FXCollections.observableArrayList(service.listerLivres()));
        tableLivres.refresh();
    }

    private void rafraichirAdherents() {
        tableAdherents.setItems(FXCollections.observableArrayList(service.listerAdherents()));
        tableAdherents.refresh();
    }

    private void rafraichirEmprunts() {
        java.util.List<EmpruntRow> rows = new java.util.ArrayList<>();
        for (Map.Entry<String, Emprunt> e : service.getEmpruntsEnCours().entrySet()) {
            Emprunt emp = e.getValue();
            String nomAdh = service.listerAdherents().stream()
                    .filter(a -> a.getId().equals(emp.getIdAdherent()))
                    .map(Adherent::getNomComplet)
                    .findFirst()
                    .orElse(emp.getIdAdherent());
            rows.add(new EmpruntRow(emp.getIsbnLivre(), nomAdh,
                    emp.getDateEmprunt().toString(), "En cours"));
        }
        tableEmprunts.setItems(FXCollections.observableArrayList(rows));
        tableEmprunts.refresh();
    }

    private void viderChamps(TextField... champs) {
        for (TextField tf : champs) {
            tf.clear();
        }
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    public void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        setStatus("Erreur : " + message);
    }

    /** Ligne affichée dans le tableau des emprunts */
    public static class EmpruntRow {
        final String isbn;
        final String adherent;
        final String dateEmprunt;
        final String statut;

        EmpruntRow(String isbn, String adherent, String dateEmprunt, String statut) {
            this.isbn = isbn;
            this.adherent = adherent;
            this.dateEmprunt = dateEmprunt;
            this.statut = statut;
        }
    }
}
