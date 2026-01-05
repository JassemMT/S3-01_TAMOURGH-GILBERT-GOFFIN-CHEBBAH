package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite; // <--- Import
import com.example.trello.Modele.TacheSimple;    // <--- Import
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

        // --- 1. FILTRAGE DES PARENTS ---
        // Seules les TacheComposite peuvent être parents.
        // On filtre la liste pour ne pas proposer de rattacher à une TacheSimple.
        ComboBox<Tache> comboParents = new ComboBox<>();
        for (Tache t : modele.getTaches()) {
            if (t instanceof TacheComposite) {
                comboParents.getItems().add(t);
            }
        }
        comboParents.setPromptText("Aucun parent");

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Tache parentSelectionne = comboParents.getValue();
                Tache nouvelleTache;

                // ---  Composite par défaut, Simple si enfant ---
                if (parentSelectionne == null) {
                    // Pas de parent = Racine = TacheComposite (Projet/Dossier)
                    nouvelleTache = new TacheComposite(
                            champTitre.getText(),
                            "", "Lundi", nomColonne, 0
                    );
                } else {
                    // A un parent = Sous-tâche = TacheSimple
                    nouvelleTache = new TacheSimple(
                            champTitre.getText(),
                            "", "Lundi", nomColonne, 0
                    );
                    // L'ajout effectif à la liste des enfants du parent se fait juste après
                    parentSelectionne.ajouterEnfant(nouvelleTache);
                }

                return nouvelleTache;
            }
            return null;
        });

        Optional<Tache> result = dialog.showAndWait();

        result.ifPresent(tache -> {
            // On ajoute toujours au modèle global pour qu'elle apparaisse dans les vues
            modele.ajouterTache(tache);

            // Ouverture immédiate de l'éditeur
            VueEditeurTache editeur = new VueEditeurTache(tache, modele);
            editeur.afficher();
        });
    }
}