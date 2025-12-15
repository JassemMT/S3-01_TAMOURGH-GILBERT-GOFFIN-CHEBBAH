package com.example.trello;

import com.example.trello. Modele.Modele;
import com.example.trello. Modele.Tache;
import com.example.trello.Modele.TacheSimple;
import com.example. trello.Vue.VueKanban;
import javafx. application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout. StackPane;
import javafx. stage.Stage;

/**
 * Classe principale de l'application Trello
 */
public class App extends Application {

    private Modele modele;
    private VueKanban vueKanban;

    @Override
    public void start(Stage primaryStage) {
        // Initialiser le modèle
        modele = new Modele();

        // Ajouter des données d'exemple
        ajouterDonneesExemple();

        // Créer la vue Kanban
        vueKanban = new VueKanban(modele);

        // Créer le conteneur principal
        StackPane root = new StackPane();
        root.getChildren().add(vueKanban);

        /*
        // Ajouter le bouton flottant en bas à droite (affichage uniquement)
        Button btnAjoutGlobal = creerBoutonFlottant();
        StackPane.setAlignment(btnAjoutGlobal, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(btnAjoutGlobal, new Insets(20));
        root.getChildren().add(btnAjoutGlobal);
        */


        // TODO:  Le contrôleur devra gérer l'action du bouton

        // Créer et configurer la scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Trello - Vue Kanban");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crée le bouton + flottant en bas à droite (affichage uniquement)
     */
    private Button creerBoutonFlottant() {
        Button btn = new Button("+");
        btn.setStyle(
                "-fx-background-color: #606060; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 35; " +
                        "-fx-min-width: 70px; " +
                        "-fx-min-height: 70px; " +
                        "-fx-max-width: 70px; " +
                        "-fx-max-height: 70px;"
        );

        return btn;
    }

    /**
     * Ajoute des données d'exemple dans le modèle pour tester l'affichage
     */
    private void ajouterDonneesExemple() {
        // Tâches dans "À faire"
        for (int i = 1; i <= 7; i++) {
            Tache tache = new TacheSimple("tâche " + i, "Description de la tâche " + i);
            tache.setColonne("À faire");
            tache. setEtat(Tache. ETAT_A_FAIRE);
            modele.ajouterTache(tache);
        }

        // Tâche dans "En cours"
        Tache tacheEnCours = new TacheSimple("tâche 2", "En cours de réalisation");
        tacheEnCours.setColonne("En cours");
        tacheEnCours.setEtat(Tache.ETAT_EN_COURS);
        modele.ajouterTache(tacheEnCours);

        // Tâche dans "Terminé"
        Tache tacheTerminee = new TacheSimple("tâche 1", "Tâche complétée");
        tacheTerminee.setColonne("Terminé");
        tacheTerminee.setEtat(Tache.ETAT_TERMINE);
        modele.ajouterTache(tacheTerminee);
    }

    public static void main(String[] args) {
        launch(args);
    }
}