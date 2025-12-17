package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurSauvegarderModif;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Fenêtre d'édition d'une tâche
 */
public class VueEditeurTache {
    private Tache tache;
    private Stage stage;
    private TextField champTitre;
    private TextArea champCommentaire;
    private Label labelDateDebut;
    private Label labelDateFin;
    private Label labelEtat;

    public VueEditeurTache(Tache tache) {
        this.tache = tache;
        initialiserInterface();
    }

    private void initialiserInterface() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Éditer la tâche");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Libellé
        Label lblTitre = new Label("Titre:");
        champTitre = new TextField(tache.getLibelle());
        champTitre.setPrefWidth(300);
        grid.add(lblTitre, 0, 0);
        grid.add(champTitre, 1, 0);

        // État
        Label lblEtatLabel = new Label("État:");
        labelEtat = new Label(tache.getEtat());
        grid.add(lblEtatLabel, 0, 1);
        grid.add(labelEtat, 1, 1);

        // Dates
        Label lblDateDebut = new Label("Date début:");
        labelDateDebut = new Label(tache.getDateDebut());
        grid.add(lblDateDebut, 0, 2);
        grid.add(labelDateDebut, 1, 2);

        Label lblDateFin = new Label("Date fin:");
        labelDateFin = new Label(tache.getDateFin());
        grid.add(lblDateFin, 0, 3);
        grid.add(labelDateFin, 1, 3);

        // Commentaire
        Label lblCommentaire = new Label("Commentaire:");
        champCommentaire = new TextArea(tache.getCommentaire());
        champCommentaire.setPrefRowCount(5);
        champCommentaire.setPrefWidth(300);
        grid.add(lblCommentaire, 0, 4);
        grid.add(champCommentaire, 1, 4);

        // Afficher les sous-tâches si composite
        if (tache instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) tache;
            Label lblSousTaches = new Label("Sous-tâches:");
            ListView<String> listeSousTaches = new ListView<>();
            for (Tache enfant : composite.getEnfants()) {
                listeSousTaches.getItems().add(enfant.getLibelle());
            }
            listeSousTaches.setPrefHeight(100);
            grid.add(lblSousTaches, 0, 5);
            grid.add(listeSousTaches, 1, 5);
        }

        // Boutons
        Button btnSauvegarder = new Button("Sauvegarder");
        Button btnAnnuler = new Button("Annuler");

        btnAnnuler.setOnAction(e -> stage.close());

        HBox boutons = new HBox(10, btnSauvegarder, btnAnnuler);
        grid.add(boutons, 1, 6);

        Scene scene = new Scene(grid);
        stage.setScene(scene);

        // Note: Le contrôleur sera ajouté depuis l'extérieur
        // car il nécessite le modèle
        btnSauvegarder.setId("btnSauvegarder");
    }

    public void afficher() {
        stage.showAndWait();
    }

    public void fermer() {
        stage.close();
    }

    public String getChampTitre() {
        return champTitre.getText();
    }

    public String getChampCommentaire() {
        return champCommentaire.getText();
    }

    public Stage getStage() {
        return stage;
    }
}