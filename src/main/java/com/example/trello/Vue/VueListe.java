package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;
import javafx.scene.Parent;
import javafx.scene.Scene;

class VueListe extends Scene implements Observateur {

    public VueListe(Parent parent, double v, double v1) {
        super(parent, v, v1);
    }

    @Override
    public void actualiser(Sujet s) {

    }
}
