package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Modele.Tache; // Import nécessaire pour les constantes d'état
import com.example.trello.Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class AppTrello extends Application {

    private Modele modele;
    private ModeleRepository repository;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation unique du modèle
        repository = new ModeleRepository("Sauvegarde/app.save");
        modele = repository.load();


        // Création des vues Liste et Kanban + Gantt
        VueListe vueListe = new VueListe(modele);
        VueKanban vueKanban = new VueKanban(modele);
        VueGantt vueGantt = new VueGantt(modele);

        // TabPane pour gérer les trois vues
        // TabPane est un conteneur qui agit comme un navigateur avec différents onglets
        TabPane tabPane = new TabPane();

        Tab tabListe = new Tab("Vue Liste");
        tabListe.setContent(vueListe);
        // empêche l'user de fermer l'onglet (sinon impossible de l'ouvrir à nouveau)
        tabListe.setClosable(false);

        Tab tabKanban = new Tab("Vue Kanban");
        tabKanban.setContent(vueKanban);
        tabKanban.setClosable(false);

        Tab tabGantt = new Tab("Vue Gantt");
        tabGantt.setContent(vueGantt);
        tabGantt.setClosable(false);

        // ajout des onglets dans le tabpane
        tabPane.getTabs().addAll(tabListe, tabKanban, tabGantt);

        // Ajout des onglets dans le conteneur
        tabPane.getTabs().addAll(tabListe, tabKanban, tabGantt);

        // création de la scène
        Scene scene = new Scene(tabPane, 1200, 700);
        primaryStage.setTitle("Trello Clone - Multi Vues");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (repository != null && modele != null) {
            modele.exit(repository);
        }
        super.stop();
    }

    // fonction permettant de créer des données factices pour faire des tests
    private void initDonneesTest(Modele modele) {
        // On crée tout en "Simple" au début
        TacheSimple t1 = new TacheSimple("Réunion", "Budget", "Lundi", "Principal", 2);
        TacheSimple t2 = new TacheSimple("Dev Backend", "API", "Lundi", "En cours", 4);

        // Futur parent (créé comme simple)
        TacheSimple parent = new TacheSimple("Interface Graphique", "JavaFX", "Mardi", "Principal", 10);

        // Enfant
        TacheSimple enfant = new TacheSimple("Vue Liste", "Implémentation", "Mardi", "A faire", 5);

        // Ajout au modèle
        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(parent);


        TacheComposite parentPromu = modele.promouvoirEnComposite(parent);
        parentPromu.ajouterEnfant(enfant);

        modele.ajouterTache(enfant);
    }

    public static void main(String[] args) {
        launch(args);
    }
}