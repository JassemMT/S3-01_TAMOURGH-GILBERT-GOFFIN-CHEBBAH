package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurSauvegarderModif;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
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

    // Champs
    private TextField champTitre;
    private TextArea champCommentaire;
    private ComboBox<String> comboEtat;
    private ComboBox<String> comboColonne; // NOUVEAU
    private ComboBox<String> comboJour;
    private Spinner<Integer> spinnerDuree;
    private ColorPicker colorPicker;

    // Constructeur de la vueEditeurTache avec en paramètre la tâche courante et le modèle
    public VueEditeurTache(Tache tache, Modele modele) {
        this.tache = tache;
        this.modele = modele;
        initialiserInterface();
    }

    // Méthode permettant de créer l'interface graphique
    private void initialiserInterface() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Éditer : " + tache.getLibelle());

        // création d'un gridpane pour contenir les différents éléments de modification
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        // 1. Ajout de l'élément graphique (label + textfield) concernant le Titre de la tâche
        grid.add(new Label("Titre:"), 0, 0);
        champTitre = new TextField(tache.getLibelle());
        grid.add(champTitre, 1, 0);

        // 2. Ajout de l'élément graphique concernant la Colonne (label + comboBox) (désigne la catégorie de la ta^che)
        grid.add(new Label("Colonne:"), 0, 1);
        comboColonne = new ComboBox<>();
        comboColonne.getItems().addAll(modele.getColonnesDisponibles());
        comboColonne.setValue(tache.getColonne());
        grid.add(comboColonne, 1, 1);

        // 3. Ajout de l'élément graphique pour gérer l'état (label + comboBox) (désigne la progression de la tâche)
        grid.add(new Label("État (Pastille):"), 0, 2);
        comboEtat = new ComboBox<>();
        comboEtat.getItems().addAll("À faire", "En cours", "Terminé", "Archivé");
        comboEtat.setValue(getEtatString(tache.getEtat()));
        grid.add(comboEtat, 1, 2);

        // 4. élément graphique pour gérer le jour (label + comboBox)
        grid.add(new Label("Jour:"), 0, 3);
        comboJour = new ComboBox<>();
        comboJour.getItems().addAll(Tache.JOURS_AUTORISES);
        comboJour.setValue(tache.getJour());
        grid.add(comboJour, 1, 3);

        // 5. élément graphique pour gérer la durée d'une tâche (label + Spinner)
        grid.add(new Label("Durée (h):"), 0, 4);
        spinnerDuree = new Spinner<>(0, 100, tache.getDureeEstimee());
        spinnerDuree.setEditable(true);
        grid.add(spinnerDuree, 1, 4);

        // 6. élément graphique pour gérer la couleur d'une tâche (label + colorPicker)
        grid.add(new Label("Couleur:"), 0, 5);
        String webColor = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        colorPicker = new ColorPicker(Color.web(webColor));
        grid.add(colorPicker, 1, 5);

        // 7. élément graphique pour gérer la description d'une tâche (label + textArea)
        grid.add(new Label("Commentaire:"), 0, 6);
        champCommentaire = new TextArea(tache.getCommentaire());
        champCommentaire.setPrefRowCount(3);
        grid.add(champCommentaire, 1, 6);

        // Initialisation des boutons
        Button btnSauvegarder = new Button("Sauvegarder");
        Button btnAnnuler = new Button("Annuler");

        // Initialisation des actions pour chaque bouton
        btnSauvegarder.setOnAction(new ControleurSauvegarderModif(modele, tache, this));
        btnAnnuler.setOnAction(e -> stage.close());

        HBox boutons = new HBox(10, btnSauvegarder, btnAnnuler);
        grid.add(boutons, 1, 7);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
    }

    // Getters
    private String getEtatString(int etat) {
        switch (etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            case Tache.ETAT_ARCHIVE: return "Archivé";
            default: return "À faire";
        }
    }

    public void afficher() { stage.showAndWait(); }
    public void fermer() { stage.close(); }

    // Getters
    public String getTitreSaisi() { return champTitre.getText(); }
    public String getCommentaireSaisi() { return champCommentaire.getText(); }
    public String getEtatSelectionne() { return comboEtat.getValue(); }
    public String getColonneSelectionnee() { return comboColonne.getValue(); } // NOUVEAU
    public String getJourSelectionne() { return comboJour.getValue(); }
    public int getDureeSaisie() { return spinnerDuree.getValue(); }
    public String getCouleurChoisie() {
        Color c = colorPicker.getValue();
        return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
}