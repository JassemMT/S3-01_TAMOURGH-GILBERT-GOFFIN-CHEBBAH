package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite; // Import nécessaire
import com.example.trello.Modele.TacheSimple;    // Import nécessaire
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
        Dialog<Tache> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Création rapide dans : " + nomColonne);

        ButtonType createButtonType = new ButtonType("Créer & Éditer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        // On charge toutes les tâches comme parents potentiels
        // (Même les Simples, car elles peuvent être promues)
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

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(champTitre, 1, 0);
        grid.add(new Label("Rattacher à:"), 0, 1);
        grid.add(comboParents, 1, 1);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(createButtonType);
        loginButton.setDisable(true);
        champTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // --- C'EST ICI QUE LA LOGIQUE DE PROMOTION S'APPLIQUE ---
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // 1. Par défaut, toute nouvelle tâche est SIMPLE (feuille)
                TacheSimple nouvelleTache = new TacheSimple(
                        champTitre.getText(),
                        "",
                        "Lundi",
                        nomColonne,
                        0
                );

                Tache parentSelectionne = comboParents.getValue();

                if (parentSelectionne != null) {
                    // 2. Vérification du type du parent
                    if (parentSelectionne instanceof TacheComposite) {
                        // Cas facile : Le parent est déjà un conteneur
                        parentSelectionne.ajouterEnfant(nouvelleTache);
                    }
                    else if (parentSelectionne instanceof TacheSimple) {
                        // Cas complexe : Le parent est simple, il faut le PROMOUVOIR
                        // On appelle le modèle pour transformer le parent Simple en Composite
                        TacheComposite nouveauParent = modele.promouvoirEnComposite((TacheSimple) parentSelectionne);

                        // On ajoute l'enfant au NOUVEAU parent composite
                        nouveauParent.ajouterEnfant(nouvelleTache);
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