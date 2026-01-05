package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueGantt;
import com.example.trello.Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class AppTrello extends Application {

    private Modele modele;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation unique du modèle
        modele = new Modele();
        initDonneesTest(modele);

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


        // Pour choisir une vue spécifique en par défaut :
        // tabPane.getSelectionModel().select(tabKanban);

        // création de la scène
        Scene scene = new Scene(tabPane, 1200, 700);
        primaryStage.setTitle("Trello Clone - Multi Vues");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // fonction permettant de créer des données factices pour faire des tests
    private void initDonneesTest(Modele modele) {
        Tache t1 = new Tache("Réunion Projet", "Discuter budget", "Lundi", "Principal", 2);
        Tache t2 = new Tache("Dev Backend", "API Rest", "Lundi", "En cours", 4);
        t2.setEtat(Tache.ETAT_EN_COURS);

        Tache parent = new Tache("Interface Graphique", "JavaFX", "Mardi", "Principal", 10);
        Tache enfant = new Tache("Vue Liste", "Implémentation", "Mardi", "A faire", 8);
        Tache enfant1 = new Tache("Vue Liste 2", "Implémentation", "Mardi", "A faire", 4);

        parent.ajouterEnfant(enfant);
        enfant.ajouterEnfant(enfant1);
        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(parent);
        modele.ajouterTache(enfant);
    }

    public static void main(String[] args) {
        launch(args);
    }
}