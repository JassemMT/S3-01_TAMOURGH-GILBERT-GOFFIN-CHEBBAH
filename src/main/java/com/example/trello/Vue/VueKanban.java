package com.example.trello.Vue;

import com.example.trello.Controleur.*;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;

public class VueKanban extends BorderPane implements Observateur {
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

        Button btnAjouterColonne = new Button("+ Ajouter une colonne");
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
            if (m.getTypeVue() != Modele.VUE_KANBAN) return;

            conteneurColonnes.getChildren().clear();
            Map<String, List<Tache>> colonnes = m.getColonnes();

            for (Map.Entry<String, List<Tache>> entry : colonnes.entrySet()) {
                VBox colonne = creerColonne(entry.getKey(), entry.getValue());
                conteneurColonnes.getChildren().add(colonne);
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

        Button btnAjouter = new Button("+ Ajouter");
        btnAjouter.setOnAction(new ControleurCreerTache(modele, titre));

        HBox entete = new HBox(10, labelTitre, btnAjouter);
        entete.setAlignment(Pos.CENTER_LEFT);

        VBox conteneurTaches = new VBox(8);
        conteneurTaches.setStyle("-fx-background-color: transparent;");

        configurerDropSurColonne(conteneurTaches, titre);

        for (Tache tache : taches) {
            VBox carteTache = creerCarteTache(tache);
            conteneurTaches.getChildren().add(carteTache);
        }

        ScrollPane scrollTaches = new ScrollPane(conteneurTaches);
        scrollTaches.setFitToWidth(true);
        scrollTaches.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollTaches.setPrefHeight(500);

        colonne.getChildren().addAll(entete, scrollTaches);
        return colonne;
    }

    private VBox creerCarteTache(Tache tache) {
        VBox carte = new VBox(5);
        carte.setPadding(new Insets(10));
        String couleurHex = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; " +
                "-fx-border-color: #ddd; -fx-border-radius: 3; -fx-cursor: hand;");

        Label lblLibelle = new Label(tache.getLibelle());
        lblLibelle.setStyle("-fx-font-weight: bold;");
        lblLibelle.setWrapText(true);

        // MODIFIÉ : Affichage du jour au lieu des dates
        Label lblJour = new Label("📅 " + tache.getJour());
        lblJour.setStyle("-fx-font-size: 10px; -fx-text-fill: #444;");

        Button btnArchiver = new Button("🗄 Archiver");
        btnArchiver.setStyle("-fx-font-size: 10px;");
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, tache));

        carte.getChildren().addAll(lblLibelle, lblJour, btnArchiver);
        carte.setOnMouseClicked(new ControleurOuvrirEditeur(tache, modele));

        configurerDragSurCarte(carte, tache);

        String styleNormal = carte.getStyle();
        carte.setOnMouseEntered(e ->
                carte.setStyle("-fx-background-color: " + couleurHex + "; -fx-background-radius: 3; " +
                        "-fx-border-color: #4a90e2; -fx-border-width: 2; -fx-border-radius: 3; -fx-cursor: hand;")
        );
        carte.setOnMouseExited(e -> carte.setStyle(styleNormal));

        return carte;
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
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
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