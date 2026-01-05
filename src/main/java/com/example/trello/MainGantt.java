package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueGantt;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainGantt extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialisation du Modèle
        Modele modele = new Modele();

        // 2. Création de données de test (Scénario "Semaine de Projet")
        initDonneesTest(modele);

        // 3. Création de la Vue Gantt
        VueGantt vueGantt = new VueGantt(modele);

        // 4. Configuration de la fenêtre
        Scene scene = new Scene(vueGantt, 1000, 600);
        primaryStage.setTitle("Trello - Vue Gantt");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initDonneesTest(Modele modele) {
        // Lundi : Lancement
        Tache kickOff = new Tache("Kick-off Meeting", "Présentation équipe", "Lundi", "Terminé", 2);
        kickOff.setColor("#FFCDD2"); // Rouge clair
        kickOff.setEtat(Tache.ETAT_TERMINE);

        // Mardi : Conception
        Tache analyse = new Tache("Analyse des besoins", "Rédaction specs", "Mardi", "En cours", 4);
        analyse.setColor("#BBDEFB"); // Bleu clair
        analyse.setEtat(Tache.ETAT_EN_COURS);

        // Mercredi : Développement (Avec sous-tâches)
        Tache devGlobal = new Tache("Développement Core", "Structure du projet", "Mercredi", "En cours", 7);
        devGlobal.setColor("#C8E6C9"); // Vert clair
        devGlobal.setEtat(Tache.ETAT_EN_COURS);

        // Sous-tâches du mercredi
        Tache devDatabase = new Tache("Setup DB", "MySQL Config", "Mercredi", "Terminé", 2);
        devDatabase.setColor("#E1BEE7"); // Violet
        devGlobal.ajouterEnfant(devDatabase);

        Tache devApi = new Tache("Création API", "Routes REST", "Mercredi", "A faire", 5);
        devApi.setColor("#FFECB3"); // Jaune
        devGlobal.ajouterEnfant(devApi);

        // Jeudi & Vendredi : Tests et Déploiement
        Tache tests = new Tache("Tests Unitaires", "JUnit 5", "Jeudi", "A faire", 3);
        tests.setColor("#B2DFDB"); // Cyan

        Tache deploy = new Tache("Déploiement Prod", "AWS Lambda", "Vendredi", "A faire", 1);
        deploy.setColor("#FFCCBC"); // Orange

        // Samedi : Urgent (Exemple de weekend)
        Tache hotfix = new Tache("Hotfix Sécurité", "Patch critique", "Samedi", "A faire", 2);
        hotfix.setColor("#CFD8DC"); // Gris

        // Ajout au modèle
        modele.ajouterTache(kickOff);
        modele.ajouterTache(analyse);

        modele.ajouterTache(devGlobal);
        modele.ajouterTache(devDatabase);
        modele.ajouterTache(devApi);

        modele.ajouterTache(tests);
        modele.ajouterTache(deploy);
        modele.ajouterTache(hotfix);
    }

    public static void main(String[] args) {
        launch(args);
    }
}