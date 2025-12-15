package com.example.trello.Vue;

import com. example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene. layout.HBox;

public class VueKanbanTache extends HBox implements Observateur {

    private Tache tache;
    private Label label;

    public VueKanbanTache(Tache tache) {
        super();
        this.tache = tache;

        afficher();
    }

    /**
     * Affiche la tâche en lisant ses données depuis le modèle
     */
    private void afficher() {
        this.setStyle("-fx-background-color:  " + tache.getColor() + "; " +
                "-fx-background-radius: 3; " +
                "-fx-padding: 8;");
        this.setAlignment(Pos.CENTER_LEFT);

        // Lire le nom de la tâche depuis le modèle
        label = new Label(tache.getLibelle());
        label.setStyle("-fx-text-fill: " + ("black") + "; " +
                "-fx-font-size: 12px;");

        this.getChildren().clear();
        this.getChildren().add(label);
    }

    @Override
    public void actualiser(Sujet s) {
        // Relire les données de la tâche depuis le modèle et rafraîchir l'affichage
        afficher();
    }

    public Tache getTache() {
        return tache;
    }
}