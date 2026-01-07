package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.ModeleRepository;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Vue.VueGantt;
import com.example.trello.Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.time.LocalDate; // <--- Import indispensable

public class AppTrello extends Application {

    private Modele modele;
    private ModeleRepository repository;

    @Override
    public void start(Stage primaryStage) {
        // 1. Chargement de la sauvegarde
        repository = new ModeleRepository("Sauvegarde/app.save");
        modele = repository.load();

        // 2. Si aucune sauvegarde n'existe (premier lancement), on crée des données de test
        if (modele == null || modele.getTaches().isEmpty()) {
            System.out.println("Sauvegarde absente ou vide, création des données de test.");

            // Si le repository a renvoyé null, on doit instancier.
            // S'il a renvoyé un objet vide, on peut le réutiliser ou en refaire un.
            if (modele == null) {
                modele = new Modele();
            }
            initDonneesTest(modele);
        }

        // Création des vues
        VueListe vueListe = new VueListe(modele);
        VueKanban vueKanban = new VueKanban(modele);
        VueGantt vueGantt = new VueGantt(modele);

        // TabPane pour gérer les trois vues
        TabPane tabPane = new TabPane();

        Tab tabListe = new Tab("Vue Liste");
        tabListe.setContent(vueListe);
        tabListe.setClosable(false);

        Tab tabKanban = new Tab("Vue Kanban");
        tabKanban.setContent(vueKanban);
        tabKanban.setClosable(false);

        Tab tabGantt = new Tab("Vue Gantt");
        tabGantt.setContent(vueGantt);
        tabGantt.setClosable(false);

        tabPane.getTabs().addAll(tabListe, tabKanban, tabGantt);

        // Création de la scène
        Scene scene = new Scene(tabPane, 1200, 700);
        primaryStage.setTitle("Trello Clone - LocalDate & Composite");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Sauvegarde à la fermeture
        if (repository != null && modele != null) {
            modele.exit(repository);
        }
        super.stop();
    }

    // Fonction permettant de créer des données factices
    private void initDonneesTest(Modele modele) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate demain = aujourdhui.plusDays(1);
        // On supprime les colonnes par défaut du constructeur du Modele
        modele.supprimerColonne("Principal");
        modele.supprimerColonne("En cours");
        modele.supprimerColonne("Terminé");

        String[] colonnes = {"O", "P", "Q", "R", "T", "X", "Y", "Z"};
        for (String col : colonnes) {
            modele.ajouterColonne(col);
        }




        String[] autresColonnes = {"O", "P", "R", "T", "Y", "Z"};

        for (String col : autresColonnes) {
            for (int i = 1; i <= 3; i++) {
                TacheSimple t = new TacheSimple(
                        "Tache " + col + "-" + i,
                        "Remplissage",
                        LocalDate.now().plusDays(i),
                        col,
                        2
                );
                modele.ajouterTache(t);
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}