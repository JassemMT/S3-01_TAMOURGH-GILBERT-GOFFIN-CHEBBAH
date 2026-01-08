package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Optional;

public class ControleurCreerTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private String nomColonne;

    public ControleurCreerTache(Modele modele, String nomColonne) {
        this.modele = modele;
        this.nomColonne = nomColonne;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        // permet de créer une nouvelle fenêtre de dialogue
        Dialog<Tache> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Création rapide dans : " + nomColonne);

        // création du bouton pour valider la création de la tache
        ButtonType createButtonType = new ButtonType("Créer & Éditer", ButtonBar.ButtonData.OK_DONE);
        // ajout du bouton à la fenêtre de dialogue
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // création d'un conteneur GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // création d'un élément graphique TextField pour renseigner le titre de la nouvelle tache
        TextField champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        // élément graphique DatePicker pour choisir la date
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Date de début");

        // On charge toutes les tâches comme parents potentiels
        ComboBox<Tache> comboParents = new ComboBox<>();
        comboParents.getItems().addAll(modele.getTaches());
        comboParents.setPromptText("Aucun parent (Racine)");

        comboParents.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getLibelle());
            }
        });
        comboParents.setButtonCell(comboParents.getCellFactory().call(null));

        // placement des différents éléments graphiques sur le gridPane
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(champTitre, 1, 0);
        grid.add(new Label("Date début:"), 0, 1); // Ajout du label date
        grid.add(datePicker, 1, 1);               // Ajout du picker
        grid.add(new Label("Rattacher à:"), 0, 2);
        grid.add(comboParents, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node loginButton = dialog.getDialogPane().lookupButton(createButtonType);
        loginButton.setDisable(true);
        champTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // Récupération de la date (ou aujourd'hui par défaut)
                LocalDate dateChoisie = datePicker.getValue();
                if (dateChoisie == null) dateChoisie = LocalDate.now();

                // 1. Création avec LocalDate
                TacheSimple nouvelleTache = new TacheSimple(
                        champTitre.getText(),
                        "",
                        dateChoisie, // <-- Utilisation de la date
                        nomColonne,
                        0
                );

                Tache parentSelectionne = comboParents.getValue();

                if (parentSelectionne != null) {
                    if (parentSelectionne instanceof TacheComposite) {
                        parentSelectionne.ajouterEnfant(nouvelleTache);
                        nouvelleTache.setDateDebut(nouvelleTache.getDateDebut(), parentSelectionne, modele); // la méthode setDateDebut est utilisée pour vérifier la cohérence (tous les tests sont dedant)
                    }
                    else if (parentSelectionne instanceof TacheSimple) {
                        TacheComposite nouveauParent = modele.promouvoirEnComposite((TacheSimple) parentSelectionne);
                        nouveauParent.ajouterEnfant(nouvelleTache);
                        nouvelleTache.setDateDebut(nouvelleTache.getDateDebut(), nouveauParent, modele);
                    }
                }
                return nouvelleTache;
            }
            return null;
        });

        Optional<Tache> result = dialog.showAndWait();

        result.ifPresent(tache -> {
            modele.ajouterTache(tache);
            VueEditeurTache editeur = new VueEditeurTache(tache, modele);
            editeur.afficher();
        });
    }
}