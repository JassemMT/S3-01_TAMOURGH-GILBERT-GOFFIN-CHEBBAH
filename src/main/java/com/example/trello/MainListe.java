package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainListe extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialisation du Modèle
        Modele modele = new Modele();

        // Données de test
        initDonneesTest(modele);

        // Création de la Vue
        VueListe vueListe = new VueListe(modele);

        // Lancement
        Scene scene = new Scene(vueListe, 900, 600);
        primaryStage.setTitle("Trello - Vue Liste");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initDonneesTest(Modele modele) {
        Tache t1 = new Tache("Réunion Projet", "Discuter budget", "Lundi", "Principal", 2);
        Tache t2 = new Tache("Dev Backend", "API Rest", "Lundi", "En cours", 4);
        t2.setEtat(Tache.ETAT_EN_COURS);

        Tache parent = new Tache("Interface Graphique", "JavaFX", "Mardi", "Principal", 10);
        Tache enfant = new Tache("Vue Liste", "Implémentation", "Mardi", "A faire", 5);
        parent.ajouterEnfant(enfant);

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(parent);
        modele.ajouterTache(enfant);
    }

    public static void main(String[] args) {
        launch(args);
    }
}