package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple; // Import
import com.example.trello.Vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainKanban extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Création du Modèle
        Modele modele = new Modele();

        // 2. Ajout de données de test
        // On utilise TacheSimple ici car ces exemples n'ont pas de sous-tâches définies dans ce fichier

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
                "Principal", // Note: "À faire" n'est pas une colonne par défaut du modèle, je mets Principal ou une colonne existante
                1
        );

        TacheSimple t3 = new TacheSimple(
                "Réunion client",
                "Présentation du prototype",
                "Vendredi",
                "Terminé",
                0
        );

        // On ajoute les différentes tâches au modèle
        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(t3);

        // 3. Création de la Vue
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