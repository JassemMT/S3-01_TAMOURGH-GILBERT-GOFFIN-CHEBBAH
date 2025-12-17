package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class ControleurAjouterColonne implements EventHandler<ActionEvent> {
    private Modele modele;
    public ControleurAjouterColonne(Modele modele) { this.modele = modele; }

    @Override
    public void handle(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("Nouvelle Colonne");
        dialog.setTitle("Ajouter une colonne");
        dialog.setHeaderText("Nom de la colonne :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nom -> modele.ajouterColonne(nom));
    }
}