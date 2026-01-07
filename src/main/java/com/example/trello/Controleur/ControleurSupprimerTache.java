package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControleurSupprimerTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private Tache tache;

    public ControleurSupprimerTache(Modele modele, Tache tache) {
        this.modele = modele;
        this.tache = tache;
    }

    // permet de supprimer la tache voulu
    @Override
    public void handle(ActionEvent event) {
        // Boite de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression définitive");
        alert.setHeaderText("Supprimer '" + tache.getLibelle() + "' ?");
        alert.setContentText("Cette action est irréversible. La tâche et ses sous-tâches seront perdues.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            modele.supprimerTache(tache);
        }
    }
}