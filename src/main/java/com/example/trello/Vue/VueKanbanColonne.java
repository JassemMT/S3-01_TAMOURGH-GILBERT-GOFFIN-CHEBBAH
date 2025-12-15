package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VueKanbanColonne extends VBox implements Observateur {

    public VueKanbanColonne(String titre, String couleur) {
        super(10);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #c0c0c0; -fx-background-radius: 5;");
        this.setPrefWidth(200);

        Label header = new Label(titre);
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #d0d0d0; " +
                "-fx-padding: 10; -fx-background-radius: 5;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // Zone pour les tâches
        VBox listeTaches = new VBox(5);
        listeTaches.setPadding(new Insets(5));

        // Bouton + en bas de la colonne
        Button btnAjoutColonne = new Button("+");
        btnAjoutColonne.setStyle(
                "-fx-background-color:  #808080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size:  20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 25; " +
                        "-fx-min-width: 40px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-max-width: 40px; " +
                        "-fx-max-height: 40px;"
        );
        //btnAjoutColonne.setOnAction();

        HBox boutonContainer = new HBox(btnAjoutColonne);
        boutonContainer.setAlignment(Pos.CENTER);
        boutonContainer.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(header, listeTaches, boutonContainer);
    }

    @Override
    public void actualiser(Sujet s) {

    }
}
