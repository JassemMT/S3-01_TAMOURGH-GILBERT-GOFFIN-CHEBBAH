package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Contrôleur pour archiver une tâche
 */
public class ControleurArchiverTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private Tache tache;

    public ControleurArchiverTache(Modele modele, Tache tache) {
        this.modele = modele;
        this.tache = tache;
    }

    @Override
    public void handle(ActionEvent event) {
        modele.archiverTache(tache);
    }
}