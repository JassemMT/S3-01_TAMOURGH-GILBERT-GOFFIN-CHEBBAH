package com.example.trello;

import com. example.trello.Modele.Modele;
import com.example.trello. Modele.Tache;
import com.example.trello.Modele.TacheSimple;
import com.example.trello. Vue.VueKanban;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale qui lance l'application
 * Respecte le pattern MVC
 */
public class App extends Application {

    private Modele modele;
    private VueKanban vueKanban;

    @Override
    public void start(Stage primaryStage) {
        // Initialise le modèle
        modele = new Modele();
        modele.setTypeVue(Modele.VUE_KANBAN);

        // Ajoute quelques tâches d'exemple
        ajouterTachesExemple();

        // Initialise la vue Kanban
        vueKanban = new VueKanban(modele);

        // Configure la fenêtre principale
        primaryStage.setTitle("Trello - Vue Kanban");
        primaryStage.setScene(vueKanban.getScene());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Ajoute quelques tâches d'exemple pour démonstration
     */
    private void ajouterTachesExemple() {
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
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}