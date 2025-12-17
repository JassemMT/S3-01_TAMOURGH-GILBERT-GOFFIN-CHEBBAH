package com.example.trello;

import com. example.trello.Modele.Modele;
import com.example.trello. Modele.Tache;
import com.example.trello.Modele.TacheSimple;
import com.example.trello. Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale qui lance l'application
 * Respecte le pattern MVC
 */
public class App extends Application {

    private Modele modele;

    @Override
    public void start(Stage primaryStage) {
        // Initialise le modèle
        modele = new Modele();

        // Ajoute quelques tâches d'exemple
        ajouterTachesExempleVueListe();

        // Initialise la vue Kanban
        VueListe vueListe = new VueListe(modele);

        Scene scene = new Scene(vueListe, 900, 600);

        // Configure la fenêtre principale
        primaryStage.setTitle("Trello - Vue Liste");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Ajoute quelques tâches d'exemple pour démonstration
     */
    private void ajouterTachesExempleVueKanban() {
        // Tâches "À faire"
        Tache tache1 = new TacheSimple("Tâche 1", "Première tâche à faire");
        tache1.setColonne("À faire");
        tache1.setEtat(Tache.ETAT_A_FAIRE);
        modele.ajouterTache(tache1);

        Tache tache2 = new TacheSimple("Tâche 2", "Deuxième tâche à faire");
        tache2.setColonne("À faire");
        tache2.setEtat(Tache.ETAT_A_FAIRE);
        modele.ajouterTache(tache2);

        // Tâche "En cours"
        Tache tache3 = new TacheSimple("Tâche 2", "Tâche en cours de réalisation");
        tache3.setColonne("En cours");
        tache3.setEtat(Tache.ETAT_EN_COURS);
        modele.ajouterTache(tache3);

        // Tâche "Terminé"
        Tache tache4 = new TacheSimple("Tâche 1", "Tâche terminée");
        tache4.setColonne("Terminé");
        tache4.setEtat(Tache.ETAT_TERMINE);
        modele.ajouterTache(tache4);
    }

    /**
     * Ajoute quelques tâches de test pour démonstration
     */
    private void ajouterTachesExempleVueListe() {
        // Tâches pour lundi
        TacheSimple t1 = new TacheSimple("Réunion d'équipe", "Réunion hebdomadaire avec l'équipe projet");
        t1.setJour("lundi");
        modele.ajouterTache(t1);

        TacheSimple t2 = new TacheSimple("Planification sprint", "Définir les objectifs de la semaine");
        t2.setJour("lundi");
        modele.ajouterTache(t2);

        // Tâches pour mardi
        TacheSimple t3 = new TacheSimple("tâche 1", "10/01/2026");
        t3.setJour("mardi");
        modele.ajouterTache(t3);

        // Tâches pour mercredi
        TacheSimple t4 = new TacheSimple("Code review", "Relire les PRs en attente");
        t4.setJour("mercredi");
        modele.ajouterTache(t4);

        TacheSimple t5 = new TacheSimple("Tests unitaires", "Compléter la couverture de tests");
        t5.setJour("mercredi");
        modele.ajouterTache(t5);

        // Tâche pour jeudi
        TacheSimple t6 = new TacheSimple("Démo client", "Présenter les nouvelles fonctionnalités");
        t6.setJour("jeudi");
        modele.ajouterTache(t6);

        // Tâches pour vendredi
        TacheSimple t7 = new TacheSimple("tâche 2", "15/01/2026");
        t7.setJour("vendredi");
        modele.ajouterTache(t7);

        TacheSimple t8 = new TacheSimple("Rétrospective", "Bilan de la semaine écoulée");
        t8.setJour("vendredi");
        modele.ajouterTache(t8);

        // Tâche pour samedi
        TacheSimple t9 = new TacheSimple("tâche 3", "17/01/2026");
        t9.setJour("samedi");
        modele.ajouterTache(t9);
    }

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}