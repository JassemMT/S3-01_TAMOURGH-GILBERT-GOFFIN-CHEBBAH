package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurArchiverTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private Tache tache;

    public ControleurArchiverTache(Modele modele, Tache tache) {
        this.modele = modele;
        this.tache = tache;
    }

    // méthode handle permettant de changer l'état d'une tache et de la passer en ARCHIVEE
    @Override
    public void handle(ActionEvent actionEvent) {
        modele.archiverTache(tache);
    }
}