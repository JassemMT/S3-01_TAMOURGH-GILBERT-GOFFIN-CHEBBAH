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
import java.util.List;
import java.util.Map;

public class VueKanban extends BorderPane implements Observateur {
    // ... (Partie Constructeur et initialisation identique à avant) ...
    // Je réécris les méthodes clés pour la lisibilité

    private Modele modele;
    private HBox conteneurColonnes;

    public VueKanban(Modele modele) {
        this.modele = modele;
        modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        Label titre = new Label("Vue Kanban");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button btnAjouterColonne = new Button("+ Colonne");
        btnAjouterColonne.setOnAction(new ControleurAjouterColonne(modele));
        HBox entete = new HBox(20, titre, btnAjouterColonne);
        entete.setAlignment(Pos.CENTER_LEFT); entete.setPadding(new Insets(10));
        setTop(entete);
        conteneurColonnes = new HBox(15); conteneurColonnes.setPadding(new Insets(10)); conteneurColonnes.setAlignment(Pos.TOP_LEFT);
        ScrollPane scrollPane = new ScrollPane(conteneurColonnes); scrollPane.setFitToHeight(true); scrollPane.setStyle("-fx-background: #f5f5f5;");
        setCenter(scrollPane);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            if (m.getTypeVue() != Modele.VUE_KANBAN) return;
            conteneurColonnes.getChildren().clear();
            Map<String, List<Tache>> colonnes = m.getColonnes();
            for (Map.Entry<String, List<Tache>> entry : colonnes.entrySet()) {
                conteneurColonnes.getChildren().add(creerColonne(entry.getKey(), entry.getValue()));
            }
        }
    }

    private VBox creerColonne(String titre, List<Tache> taches) {
        // (Identique à l'étape précédente avec les boutons Renommer/Supprimer)
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
        conteneurTaches.setStyle("-fx-background-color: transparent;");
        configurerDropSurColonne(conteneurTaches, titre);

        for (Tache tache : taches) { conteneurTaches.getChildren().add(creerCarteTache(tache)); }

        ScrollPane scrollTaches = new ScrollPane(conteneurTaches);
        scrollTaches.setFitToWidth(true);
        scrollTaches.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollTaches.setPrefHeight(500);

        colonne.getChildren().addAll(ligneTitre, btnAjouter, scrollTaches);
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

        // 2. Jour
        Label lblJour = new Label("📅 " + tache.getJour());
        lblJour.setStyle("-fx-font-size: 10px; -fx-text-fill: #444;");

        // 3. NOUVEAU : Affichage de l'état (Pastille)
        Label lblEtat = new Label(getTexteEtat(tache.getEtat()));
        lblEtat.setStyle("-fx-font-size: 9px; -fx-padding: 2 5; -fx-background-radius: 10; " + getStyleEtat(tache.getEtat()));

        HBox ligneInfos = new HBox(10, lblJour, lblEtat);
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
        Button btnArchiver = new Button("🗄 Archiver");
        btnArchiver.setStyle("-fx-font-size: 10px;");
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, tache));
        carte.getChildren().add(btnArchiver);

        // Interactions
        carte.setOnMouseClicked(new ControleurOuvrirEditeur(tache, modele));
        configurerDragSurCarte(carte, tache);

        String styleNormal = carte.getStyle();
        carte.setOnMouseEntered(e -> carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; -fx-border-color: #4a90e2; -fx-border-width: 2; -fx-border-radius: 3; -fx-cursor: hand;"));
        carte.setOnMouseExited(e -> carte.setStyle(styleNormal));

        return carte;
    }

    // Helpers pour l'affichage de l'état
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
        // Couleurs de fond pour les pastilles
        switch(etat) {
            case Tache.ETAT_A_FAIRE: return "-fx-background-color: #ddd; -fx-text-fill: black;";
            case Tache.ETAT_EN_COURS: return "-fx-background-color: #fff3cd; -fx-text-fill: #856404;"; // Jaune
            case Tache.ETAT_TERMINE: return "-fx-background-color: #d4edda; -fx-text-fill: #155724;"; // Vert
            case Tache.ETAT_ARCHIVE: return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;"; // Rouge
            default: return "";
        }
    }

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

    private void configurerDropSurColonne(VBox colonne, String titreColonne) {
        colonne.setOnDragOver(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) event.acceptTransferModes(TransferMode.MOVE);
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
                        modele.deplacerTacheColonne((Tache) userData, titreColonne);
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}