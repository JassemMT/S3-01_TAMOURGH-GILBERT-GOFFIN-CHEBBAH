package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurArchiverTache;
import com.example.trello.Controleur.ControleurOuvrirEditeur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VueGantt extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurLignes;
    private GridPane headerJours;

    // On définit ici qu'une journée de travail visuelle vaut 8h
    private static final int HEURES_PAR_JOUR = 8;

    private static final String[] JOURS_SEMAINE = {
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    };

    public VueGantt(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);

        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        // 1. En-tête
        Label titreVue = new Label("Vue Gantt Hebdomadaire");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10;");
        setTop(titreVue);

        // 2. Structure Centrale
        VBox layoutCentral = new VBox(0);

        // A. Header des Jours
        headerJours = new GridPane();
        headerJours.setStyle("-fx-background-color: #eee; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
        headerJours.setPadding(new Insets(5, 0, 5, 0));

        ColumnConstraints colTitre = new ColumnConstraints();
        colTitre.setPercentWidth(30);
        headerJours.getColumnConstraints().add(colTitre);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints colJour = new ColumnConstraints();
            colJour.setPercentWidth(10);
            headerJours.getColumnConstraints().add(colJour);

            Label lblJour = new Label(JOURS_SEMAINE[i]);
            lblJour.setFont(Font.font("System", FontWeight.BOLD, 12));
            lblJour.setMaxWidth(Double.MAX_VALUE);
            lblJour.setAlignment(Pos.CENTER);
            headerJours.add(lblJour, i + 1, 0);
        }

        layoutCentral.getChildren().add(headerJours);

        // Liste des tâches
        conteneurLignes = new VBox(0);
        layoutCentral.getChildren().add(conteneurLignes);

        ScrollPane scroll = new ScrollPane(layoutCentral);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        setCenter(scroll);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            construireGantt(((Modele) s).getTaches());
        }
    }

    private void construireGantt(List<Tache> taches) {
        conteneurLignes.getChildren().clear();

        Set<Tache> sontDesEnfants = new HashSet<>();
        for (Tache t : taches) {
            if (t.aDesEnfants()) sontDesEnfants.addAll(t.getEnfants());
        }

        for (Tache t : taches) {
            if (!sontDesEnfants.contains(t)) {
                ajouterLigneTache(t, 0);
            }
        }
    }

    private void ajouterLigneTache(Tache t, int niveauIndent) {
        GridPane ligne = new GridPane();
        ligne.setPadding(new Insets(5, 0, 5, 0));
        ligne.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        // Configuration des colonnes (Identique au Header pour alignement)
        ColumnConstraints colTitre = new ColumnConstraints();
        colTitre.setPercentWidth(30);
        ligne.getColumnConstraints().add(colTitre);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints colJour = new ColumnConstraints();
            colJour.setPercentWidth(10);
            ligne.getColumnConstraints().add(colJour);

            // Guides visuels verticaux
            Pane guideVisuel = new Pane();
            guideVisuel.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 1 0 0;");
            ligne.add(guideVisuel, i + 1, 0);
        }

        // --- PARTIE GAUCHE : TITRE ---
        HBox boxNom = new HBox(5);
        boxNom.setAlignment(Pos.CENTER_LEFT);
        boxNom.setPadding(new Insets(0, 0, 0, 10 + (niveauIndent * 20)));

        if (niveauIndent > 0) boxNom.getChildren().add(new Label("↳"));

        Label lblNom = new Label(t.getLibelle());
        lblNom.setFont(Font.font("System", FontWeight.NORMAL, 13));
        boxNom.getChildren().add(lblNom);

        boxNom.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        boxNom.setCursor(javafx.scene.Cursor.HAND);

        ligne.add(boxNom, 0, 0);

        // --- BARRE GANTT AVEC DÉPASSEMENT ---
        int indexJourDebut = getIndexJour(t.getJour());
        if (indexJourDebut != -1) {

            // Calcul du nombre de jours nécessaires (Arrondi supérieur)
            // Ex: 4h -> 1 jour | 10h -> 2 jours
            int duree = t.getDureeEstimee();
            int nbJoursNecessaires = (int) Math.ceil((double) duree / HEURES_PAR_JOUR);
            if (nbJoursNecessaires < 1) nbJoursNecessaires = 1;

            // Gestion du débordement de semaine (Max 7 jours affichés)
            // Combien de jours reste-t-il dans la semaine à partir du jour de début ?
            // Ex: Mercredi (index 2) -> Reste 5 jours (Mer, Jeu, Ven, Sam, Dim)
            int joursRestantsSemaine = 7 - indexJourDebut;

            // Le span réel est le minimum entre le besoin et la place disponible
            int spanReel = Math.min(nbJoursNecessaires, joursRestantsSemaine);

            // Création de la barre
            HBox barre = new HBox();
            String couleurHex = t.getColor() != null ? t.getColor() : "#4A90E2";

            // Style de base
            String styleBarre = "-fx-background-color: " + couleurHex + "; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-color: derive(" + couleurHex + ", -20%); " +
                    "-fx-border-radius: 4; ";

            // Si la tâche est coupée (elle continue la semaine d'après), on ouvre la bordure droite
            if (nbJoursNecessaires > joursRestantsSemaine) {
                styleBarre += "-fx-border-width: 1 0 1 1; " + // Pas de bordure à droite
                        "-fx-background-radius: 4 0 0 4; " +
                        "-fx-border-radius: 4 0 0 4;";
            }

            barre.setStyle(styleBarre);
            barre.setPrefHeight(20);
            GridPane.setMargin(barre, new Insets(2, 5, 2, 5));

            Label lblDuree = new Label(t.getDureeEstimee() + "h");
            lblDuree.setTextFill(contrasteCouleur(couleurHex));
            lblDuree.setFont(Font.font("System", FontWeight.BOLD, 10));
            barre.setAlignment(Pos.CENTER);
            barre.getChildren().add(lblDuree);

            // Interactions
            barre.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
            barre.setCursor(javafx.scene.Cursor.HAND);

            // Note: j'utilise getCommentaire() comme vu précédemment
            String description = t.getCommentaire() != null ? t.getCommentaire() : "";
            Tooltip tp = new Tooltip(t.getLibelle() + "\n" + description + "\nÉtat: " + getEtatString(t.getEtat()));
            Tooltip.install(barre, tp);

            ContextMenu contextMenu = new ContextMenu();
            MenuItem itemArchiver = new MenuItem("Archiver la tâche");
            itemArchiver.setOnAction(new ControleurArchiverTache(modele, t));
            contextMenu.getItems().add(itemArchiver);
            barre.setOnContextMenuRequested(e -> contextMenu.show(barre, e.getScreenX(), e.getScreenY()));

            // 4. Ajout et Application du SPAN
            // indexJourDebut + 1 car la colonne 0 est le titre
            ligne.add(barre, indexJourDebut + 1, 0);
            GridPane.setColumnSpan(barre, spanReel);
        }

        conteneurLignes.getChildren().add(ligne);

        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                ajouterLigneTache(enfant, niveauIndent + 1);
            }
        }
    }

    private int getIndexJour(String jour) {
        if (jour == null) return -1;
        for (int i = 0; i < JOURS_SEMAINE.length; i++) {
            if (JOURS_SEMAINE[i].equalsIgnoreCase(jour)) return i;
        }
        return 0;
    }

    private Color contrasteCouleur(String hexColor) {
        try {
            Color c = Color.web(hexColor);
            double brightness = c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114;
            return brightness > 0.5 ? Color.BLACK : Color.WHITE;
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    private String getEtatString(int etat) {
        switch (etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            default: return "";
        }
    }
}