package com.example.trello.Vue;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele. Tache;
import com.example.trello.Modele.TacheSimple;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util. List;
import java.util. Map;


/**
 * Vue Kanban - Affichage en colonnes
 * Implémente le pattern Observateur côté Observateur
 */
public class VueKanban implements Observateur {

    private Modele modele;
    private BorderPane root;
    private HBox columnsContainer;
    private Scene scene;

    public VueKanban(Modele modele) {
        this.modele = modele;
        this.modele. ajouterObservateur(this);
        initialiserInterface();
    }

    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #F5F5F5;");

        // Conteneur des colonnes
        columnsContainer = new HBox(15);
        columnsContainer.setPadding(new Insets(10));
        columnsContainer. setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(columnsContainer);
        scrollPane.setFitToHeight(true);
        scrollPane. setStyle("-fx-background-color: #F5F5F5;");

        // Bouton pour ajouter une colonne
        Button addColumnButton = creerBoutonRond("+");
        addColumnButton. setStyle("-fx-font-size: 24px; -fx-background-color: #6C6C6C; " +
                "-fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand;");
        addColumnButton.setOnAction(e -> afficherDialogueNouvelleColonne());

        VBox buttonContainer = new VBox(addColumnButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10));

        HBox mainContent = new HBox(scrollPane, buttonContainer);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        root.setCenter(mainContent);

        scene = new Scene(root, 900, 600);

        // Charge les colonnes initiales
        chargerColonnes();
    }

    /**
     * Charge toutes les colonnes depuis le modèle
     */
    private void chargerColonnes() {
        columnsContainer.getChildren().clear();

        Map<String, List<Tache>> colonnes = modele.getColonnes();

        for (Map.Entry<String, List<Tache>> entry : colonnes. entrySet()) {
            String nomColonne = entry.getKey();
            List<Tache> taches = entry.getValue();

            VBox colonneVBox = creerColonne(nomColonne, taches);
            columnsContainer.getChildren().add(colonneVBox);
        }
    }

    /**
     * Crée une colonne graphique
     */
    private VBox creerColonne(String titre, List<Tache> taches) {
        VBox columnPane = new VBox(10);
        columnPane.setPrefWidth(200);
        columnPane.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #E0E0E0; -fx-border-radius: 5;");
        columnPane.setPadding(new Insets(10));

        // En-tête de la colonne
        Label titleLabel = new Label(titre);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-color: #E0E0E0; -fx-padding:  5px 10px; -fx-background-radius: 5;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        // Conteneur des tâches
        VBox tasksContainer = new VBox(8);
        tasksContainer.setPadding(new Insets(10, 0, 10, 0));

        // Ajoute les tâches existantes
        for (Tache tache : taches) {
            tasksContainer.getChildren().add(creerTacheVisuelle(tache, titre));
        }

        // Bouton pour ajouter une tâche
        Button addTaskButton = creerBoutonRond("+");
        addTaskButton. setStyle("-fx-background-color: #A0A0A0; -fx-text-fill: white; " +
                "-fx-font-size: 20px; -fx-background-radius: 15; -fx-cursor: hand;");
        addTaskButton.setOnAction(e -> afficherDialogueNouvelleTache(titre));

        HBox buttonContainer = new HBox(addTaskButton);
        buttonContainer.setAlignment(Pos.CENTER);

        columnPane.getChildren().addAll(titleLabel, tasksContainer, buttonContainer);

        return columnPane;
    }

    /**
     * Crée la représentation visuelle d'une tâche
     */
    private HBox creerTacheVisuelle(Tache tache, String colonne) {
        HBox taskBox = new HBox(5);
        taskBox.setAlignment(Pos.CENTER_LEFT);
        taskBox.setPadding(new Insets(10));
        taskBox.setMaxWidth(Double.MAX_VALUE);

        Color couleur = Color.web(tache.getColor());
        taskBox.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 5; -fx-cursor: hand;",
                toHexString(couleur)));

        Label taskLabel = new Label(tache.getLibelle());
        taskLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold;",
                couleur.getBrightness() > 0.5 ? "black" : "white"));
        taskLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(taskLabel, Priority.ALWAYS);

        Button deleteBtn = new Button("×");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill:  " +
                (couleur.getBrightness() > 0.5 ? "black" : "white") + "; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            modele.supprimerTache(tache);
        });

        taskBox. getChildren().addAll(taskLabel, deleteBtn);

        taskBox.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                afficherDialogueEditionTache(tache);
            }
        });

        return taskBox;
    }

    /**
     * Affiche le dialogue pour ajouter une nouvelle tâche
     */
    private void afficherDialogueNouvelleTache(String colonne) {
        Stage dialog = new Stage();
        dialog.setTitle("Nouvelle tâche");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(20));

        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la tâche");

        TextArea descField = new TextArea();
        descField.setPromptText("Description (optionnelle)");
        descField.setPrefRowCount(3);

        Button confirmButton = new Button("Créer");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
        confirmButton.setOnAction(e -> {
            if (! titreField.getText().isEmpty()) {
                Tache nouvelleTache = new TacheSimple(titreField.getText(), descField.getText());
                nouvelleTache.setColonne(colonne);
                modele. ajouterTache(nouvelleTache);
                dialog.close();
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonsBox = new HBox(10, confirmButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(
                new Label("Titre de la tâche:"),
                titreField,
                new Label("Description:"),
                descField,
                buttonsBox
        );

        Scene dialogScene = new Scene(dialogVbox, 350, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Affiche le dialogue pour éditer une tâche
     */
    private void afficherDialogueEditionTache(Tache tache) {
        Stage dialog = new Stage();
        dialog.setTitle("Éditer la tâche");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(20));

        TextField titreField = new TextField(tache.getLibelle());
        TextArea descField = new TextArea(tache.getCommentaire());
        descField.setPrefRowCount(3);

        ComboBox<String> colonneCombo = new ComboBox<>();
        colonneCombo. getItems().addAll(modele.getColonnes().keySet());
        colonneCombo.setValue(tache.getColonne());

        Button confirmButton = new Button("Enregistrer");
        confirmButton. setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            tache.setLibelle(titreField.getText());
            tache.setLibelle(descField.getText());
            modele.deplacerTache(tache, colonneCombo. getValue());
            dialog.close();
        });

        Button cancelButton = new Button("Annuler");
        cancelButton. setOnAction(e -> dialog. close());

        HBox buttonsBox = new HBox(10, confirmButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(
                new Label("Titre:"), titreField,
                new Label("Description:"), descField,
                new Label("Colonne:"), colonneCombo,
                buttonsBox
        );

        Scene dialogScene = new Scene(dialogVbox, 350, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Affiche le dialogue pour ajouter une nouvelle colonne
     */
    private void afficherDialogueNouvelleColonne() {
        Stage dialog = new Stage();
        dialog.setTitle("Nouvelle colonne");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de la colonne");

        Button confirmButton = new Button("Créer");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill:  white;");
        confirmButton. setOnAction(e -> {
            if (!nameField.getText().isEmpty()) {
                // La colonne sera créée automatiquement lors de l'actualisation
                // si une tâche y est ajoutée
                dialog. close();
                afficherDialogueNouvelleTache(nameField.getText());
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton. setOnAction(e -> dialog. close());

        HBox buttonsBox = new HBox(10, confirmButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(
                new Label("Nom de la colonne: "),
                nameField,
                buttonsBox
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Crée un bouton rond
     */
    private Button creerBoutonRond(String text) {
        Button button = new Button(text);
        button.setMinSize(50, 50);
        button.setMaxSize(50, 50);
        return button;
    }

    /**
     * Convertit une couleur en format hexadécimal
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Méthode appelée par le modèle lors d'une mise à jour
     */
    @Override
    public void actualiser(Sujet s) {
        // Recharge les colonnes
        chargerColonnes();
    }

    /**
     * Retourne la scène JavaFX
     */
    public Scene getScene() {
        return scene;
    }
}