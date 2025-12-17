package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainKanban extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Création du Modèle
        Modele modele = new Modele();

        // 2. Ajout de données de test adaptées au nouveau constructeur
        // Signature : (Libellé, Commentaire, Jour, Colonne, Durée)

        TacheSimple t1 = new TacheSimple(
                "Finir le diagramme UML",
                "Vérifier les relations entre les classes",
                "Lundi",
                "En cours",
                2
        );

        TacheSimple t2 = new TacheSimple(
                "Coder les contrôleurs",
                "Implémenter la logique de sauvegarde",
                "Mardi",
                "À faire",
                1
        );

        TacheSimple t3 = new TacheSimple(
                "Réunion client",
                "Présentation du prototype",
                "Vendredi",
                "Terminé",
                0
        );

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(t3);

        // 3. Création de la Vue (VueKanban est un BorderPane)
        VueKanban root = new VueKanban(modele);

        // 4. Configuration de la scène
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Projet Kanban - Gestion par Jours");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}