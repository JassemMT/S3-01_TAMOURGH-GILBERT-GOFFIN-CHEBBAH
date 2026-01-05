package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;
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

        // --- 1. LISTE DES PARENTS ---
        // CHANGEMENT : On affiche TOUTES les tâches du modèle.
        // Raison : Une TacheSimple peut être sélectionnée, elle sera promue en Composite ensuite.
        ComboBox<Tache> comboParents = new ComboBox<>();
        comboParents.getItems().addAll(modele.getTaches());
        comboParents.setPromptText("Aucun parent (Racine)");

        // Affichage propre (Libellé uniquement)
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

        // Validation du bouton (désactivé si titre vide)
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(createButtonType);
        loginButton.setDisable(true);
        champTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // --- 2. LOGIQUE DE CRÉATION ET PROMOTION ---
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // A. Par défaut, on crée TOUJOURS une TacheSimple (feuille)
                TacheSimple nouvelleTache = new TacheSimple(
                        champTitre.getText(),
                        "", "Lundi", nomColonne, 0
                );

                // B. Gestion du Parent et Promotion
                Tache parentSelectionne = comboParents.getValue();

                if (parentSelectionne != null) {

                    // CAS 1 : Le parent est déjà un Composite (c'est un Projet existant)
                    if (parentSelectionne instanceof TacheComposite) {
                        parentSelectionne.ajouterEnfant(nouvelleTache);
                    }

                    // CAS 2 : Le parent est Simple (c'était une tâche standard) -> PROMOTION
                    else if (parentSelectionne instanceof TacheSimple) {
                        // 1. On demande au modèle de transformer le Simple en Composite
                        // Cette méthode doit retourner la nouvelle instance de TacheComposite
                        TacheComposite nouveauParent = modele.promouvoirEnComposite((TacheSimple) parentSelectionne);

                        // 2. On ajoute l'enfant au NOUVEAU parent
                        nouveauParent.ajouterEnfant(nouvelleTache);
                    }
                }
                // Si parentSelectionne est null, la tâche est créée à la racine comme Simple.

                return nouvelleTache;
            }
            return null;
        });

        Optional<Tache> result = dialog.showAndWait();

        result.ifPresent(tache -> {
            // Note : Si la tâche est racine (pas de parent), on doit l'ajouter au modèle.
            // Si elle est enfant, elle est liée à son parent, mais selon votre implémentation de VueListe,
            // il est souvent plus simple d'ajouter aussi l'enfant au modèle principal pour les recherches/filtres.
            modele.ajouterTache(tache);

            // Ouverture de l'éditeur
            VueEditeurTache editeur = new VueEditeurTache(tache, modele);
            editeur.afficher();
        });
    }
}