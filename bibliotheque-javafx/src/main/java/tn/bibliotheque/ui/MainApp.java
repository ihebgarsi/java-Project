package tn.bibliotheque.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.bibliotheque.service.BibliothequeService;

import java.nio.file.Path;

/**
 * Point d'entrée JavaFX — Gestion de bibliothèque.
 */
public class MainApp extends Application {

    private static BibliothequeService service;

    public static BibliothequeService getService() {
        return service;
    }

    @Override
    public void start(Stage primaryStage) {
        Path dataDir = Path.of(System.getProperty("user.dir"), "data");
        service = new BibliothequeService(dataDir);

        try {
            service.charger();
        } catch (Exception e) {
            service.chargerDonneesDemo();
        }

        if (service.listerLivres().isEmpty()) {
            service.chargerDonneesDemo();
        }

        MainController controller = new MainController(service);
        Scene scene = new Scene(controller.getRoot(), 1000, 650);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("Gestion de Bibliothèque — Projet Java");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            try {
                service.sauvegarder();
            } catch (Exception ex) {
                controller.afficherErreur("Sauvegarde à la fermeture", ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
