package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class VueKanbanTache extends HBox implements Observateur {

    public VueKanbanTache(String texte, String couleur) {
        super();
        this.setStyle("-fx-background-color: " + couleur + "; " +
                "-fx-background-radius: 3; " +
                "-fx-padding: 8;");
        this.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(texte);
        label.setStyle("-fx-text-fill: " + (couleur. equals("#00ff00") ? "black" : "white") + "; " +
                "-fx-font-size: 12px;");

        this.getChildren().add(label);
    }

    @Override
    public void actualiser(Sujet s) {

    }
}
