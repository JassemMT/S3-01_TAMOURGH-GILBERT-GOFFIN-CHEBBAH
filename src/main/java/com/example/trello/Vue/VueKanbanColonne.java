package com.example.trello.Vue;

import com.example.trello.Modele.Modele;
import com.example. trello.Modele. Sujet;
import com.example.trello.Modele. Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene. control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout. HBox;
import javafx. scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util. List;

public class VueKanbanColonne extends VBox implements Observateur {

    private String nomColonne;
    private Modele modele;
    private VBox listeTaches;
    private Label header;
    public VueKanbanColonne(String nomColonne, Modele modele) {
        super(10);
        this.nomColonne = nomColonne;
        this.modele = modele;

        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #c0c0c0; -fx-background-radius: 5;");
        this.setPrefWidth(200);

        initialiser();
        afficher();

        // S'enregistrer comme observateur du modèle
        modele. ajouterObservateur(this);
    }

    private Button btnAjoutColonne;

    private void initialiser() {
        // En-tête de la colonne
        header = new Label();
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #d0d0d0; " +
                "-fx-padding:  10; -fx-background-radius: 5;");
        header.setMaxWidth(Double. MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // Zone pour les tâches
        listeTaches = new VBox(5);
        listeTaches. setPadding(new Insets(5));
        VBox.setVgrow(listeTaches, Priority.ALWAYS);

        // Bouton + en bas de la colonne (affichage uniquement, action gérée par le contrôleur)
        btnAjoutColonne = new Button("+");
        btnAjoutColonne.setStyle(
                "-fx-background-color:  #808080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius:  25; " +
                        "-fx-min-width: 40px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-max-width: 40px; " +
                        "-fx-max-height: 40px;"
        );

        HBox boutonContainer = new HBox(btnAjoutColonne);
        boutonContainer.setAlignment(Pos.CENTER);
        boutonContainer.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(header, listeTaches, boutonContainer);
    }

    /**
     * Affiche la colonne en lisant les données depuis le modèle via getColonnes()
     */
    private void afficher() {
        // Lire les tâches de cette colonne depuis le modèle
        List<Tache> taches = modele.getColonnes().get(nomColonne);

        // Vider l'affichage actuel
        listeTaches.getChildren().clear();

        // Créer une vue pour chaque tâche lue depuis le modèle
        if (taches != null) {
            for (Tache tache : taches) {
                VueKanbanTache vueTache = new VueKanbanTache(tache);
                listeTaches.getChildren().add(vueTache);
            }
        }

        // Mettre à jour l'en-tête avec le nombre de tâches lu depuis le modèle
        int nbTaches = (taches != null) ? taches.size() : 0;
        header.setText(nomColonne + " (" + nbTaches + ")");
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            // Relire les données depuis le modèle et rafraîchir l'affichage
            afficher();
        }
    }

    public String getNomColonne() {
        return nomColonne;
    }

    public Button getBtnAjoutColonne() {
        return btnAjoutColonne;
    }
}