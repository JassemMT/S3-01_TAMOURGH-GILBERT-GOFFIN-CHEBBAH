package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

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
        // 1. Configuration de la boite de dialogue
        Dialog<Tache> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Créer une tâche dans : " + nomColonne);

        // Boutons standards (Créer / Annuler)
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // 2. Formulaire (Grille)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Champ Titre
        TextField champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        // Champ Parent (ComboBox avec toutes les tâches existantes)
        ComboBox<Tache> comboParents = new ComboBox<>();
        // On remplit avec toutes les tâches non archivées du modèle
        comboParents.getItems().addAll(modele.getTaches());
        comboParents.setPromptText("Aucun parent (Racine)");

        // Affichage propre dans la liste (Optionnel car toString() est défini dans Tache, mais plus sûr)
        comboParents.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLibelle());
                }
            }
        });
        // Affiche correctement l'élément sélectionné quand le menu est fermé
        comboParents.setButtonCell(comboParents.getCellFactory().call(null));

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(champTitre, 1, 0);
        grid.add(new Label("Rattacher à:"), 0, 1);
        grid.add(comboParents, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Active le bouton "Créer" seulement si un titre est saisi
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(createButtonType);
        loginButton.setDisable(true);
        champTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // 3. Traitement du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // Création de la nouvelle tâche
                // Par défaut : Lundi, Durée 0, Couleur par défaut
                Tache nouvelleTache = new Tache(
                        champTitre.getText(),
                        "",         // Commentaire vide
                        "Lundi",    // Jour par défaut
                        nomColonne, // Colonne cliquée
                        0           // Durée par défaut
                );

                // Gestion de la dépendance (Parent/Enfant)
                Tache parentSelectionne = comboParents.getValue();
                if (parentSelectionne != null) {
                    parentSelectionne.ajouterEnfant(nouvelleTache);
                }

                return nouvelleTache;
            }
            return null;
        });

        // 4. Affichage et ajout au modèle
        Optional<Tache> result = dialog.showAndWait();
        result.ifPresent(tache -> {
            modele.ajouterTache(tache);
        });
    }
}