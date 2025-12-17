package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurCreerTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private String nomColonne;

    public ControleurCreerTache(Modele modele, String nomColonne) {
        this.modele = modele;
        this.nomColonne = nomColonne;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        // MODIFIÉ : On passe "Lundi" par défaut et 0 pour la durée
        TacheSimple nouvelleTache = new TacheSimple(
                "Nouvelle Tâche",
                "Description...",
                "Lundi",
                nomColonne,
                0
        );

        modele.ajouterTache(nouvelleTache);
    }
}