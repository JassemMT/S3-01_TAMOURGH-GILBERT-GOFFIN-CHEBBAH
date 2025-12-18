package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ControleurSupprimerColonne implements EventHandler<ActionEvent> {
    private Modele modele;
    private String nomColonne;

    public ControleurSupprimerColonne(Modele modele, String nomColonne) {
        this.modele = modele;
        this.nomColonne = nomColonne;
    }

    @Override
    public void handle(ActionEvent event) {
        // Vérification "Principal"
        if ("Principal".equals(nomColonne)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Action impossible");
            alert.setHeaderText(null);
            alert.setContentText("Vous ne pouvez pas supprimer la colonne 'Principal'.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer la colonne");
        alert.setHeaderText("Supprimer la colonne : " + nomColonne + " ?");
        alert.setContentText("Les tâches seront déplacées vers 'À faire'.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            modele.supprimerColonne(nomColonne);
        }
    }
}