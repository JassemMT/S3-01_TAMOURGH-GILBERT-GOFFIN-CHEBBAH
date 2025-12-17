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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class VueEditeurTache {
    private Tache tache;
    private Modele modele;
    private Stage stage;

    // Composants graphiques (champs)
    private TextField champTitre;
    private TextArea champCommentaire;
    private ComboBox<String> comboEtat;
    private ComboBox<String> comboJour; // Remplace les DatePicker
    private Spinner<Integer> spinnerDuree;
    private ColorPicker colorPicker;

    public VueEditeurTache(Tache tache, Modele modele) {
        this.tache = tache;
        this.modele = modele;
        initialiserInterface();
    }

    private void initialiserInterface() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Éditer : " + tache.getLibelle());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // 1. Titre
        grid.add(new Label("Titre:"), 0, 0);
        champTitre = new TextField(tache.getLibelle());
        grid.add(champTitre, 1, 0);

        // 2. État
        grid.add(new Label("État:"), 0, 1);
        comboEtat = new ComboBox<>();
        comboEtat.getItems().addAll("À faire", "En cours", "Terminé", "Archivé");
        comboEtat.setValue(tache.getEtat());
        grid.add(comboEtat, 1, 1);

        // 3. Dates
        // Remplacer la section Dates par :
        grid.add(new Label("Jour:"), 0, 2);
        comboJour = new ComboBox<>();
        // On charge les jours autorisés (triés si besoin, ici Set n'est pas ordonné, attention !)
        // Pour l'ordre Lundi->Dimanche, mieux vaut une List ordonnée dans Tache
        comboJour.getItems().addAll("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        comboJour.setValue(tache.getJour());
        grid.add(comboJour, 1, 2);


        // 4. Durée (Spinner)
        grid.add(new Label("Durée (jours):"), 0, 4);
        spinnerDuree = new Spinner<>(0, 365, tache.getDureeEstimee());
        spinnerDuree.setEditable(true);
        grid.add(spinnerDuree, 1, 4);

        // 5. Couleur
        grid.add(new Label("Couleur:"), 0, 5);
        String webColor = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        colorPicker = new ColorPicker(Color.web(webColor));
        grid.add(colorPicker, 1, 5);

        // 6. Commentaire
        grid.add(new Label("Commentaire:"), 0, 6);
        champCommentaire = new TextArea(tache.getCommentaire());
        champCommentaire.setPrefRowCount(3);
        grid.add(champCommentaire, 1, 6);

        // 7. Sous-tâches (Si applicable)
        if (tache instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) tache;
            grid.add(new Label("Sous-tâches:"), 0, 7);
            ListView<String> listeSousTaches = new ListView<>();
            for (Tache enfant : composite.getEnfants()) {
                listeSousTaches.getItems().add(enfant.getLibelle());
            }
            listeSousTaches.setPrefHeight(80);
            grid.add(listeSousTaches, 1, 7);
        }

        // Boutons
        Button btnSauvegarder = new Button("Sauvegarder");
        Button btnAnnuler = new Button("Annuler");

        // Utilisation du contrôleur externe pour la sauvegarde
        btnSauvegarder.setOnAction(new ControleurSauvegarderModif(modele, tache, this));

        btnAnnuler.setOnAction(e -> stage.close());

        HBox boutons = new HBox(10, btnSauvegarder, btnAnnuler);
        grid.add(boutons, 1, 8);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
    }

    public void afficher() {
        stage.showAndWait();
    }

    public void fermer() {
        stage.close();
    }

    // --- ACCESSEURS (Getters) POUR LE CONTRÔLEUR ---

    public String getTitreSaisi() {
        return champTitre.getText();
    }

    public String getCommentaireSaisi() {
        return champCommentaire.getText();
    }

    public String getEtatSelectionne() {
        return comboEtat.getValue();
    }

    // Getter pour le contrôleur
    public String getJourSelectionne() {
        return comboJour.getValue();
    }

    public int getDureeSaisie() {
        return spinnerDuree.getValue();
    }

    public String getCouleurChoisie() {
        // Conversion de l'objet Color JavaFX en String Hexadécimal (#RRGGBB)
        Color c = colorPicker.getValue();
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }
}