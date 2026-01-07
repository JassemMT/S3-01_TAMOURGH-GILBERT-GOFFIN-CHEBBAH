package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurDesarchiverTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private Tache tache;

    public ControleurDesarchiverTache(Modele modele, Tache tache) {
        this.modele = modele;
        this.tache = tache;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        // On remet la tâche "A FAIRE" (ou on pourrait restaurer son état précédent si on le stockait)
        modele.desarchiverTache(tache);
    }
}