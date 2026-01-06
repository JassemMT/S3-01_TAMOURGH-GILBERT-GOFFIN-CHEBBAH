package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite; // Import
import com.example.trello.Modele.TacheSimple;    // Import
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
        primaryStage.setTitle("Trello - Vue Liste (Composite)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initDonneesTest(Modele modele) {
        // Tâches simples isolées
        TacheSimple t1 = new TacheSimple("Réunion Projet", "Discuter budget", "Lundi", "Principal", 2);
        TacheSimple t2 = new TacheSimple("Dev Backend", "API Rest", "Lundi", "En cours", 4);
        t2.setEtat(Tache.ETAT_EN_COURS);

        // Hiérarchie : Parent (Composite) -> Enfant (Simple)
        TacheComposite parent = new TacheComposite("Interface Graphique", "JavaFX", "Mardi", "Principal", 10);
        TacheSimple enfant = new TacheSimple("Vue Liste", "Implémentation", "Mardi", "A faire", 5);

        // On lie l'enfant au parent
        parent.ajouterEnfant(enfant);

        // Ajout au modèle
        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(parent);
        modele.ajouterTache(enfant); // On ajoute aussi l'enfant pour qu'il soit géré par le modèle
    }

    public static void main(String[] args) {
        launch(args);
    }
}