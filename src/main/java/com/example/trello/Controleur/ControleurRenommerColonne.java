package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class ControleurRenommerColonne implements EventHandler<ActionEvent> {
    private Modele modele;
    private String ancienNom;

    public ControleurRenommerColonne(Modele modele, String ancienNom) {
        this.modele = modele;
        this.ancienNom = ancienNom;
    }

    @Override
    public void handle(ActionEvent event) {

        if ("Principal".equals(ancienNom)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Action impossible");
            alert.setHeaderText(null);
            alert.setContentText("Vous ne pouvez pas renommer la colonne 'Principal'.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(ancienNom);
        dialog.setTitle("Renommer la colonne");
        dialog.setHeaderText("Renommer : " + ancienNom);
        dialog.setContentText("Nouveau nom :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nouveauNom -> {
            modele.renommerColonne(ancienNom, nouveauNom);
        });
    }
}