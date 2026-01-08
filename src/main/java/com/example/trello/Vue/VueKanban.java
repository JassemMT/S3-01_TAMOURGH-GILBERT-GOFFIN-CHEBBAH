package com.example.trello.Vue;

import com.example.trello.Controleur.*;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class VueKanban extends BorderPane implements Observateur {

    private Modele modele;
    private HBox conteneurColonnes;

    // Formatter for short date display (e.g. "12/10")
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");

    public VueKanban(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        Label titre = new Label("Vue Kanban");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button btnAjouterColonne = new Button("+ Colonne");
        btnAjouterColonne.setOnAction(new ControleurAjouterColonne(modele));

        HBox entete = new HBox(20, titre, btnAjouterColonne);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(10));
        setTop(entete);

        conteneurColonnes = new HBox(15);
        conteneurColonnes.setPadding(new Insets(10));
        conteneurColonnes.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(conteneurColonnes);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f5f5f5;");
        setCenter(scrollPane);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            // Removed check for VUE_KANBAN to allow updates even if not active view
            // if (m.getTypeVue() != Modele.VUE_KANBAN) return;

            conteneurColonnes.getChildren().clear();
            Map<String, List<Tache>> colonnes = m.getColonnes();
            for (Map.Entry<String, List<Tache>> entry : colonnes.entrySet()) {
                conteneurColonnes.getChildren().add(creerColonne(entry.getKey(), entry.getValue()));
            }
        }
    }

    private VBox creerColonne(String titre, List<Tache> taches) {
        VBox colonne = new VBox(10);
        colonne.setPrefWidth(300);
        colonne.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5;");
        colonne.setPadding(new Insets(10));

        Label labelTitre = new Label(titre + " (" + taches.size() + ")");
        labelTitre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox.setHgrow(labelTitre, Priority.ALWAYS);
        labelTitre.setMaxWidth(Double.MAX_VALUE);

        Button btnRenommer = new Button("✎");
        btnRenommer.setStyle("-fx-font-size: 10px;");
        btnRenommer.setOnAction(new ControleurRenommerColonne(modele, titre));
        Button btnSupprimer = new Button("X");
        btnSupprimer.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
        btnSupprimer.setOnAction(new ControleurSupprimerColonne(modele, titre));

        if("Principal".equals(titre)) btnSupprimer.setDisable(true);

        HBox actions = new HBox(5, btnRenommer, btnSupprimer);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox ligneTitre = new HBox(5, labelTitre, actions);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);

        Button btnAjouter = new Button("+ Ajouter tâche");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setOnAction(new ControleurCreerTache(modele, titre));

        VBox conteneurTaches = new VBox(8);
        conteneurTaches.setMinHeight(400);
        conteneurTaches.setStyle("-fx-background-color: transparent;");
        configurerDropSurColonne(conteneurTaches, titre);

        for (Tache tache : taches) { conteneurTaches.getChildren().add(creerCarteTache(tache)); }

        ScrollPane scrollTaches = new ScrollPane(conteneurTaches);
        scrollTaches.setFitToWidth(true);
        scrollTaches.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollTaches.setPrefHeight(500);

        colonne.getChildren().addAll(ligneTitre, btnAjouter, scrollTaches);
        //configurerDragDropColonne(colonne, titre);
        return colonne;
    }

    private VBox creerCarteTache(Tache tache) {
        VBox carte = new VBox(5);
        carte.setPadding(new Insets(10));
        String couleurHex = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; -fx-border-color: #ddd; -fx-border-radius: 3; -fx-cursor: hand;");

        // 1. Titre
        Label lblLibelle = new Label(tache.getLibelle());
        lblLibelle.setStyle("-fx-font-weight: bold;");
        lblLibelle.setWrapText(true);

        // 2. Date (CORRECTION ICI)
        String dateStr = tache.getDateDebut().format(SHORT_DATE);
        Label lblDate = new Label("📅 " + dateStr);
        lblDate.setStyle("-fx-font-size: 10px; -fx-text-fill: #444;");

        // 3. État
        Label lblEtat = new Label(getTexteEtat(tache.getEtat()));
        lblEtat.setStyle("-fx-font-size: 9px; -fx-padding: 2 5; -fx-background-radius: 10; " + getStyleEtat(tache.getEtat()));

        HBox ligneInfos = new HBox(10, lblDate, lblEtat);
        ligneInfos.setAlignment(Pos.CENTER_LEFT);

        carte.getChildren().addAll(lblLibelle, ligneInfos);

        // 4. Sous-tâches
        if (tache.aDesEnfants()) {
            List<Tache> enfants = tache.getEnfants();
            VBox boxEnfants = new VBox(2);
            boxEnfants.setPadding(new Insets(5, 0, 0, 10));
            boxEnfants.setStyle("-fx-border-color: transparent transparent transparent #888; -fx-border-width: 0 0 0 2;");

            Label lblSousTaches = new Label("Sous-tâches :");
            lblSousTaches.setStyle("-fx-font-size: 9px; -fx-font-style: italic;");
            boxEnfants.getChildren().add(lblSousTaches);

            for (Tache enfant : enfants) {
                Label lblEnfant = new Label("• " + enfant.getLibelle());
                lblEnfant.setStyle("-fx-font-size: 10px;");
                boxEnfants.getChildren().add(lblEnfant);
            }
            carte.getChildren().add(boxEnfants);
        }

        // 5. Bouton archiver
        Button btnArchiver = new Button("ARCHIVER"); // Icone seule pour gagner de la place
        btnArchiver.setStyle("-fx-font-size: 10px; -fx-background-color: transparent; -fx-text-fill: #666;");
        btnArchiver.setTooltip(new Tooltip("Archiver"));
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, tache));

        HBox boxActions = new HBox(btnArchiver);
        boxActions.setAlignment(Pos.CENTER_RIGHT);
        carte.getChildren().add(boxActions);

        // Interactions
        carte.setOnMouseClicked(new ControleurOuvrirEditeur(tache, modele));
        configurerDragSurCarte(carte, tache);

        String styleNormal = carte.getStyle();
        // Hover effect
        carte.setOnMouseEntered(e -> carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; -fx-border-color: #4a90e2; -fx-border-width: 2; -fx-border-radius: 3; -fx-cursor: hand;"));
        carte.setOnMouseExited(e -> carte.setStyle(styleNormal));

        return carte;
    }

    private String getTexteEtat(int etat) {
        switch(etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            case Tache.ETAT_ARCHIVE: return "Archivé";
            default: return "";
        }
    }

    private String getStyleEtat(int etat) {
        switch(etat) {
            case Tache.ETAT_A_FAIRE: return "-fx-background-color: #ddd; -fx-text-fill: black;";
            case Tache.ETAT_EN_COURS: return "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
            case Tache.ETAT_TERMINE: return "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
            case Tache.ETAT_ARCHIVE: return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
            default: return "";
        }
    }

    /**
     * Configure le comportement de la carte (VBox) quand on commence à la glisser.
     * @param carte L'élément graphique (VBox) qui représente la tâche.
     * @param tache L'objet métier (Donnée) associé.
     */
    private void configurerDragSurCarte(VBox carte, Tache tache) {
        // Événement déclenché UNE SEULE FOIS au moment précis où le geste de glisser commence
        carte.setOnDragDetected(event -> {

            // 1. Démarrage officiel du Drag & Drop.
            // On indique à JavaFX que l'intention est de DÉPLACER (MOVE) l'objet, pas de le copier.
            // 'db' (Dragboard) est le "camion de transport" virtuel.
            Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

            // 2. Préparation du contenu "officiel" pour JavaFX.
            // JavaFX exige qu'on mette quelque chose dans le Dragboard pour valider le drag.
            ClipboardContent content = new ClipboardContent();
            content.putString(tache.getLibelle()); // On met juste le titre comme "étiquette".
            db.setContent(content); // On charge le camion.

            // 3. L'ASTUCE DU "POST-IT" (Le point clé !)
            // Le Dragboard gère mal les objets Java complexes.
            // Au lieu de mettre la Tache DANS le Dragboard, on l'attache À LA VBOX elle-même via 'UserData'.
            // C'est comme coller un post-it "Ceci est la Tache ID 42" au dos de la carte graphique.
            carte.setUserData(tache);

            // 4. On dit à l'événement "C'est bon, j'ai géré, ne le propage pas aux parents".
            event.consume();
        });
    }
    /**
     * Configure la colonne pour accepter qu'on lâche des tâches dessus.
     * @param colonne La VBox verticale qui contient les tâches.
     * @param titreColonne Le nom de la colonne (ex: "En cours") pour savoir où envoyer la tâche.
     */
    private void configurerDropSurColonne(VBox colonne, String titreColonne) {

        // --- PARTIE A : LE SURVOL (Autorisation d'atterrir) ---
        // Cet événement se déclenche en continu tant que la souris survole la colonne avec un objet.
        colonne.setOnDragOver(event -> {
            // On vérifie si ce qui est transporté contient du texte (notre "étiquette" de l'étape 1).
            // Si oui, on AUTORISE le dépôt en mode MOVE.
            // Sans cette ligne, le curseur afficherait un sens interdit 🚫.
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // --- PARTIE B : LE LÂCHER (Réception du colis) ---
        // Cet événement se déclenche quand l'utilisateur relâche le clic gauche.
        colonne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false; // Par défaut, on considère que ça a échoué

            // 1. Vérification de sécurité : est-ce qu'il y a bien des données ?
            if (db.hasString()) {

                // 2. RETROUVER L'EXPÉDITEUR
                // "Qui a lancé ce drag ?" -> C'est la VBox de la carte (configurée à l'étape 1)
                Object source = event.getGestureSource();

                // On vérifie que la source est bien une VBox (la carte graphique)
                if (source instanceof VBox) {
                    VBox carteTache = (VBox) source;

                    // 3. RÉCUPÉRER L'OBJET RÉEL (Lecture du "Post-it")
                    // On récupère l'objet qu'on avait attaché via setUserData() au départ.
                    Object userData = carteTache.getUserData();

                    // On vérifie que c'est bien un objet Tache
                    if (userData instanceof Tache) {
                        try {
                            // 4. ACTION MÉTIER (Le vrai travail)
                            // On demande au Modèle de déplacer cette tâche vers la colonne actuelle.
                            // C'est ici que les règles métier (vérification parents, etc.) s'appliquent.
                            modele.deplacerTacheColonne((Tache) userData, titreColonne);

                            // Si aucune exception n'est levée, c'est un succès.
                            success = true;

                        } catch (Exception e) {
                            // 5. GESTION DES ERREURS (Ex: Sous-tâche bloquée par son parent)
                            // Si le Modèle dit "Non", on affiche une pop-up d'erreur.
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur de déplacement");
                            alert.setHeaderText("Impossible de déplacer la tâche");
                            alert.setContentText(e.getMessage()); // Le message vient du Modèle
                            alert.showAndWait();
                        }
                    }
                }
            }

            // 6. FIN DE LA TRANSACTION
            // On signale au système si le drop a réussi ou non.
            // Si true, JavaFX peut nettoyer le Dragboard.
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
