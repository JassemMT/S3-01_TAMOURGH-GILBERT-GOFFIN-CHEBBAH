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
        if (modele == null) {
            System.out.println("Aucune sauvegarde trouvée, création d'un nouveau modèle.");
            modele = new Modele();
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
        // CORRECTION : Utilisation de LocalDate au lieu de String
        LocalDate aujourdhui = LocalDate.now();
        LocalDate demain = aujourdhui.plusDays(1);

        // Tâche simple pour aujourd'hui
        TacheSimple t1 = new TacheSimple(
                "Réunion Projet",
                "Discuter budget",
                aujourdhui, // <-- LocalDate
                "Principal",
                2
        );

        TacheSimple t2 = new TacheSimple(
                "Dev Backend",
                "API Rest",
                aujourdhui, // <-- LocalDate
                "En cours",
                4
        );
        t2.setEtat(Tache.ETAT_EN_COURS);

        // Tâche Composite (Dossier) pour demain
        TacheComposite parent = new TacheComposite(
                "Interface Graphique",
                "JavaFX",
                demain, // <-- LocalDate
                "Principal",
                10
        );

        // L'enfant est une TacheSimple (même jour que le parent ici)
        TacheSimple enfant = new TacheSimple(
                "Vue Liste",
                "Implémentation",
                demain, // <-- LocalDate
                "A faire",
                5
        );

        // Ajout de l'enfant au parent
        parent.ajouterEnfant(enfant);

        // Ajout au modèle
        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(parent);
        modele.ajouterTache(enfant);
    }

    public static void main(String[] args) {
        launch(args);
    }
}