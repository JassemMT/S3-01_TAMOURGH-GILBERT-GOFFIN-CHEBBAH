package com.example.trello. Controleur;

import com. example.trello.Modele.Modele;
import com.example.trello.Modele. Tache;
import javafx. geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Contrôleur sous forme de boîte de dialogue pour modifier une tâche
 */
public class ControleurModificationTacheVueListe extends Stage {
    private Modele modele;
    private Tache tache;
    private TextField txtLibelle;
    private TextArea txtCommentaire;
    private ComboBox<String> comboJour;
    private boolean modifie = false;
    private boolean supprime = false;

    private final String[] joursNoms = {"Lundi", "Mardi", "Mercredi", "Jeudi",
            "Vendredi", "Samedi", "Dimanche"};
    private final String[] joursMinuscules = {"lundi", "mardi", "mercredi", "jeudi",
            "vendredi", "samedi", "dimanche"};

    public ControleurModificationTacheVueListe(Modele modele, Tache tache) {
        this.modele = modele;
        this.tache = tache;

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Modifier la tâche");
        setResizable(false);

        creerInterface();
    }

    private void creerInterface() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // En-tête
        Label header = new Label("Modifier :  " + tache.getLibelle());
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(header, 0, 0, 2, 1);

        // Champ libellé
        Label lblLibelle = new Label("Libellé :");
        txtLibelle = new TextField(tache.getLibelle());
        txtLibelle.setPrefWidth(300);
        grid.add(lblLibelle, 0, 1);
        grid.add(txtLibelle, 1, 1);

        // Champ commentaire
        Label lblCommentaire = new Label("Commentaire :");
        txtCommentaire = new TextArea(tache.getCommentaire());
        txtCommentaire.setPrefRowCount(4);
        txtCommentaire.setPrefWidth(300);
        grid.add(lblCommentaire, 0, 2);
        grid.add(txtCommentaire, 1, 2);

        // ComboBox jour
        Label lblJour = new Label("Jour :");
        comboJour = new ComboBox<>();
        for (String jour : joursNoms) {
            comboJour.getItems().add(jour);
        }

        // Sélectionne le jour actuel
        String jourActuel = tache.getJour();
        for (int i = 0; i < joursMinuscules.length; i++) {
            if (joursMinuscules[i].equalsIgnoreCase(jourActuel)) {
                comboJour.setValue(joursNoms[i]);
                break;
            }
        }

        grid.add(lblJour, 0, 3);
        grid.add(comboJour, 1, 3);

        // Label état
        Label lblEtat = new Label("État :");
        Label valeurEtat = new Label(tache.getEtat());
        valeurEtat.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        grid.add(lblEtat, 0, 4);
        grid.add(valeurEtat, 1, 4);

        // Boutons
        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #2196F3; -fx-text-fill:  white; " +
                "-fx-font-weight: bold; -fx-padding: 8 20;");
        btnModifier.setOnAction(e -> modifierTache());

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 20;");
        btnSupprimer.setOnAction(e -> supprimerTache());

        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.setStyle("-fx-padding: 8 20;");
        btnAnnuler.setOnAction(e -> close());

        grid.add(btnModifier, 0, 5);
        grid.add(btnSupprimer, 1, 5);
        grid.add(btnAnnuler, 0, 6, 2, 1);

        Scene scene = new Scene(grid);
        setScene(scene);

        // Focus sur le champ libellé
        txtLibelle.requestFocus();
        txtLibelle.selectAll();
    }

    private void modifierTache() {
        String nouveauLibelle = txtLibelle. getText().trim();
        String nouveauCommentaire = txtCommentaire.getText().trim();
        String nouveauJourNom = comboJour. getValue();

        if (nouveauLibelle.isEmpty()) {
            afficherErreur("Le libellé ne peut pas être vide");
            return;
        }

        try {
            // Trouve le jour en minuscules
            String nouveauJour = null;
            for (int i = 0; i < joursNoms.length; i++) {
                if (joursNoms[i].equals(nouveauJourNom)) {
                    nouveauJour = joursMinuscules[i];
                    break;
                }
            }

            // Modifie la tâche
            tache.setLibelle(nouveauLibelle);
            tache.setCommentaire(nouveauCommentaire);
            if (nouveauJour != null) {
                tache.setJour(nouveauJour);
            }

            // Notifie le modèle
            modele. modifierTache(tache);

            modifie = true;
            close();
        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void supprimerTache() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation. setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la tâche ? ");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer \"" +
                tache.getLibelle() + "\" ?");

        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(btnOui, btnNon);

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == btnOui) {
                try {
                    modele.supprimerTache(tache);
                    supprime = true;
                    close();
                } catch (Exception e) {
                    afficherErreur("Erreur lors de la suppression : " + e. getMessage());
                }
            }
        });
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean estModifie() {
        return modifie;
    }

    public boolean estSupprime() {
        return supprime;
    }
}