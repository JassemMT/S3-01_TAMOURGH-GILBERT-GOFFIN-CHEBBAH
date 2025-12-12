package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;
import javafx.scene.Parent;
import javafx.scene.Scene;


class VueKanban  extends Scene implements Observateur {

    public VueKanban(Parent parent, double v, double v1) {
        super(parent, v, v1);
    }

    @Override
    public void actualiser(Sujet s) {

    }
}
