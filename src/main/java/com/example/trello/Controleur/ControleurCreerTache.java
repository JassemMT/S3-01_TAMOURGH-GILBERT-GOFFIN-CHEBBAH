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

/**
 * Contrôleur gérant la création d'une nouvelle tâche.
 * <p>
 * Déclenché par le bouton "+ Ajouter tâche" d'une colonne. Ce contrôleur :
 * <ul>
 * <li>Affiche une boîte de dialogue (Dialog) pour la saisie rapide.</li>
 * <li>Permet de choisir un parent existant pour créer directement une sous-tâche.</li>
 * <li>Gère la <b>Promotion Automatique</b> : si on attache la nouvelle tâche à un parent qui était "Simple",
 * celui-ci est transformé en "Composite" à la volée.</li>
 * <li>Ouvre immédiatement l'éditeur détaillé après la création.</li>
 * </ul>
 * </p>
 */
public class ControleurCreerTache implements EventHandler<ActionEvent> {

    /** Référence vers le modèle pour manipuler les données. */
    private Modele modele;

    /** Nom de la colonne où le bouton a été cliqué (colonne par défaut de la nouvelle tâche). */
    private String nomColonne;

    /**
     * Constructeur du contrôleur.
     *
     * @param modele     Le modèle principal de l'application.
     * @param nomColonne Le nom de la colonne cible (ex: "A faire").
     */
    public ControleurCreerTache(Modele modele, String nomColonne) {
        this.modele = modele;
        this.nomColonne = nomColonne;
    }

    /**
     * Gère l'événement de clic sur le bouton d'ajout.
     * Construit et affiche le formulaire de création.
     *
     * @param actionEvent L'événement JavaFX.
     */
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
        // Cela permet de créer une sous-tâche directement depuis ce menu
        ComboBox<Tache> comboParents = new ComboBox<>();
        comboParents.getItems().addAll(modele.getTaches());
        comboParents.setPromptText("Aucun parent (Racine)");

        // Configuration de l'affichage de la ComboBox pour montrer les libellés
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

        // Validation : Le bouton créer est désactivé si le titre est vide
        Node loginButton = dialog.getDialogPane().lookupButton(createButtonType);
        loginButton.setDisable(true);
        champTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // --- CONVERSION DU RÉSULTAT ---
        // C'est ici que la logique métier de création s'opère
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // Récupération de la date (ou aujourd'hui par défaut)
                LocalDate dateChoisie = datePicker.getValue();
                if (dateChoisie == null) dateChoisie = LocalDate.now();

                // 1. Création de base d'une TacheSimple
                TacheSimple nouvelleTache = new TacheSimple(
                        champTitre.getText(),
                        "",
                        dateChoisie, // <-- Utilisation de la date
                        nomColonne,
                        0
                );

                Tache parentSelectionne = comboParents.getValue();

                // 2. Gestion de la hiérarchie et de la Promotion
                if (parentSelectionne != null) {
                    if (parentSelectionne instanceof TacheComposite) {
                        // Cas facile : le parent est déjà un dossier
                        parentSelectionne.ajouterEnfant(nouvelleTache);
                        // Vérification de la cohérence temporelle
                        nouvelleTache.setDateDebut(nouvelleTache.getDateDebut(), parentSelectionne, modele);
                    }
                    else if (parentSelectionne instanceof TacheSimple) {
                        // Cas complexe : PROMOTION D'OBJET
                        // Le parent était simple, il devient Composite pour accueillir l'enfant
                        TacheComposite nouveauParent = modele.promouvoirEnComposite((TacheSimple) parentSelectionne);
                        nouveauParent.ajouterEnfant(nouvelleTache);
                        nouvelleTache.setDateDebut(nouvelleTache.getDateDebut(), nouveauParent, modele);
                    }
                }
                return nouvelleTache;
            }
            return null;
        });

        // Affichage bloquant de la fenêtre
        Optional<Tache> result = dialog.showAndWait();

        // Une fois la tâche créée, on l'ajoute au modèle et on ouvre l'éditeur détaillé
        result.ifPresent(tache -> {
            modele.ajouterTache(tache);
            VueEditeurTache editeur = new VueEditeurTache(tache, modele);
            editeur.afficher();
        });
    }
}