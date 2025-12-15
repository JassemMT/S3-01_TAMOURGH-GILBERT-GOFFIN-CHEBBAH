package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;


class VueKanban  extends GridPane implements Observateur {

    public VueKanban() {
        super();
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #e0e0e0;");
    }

    @Override
    public void actualiser(Sujet s) {
    }
}
