package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 * Contrôleur pour ajouter une nouvelle colonne
 */
public class ControleurAjouterColonne implements EventHandler<ActionEvent> {
    private Modele modele;

    public ControleurAjouterColonne(Modele modele) {
        this.modele = modele;
    }

    @Override
    public void handle(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle colonne");
        dialog.setHeaderText("Créer une nouvelle colonne");
        dialog.setContentText("Nom de la colonne:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nom -> {
            if (!nom.trim().isEmpty()) {
                modele.ajouterColonne(nom.trim());
            }
        });
    }
}