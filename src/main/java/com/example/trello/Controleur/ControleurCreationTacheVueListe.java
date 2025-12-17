package com.example.trello.Controleur;

import com.example. trello.Modele. Modele;
import com.example.trello. Modele.TacheSimple;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage. Modality;
import javafx. stage.Stage;

/**
 * Contrôleur sous forme de boîte de dialogue pour créer une tâche
 */
public class ControleurCreationTacheVueListe extends Stage {
    private Modele modele;
    private String jour;
    private TextField txtLibelle;
    private TextArea txtCommentaire;
    private boolean valide = false;

    public ControleurCreationTacheVueListe(Modele modele, String jour) {
        this.modele = modele;
        this.jour = jour;

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Nouvelle tâche");
        setResizable(false);

        creerInterface();
    }

    private void creerInterface() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // En-tête
        Label header = new Label("Créer une tâche pour " + capitaliser(jour));
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(header, 0, 0, 2, 1);

        // Champ libellé
        Label lblLibelle = new Label("Libellé :");
        txtLibelle = new TextField();
        txtLibelle.setPromptText("Nom de la tâche");
        txtLibelle.setPrefWidth(300);
        grid.add(lblLibelle, 0, 1);
        grid.add(txtLibelle, 1, 1);

        // Champ commentaire
        Label lblCommentaire = new Label("Commentaire :");
        txtCommentaire = new TextArea();
        txtCommentaire.setPromptText("Description (optionnelle)");
        txtCommentaire.setPrefRowCount(4);
        txtCommentaire.setPrefWidth(300);
        grid.add(lblCommentaire, 0, 2);
        grid.add(txtCommentaire, 1, 2);

        // Label jour
        Label lblJour = new Label("Jour :");
        Label valeurJour = new Label(capitaliser(jour));
        valeurJour.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3;");
        grid.add(lblJour, 0, 3);
        grid.add(valeurJour, 1, 3);

        // Boutons
        Button btnCreer = new Button("Créer");
        btnCreer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill:  white; " +
                "-fx-font-weight: bold; -fx-padding: 8 20;");
        btnCreer.setOnAction(e -> creerTache());

        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.setStyle("-fx-padding: 8 20;");
        btnAnnuler.setOnAction(e -> close());

        GridPane.setColumnSpan(btnCreer, 1);
        grid.add(btnCreer, 0, 4);
        grid.add(btnAnnuler, 1, 4);

        // Validation en temps réel
        btnCreer.setDisable(true);
        txtLibelle.textProperty().addListener((obs, oldVal, newVal) -> {
            btnCreer.setDisable(newVal.trim().isEmpty());
        });

        Scene scene = new Scene(grid);
        setScene(scene);

        // Focus sur le champ libellé
        txtLibelle.requestFocus();
    }

    private void creerTache() {
        String libelle = txtLibelle.getText().trim();
        String commentaire = txtCommentaire.getText().trim();

        if (libelle.isEmpty()) {
            afficherErreur("Le libellé ne peut pas être vide");
            return;
        }

        try {
            TacheSimple nouvelleTache = new TacheSimple(libelle, commentaire, jour.toLowerCase());
            modele.ajouterTache(nouvelleTache);
            valide = true;
            close();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la création :  " + e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String capitaliser(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public boolean estValide() {
        return valide;
    }
}