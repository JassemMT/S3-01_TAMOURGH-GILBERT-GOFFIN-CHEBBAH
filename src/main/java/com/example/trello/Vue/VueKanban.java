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

    // Formattage de la date ( "12/10")
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");
    // 1. CONSTANTE POUR DIFFÉRENCIER TACHE ET COLONNE
    private static final String PREFIX_COL = "COL|";

    public VueKanban(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    // initialisation de l'interface graphique
    private void initialiserInterface() {
        // implémentation du titre de la vue
        Label titre = new Label("Vue Kanban");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // création du bouton pour ajouter une colonne
        Button btnAjouterColonne = new Button("+ Colonne");
        btnAjouterColonne.setOnAction(new ControleurAjouterColonne(modele));

        // création d'une hbox pour les entêtes
        HBox entete = new HBox(20, titre, btnAjouterColonne);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(10));
        setTop(entete);

        // création du conteneur des colonnes
        conteneurColonnes = new HBox(15);
        conteneurColonnes.setPadding(new Insets(10));
        conteneurColonnes.setAlignment(Pos.TOP_LEFT);

        // implémentation d'un scrollPane pour gérer le grand nombre de tache
        ScrollPane scrollPane = new ScrollPane(conteneurColonnes);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f5f5f5;");
        setCenter(scrollPane);
    }

    // nettoie la vue et reconstruit les colonnes
    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            // if (m.getTypeVue() != Modele.VUE_KANBAN) return;

            conteneurColonnes.getChildren().clear();
            Map<String, List<Tache>> colonnes = m.getColonnes();
            for (Map.Entry<String, List<Tache>> entry : colonnes.entrySet()) {
                conteneurColonnes.getChildren().add(creerColonne(entry.getKey(), entry.getValue()));
            }
        }
    }

    // permet la création d'une colonne donnée avec sa liste de taches
    private VBox creerColonne(String titre, List<Tache> taches) {
        // vbox pour l'entête
        VBox colonne = new VBox(10);
        colonne.setPrefWidth(300);
        colonne.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5;");
        colonne.setPadding(new Insets(10));

        // label pour le titre de la colonne
        Label labelTitre = new Label(titre + " (" + taches.size() + ")");
        labelTitre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox.setHgrow(labelTitre, Priority.ALWAYS);
        labelTitre.setMaxWidth(Double.MAX_VALUE);

        // bouton renommer avec un style et une action spécifique
        Button btnRenommer = new Button("✎");
        btnRenommer.setStyle("-fx-font-size: 10px;");
        btnRenommer.setOnAction(new ControleurRenommerColonne(modele, titre));

        // bouton supprimer avec un style et une action spécifique
        Button btnSupprimer = new Button("X");
        btnSupprimer.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
        btnSupprimer.setOnAction(new ControleurSupprimerColonne(modele, titre));

        // ne permet à l'utilisateur de supprimer la colonne principale
        if("Principal".equals(titre)) btnSupprimer.setDisable(true);

        // hbox pour contenir les boutons
        HBox actions = new HBox(5, btnRenommer, btnSupprimer);
        actions.setAlignment(Pos.CENTER_RIGHT);

        // hbox pour le titre de la colonne
        HBox ligneTitre = new HBox(5, labelTitre, actions);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);


        // bouton ajouter + action
        Button btnAjouter = new Button("+ Ajouter tâche");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setOnAction(new ControleurCreerTache(modele, titre));

        // vbox pour contenir les taches
        VBox conteneurTaches = new VBox(8);
        conteneurTaches.setMinHeight(400);
        conteneurTaches.setStyle("-fx-background-color: transparent;");
        configurerDropSurColonne(conteneurTaches, titre);

        // implémentation de chaque tache de la colonne
        for (Tache tache : taches) { conteneurTaches.getChildren().add(creerCarteTache(tache)); }

        // scrollPane pour gérer le trop grand nombre de tache ou de colonne
        ScrollPane scrollTaches = new ScrollPane(conteneurTaches);
        scrollTaches.setFitToWidth(true);
        scrollTaches.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollTaches.setPrefHeight(500);

        colonne.getChildren().addAll(ligneTitre, btnAjouter, scrollTaches);
        configurerDragDropColonne(colonne, titre);
        return colonne;
    }
    // 3. LA MÉTHODE MAGIQUE
    private void configurerDragDropColonne(VBox colonneUI, String titreColonne) {

        // --- DÉBUT DU DRAG (On attrape la colonne) ---
        colonneUI.setOnDragDetected(event -> {
            // Sécurité : Si l'utilisateur a cliqué sur une tâche à l'intérieur de la colonne,
            // on ne veut pas déplacer la colonne, mais la tâche.
            // On vérifie si la source du clic n'est pas une tâche (VBox avec UserData)
            if (event.getTarget() instanceof javafx.scene.Node) {
                // Astuce simple : si ce qu'on drag n'est pas la colonne elle-même, on annule ce handler
                // pour laisser la tâche gérer son propre drag.
                // (Mais JavaFX gère souvent le bubbling, donc on force le contenu ici)
            }

            // On lance le Drag and Drop
            Dragboard db = colonneUI.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // On met le TAG spécial + le titre de la colonne
            content.putString(PREFIX_COL + titreColonne);
            db.setContent(content);

            event.consume();
        });

        // --- SURVOL (On passe au dessus d'une autre colonne) ---
        colonneUI.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                String data = event.getDragboard().getString();
                // On accepte le mouvement SEULEMENT si c'est une colonne qui est déplacée
                if (data.startsWith(PREFIX_COL)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });

        // --- EFFET VISUEL ---
        colonneUI.setOnDragEntered(event -> {
            if (event.getDragboard().hasString() && event.getDragboard().getString().startsWith(PREFIX_COL)) {
                // Bordure Bleue pour dire "ça va atterrir ici"
                colonneUI.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5; -fx-border-width: 2; -fx-border-color: #007bff;");
            }
        });

        colonneUI.setOnDragExited(event -> {
            // On remet le style normal quand on sort
            colonneUI.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5; -fx-border-width: 2; -fx-border-color: transparent;");
        });

        // --- LÂCHER (Drop) ---
        colonneUI.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String data = db.getString();

                // Si c'est bien une colonne
                if (data.startsWith(PREFIX_COL)) {
                    // On récupère le nom de la colonne source (en enlevant le préfixe)
                    String sourceCol = data.replace(PREFIX_COL, "");
                    String targetCol = titreColonne; // La colonne sur laquelle on a lâché

                    // On appelle le modèle pour faire l'échange
                    if (!sourceCol.equals(targetCol)) {
                        modele.deplacerColonneOrdre(sourceCol, targetCol);
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }



    // permet la création et l'implémentation des taches
    private VBox creerCarteTache(Tache tache) {
        // vbox permettant contenir les infos de la tache
        VBox carte = new VBox(5);
        carte.setPadding(new Insets(10));
        String couleurHex = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; -fx-border-color: #ddd; -fx-border-radius: 3; -fx-cursor: hand;");

        // Titre
        Label lblLibelle = new Label(tache.getLibelle());
        lblLibelle.setStyle("-fx-font-weight: bold;");
        lblLibelle.setWrapText(true);

        // Date
        String dateStr = tache.getDateDebut().format(SHORT_DATE);
        Label lblDate = new Label("📅 " + dateStr);
        lblDate.setStyle("-fx-font-size: 10px; -fx-text-fill: #444;");

        // État
        Label lblEtat = new Label(getTexteEtat(tache.getEtat()));
        lblEtat.setStyle("-fx-font-size: 9px; -fx-padding: 2 5; -fx-background-radius: 10; " + getStyleEtat(tache.getEtat()));

        HBox ligneInfos = new HBox(10, lblDate, lblEtat);
        ligneInfos.setAlignment(Pos.CENTER_LEFT);

        carte.getChildren().addAll(lblLibelle, ligneInfos);

        // Gestion des sous-tâches
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

        // Bouton archiver
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
        // effet quand la souris est sur la tache
        carte.setOnMouseEntered(e -> carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; -fx-border-color: #4a90e2; -fx-border-width: 2; -fx-border-radius: 3; -fx-cursor: hand;"));
        carte.setOnMouseExited(e -> carte.setStyle(styleNormal));

        return carte;
    }

    // getter etat de la tache la convertissant en string
    private String getTexteEtat(int etat) {
        switch(etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            case Tache.ETAT_ARCHIVE: return "Archivé";
            default: return "";
        }
    }

    // getter pour le style de l'état de la tache en string
    private String getStyleEtat(int etat) {
        switch(etat) {
            case Tache.ETAT_A_FAIRE: return "-fx-background-color: #ddd; -fx-text-fill: black;";
            case Tache.ETAT_EN_COURS: return "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
            case Tache.ETAT_TERMINE: return "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
            case Tache.ETAT_ARCHIVE: return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
            default: return "";
        }
    }

    // permet le drop d'une tache sur une nouvelle carte
    private void configurerDragSurCarte(VBox carte, Tache tache) {
        carte.setOnDragDetected(event -> {
            Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(tache.getLibelle());
            db.setContent(content);
            carte.setUserData(tache);
            event.consume();
        });
    }

    // méthode gérant le drag&drop d'une tache dans une nouvlle colonne
    private void configurerDropSurColonne(VBox colonne, String titreColonne) {
        colonne.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        colonne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Object source = event.getGestureSource();
                if (source instanceof VBox) {
                    VBox carteTache = (VBox) source;
                    Object userData = carteTache.getUserData();
                    if (userData instanceof Tache) {
                        try {
                            modele.deplacerTacheColonne((Tache) userData, titreColonne);
                            success = true;
                        }catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur de déplacement");
                            alert.setHeaderText("Impossible de déplacer la tâche");
                            alert.setContentText(e.getMessage());
                            alert.showAndWait();
                        }
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}