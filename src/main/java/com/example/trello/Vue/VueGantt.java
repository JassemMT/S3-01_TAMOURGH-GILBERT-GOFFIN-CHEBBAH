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
        // 1. En-tête de la vue
        Label titreVue = new Label("Vue Gantt Hebdomadaire");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15;");
        setTop(titreVue);

        VBox layoutCentral = new VBox(0);

        // 2. Header des Jours avec lignes verticales
        headerJours = new GridPane();
        headerJours.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-width: 1 0 1 0;");

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

            // Bordures verticales du header
            String styleBordure = "-fx-border-color: #ccc; -fx-border-width: 0 1 0 0; -fx-padding: 10 0 10 0;";
            if (i == 0) styleBordure = "-fx-border-color: #ccc; -fx-border-width: 0 1 0 1; -fx-padding: 10 0 10 0;";
            lblJour.setStyle(styleBordure);

            headerJours.add(lblJour, i + 1, 0);
        }

        layoutCentral.getChildren().add(headerJours);

        // 3. Conteneur des lignes de tâches
        conteneurLignes = new VBox(0);
        layoutCentral.getChildren().add(conteneurLignes);

        ScrollPane scroll = new ScrollPane(layoutCentral);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: white;");

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
        ligne.setMinHeight(40);
        // Ligne horizontale de délimitation
        ligne.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

        // Configuration des colonnes
        ColumnConstraints colTitre = new ColumnConstraints();
        colTitre.setPercentWidth(30);
        ligne.getColumnConstraints().add(colTitre);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints colJour = new ColumnConstraints();
            colJour.setPercentWidth(10);
            ligne.getColumnConstraints().add(colJour);

            // Guides visuels verticaux (Le Quadrillage)
            Pane guideVisuel = new Pane();
            String styleGuide = "-fx-border-color: #f2f2f2; -fx-border-width: 0 1 0 0;";
            if (i == 0) styleGuide = "-fx-border-color: #f2f2f2; -fx-border-width: 0 1 0 1;";
            guideVisuel.setStyle(styleGuide);
            guideVisuel.setMouseTransparent(true); // Pour cliquer à travers vers la ligne
            ligne.add(guideVisuel, i + 1, 0);
        }

        // --- PARTIE GAUCHE : TITRE ---
        HBox boxNom = new HBox(5);
        boxNom.setAlignment(Pos.CENTER_LEFT);
        boxNom.setPadding(new Insets(0, 0, 0, 10 + (niveauIndent * 20)));

        if (niveauIndent > 0) {
            Label sub = new Label("↳");
            sub.setTextFill(Color.GRAY);
            boxNom.getChildren().add(sub);
        }

        Label lblNom = new Label(t.getLibelle());
        lblNom.setFont(Font.font("System", niveauIndent == 0 ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        boxNom.getChildren().add(lblNom);
        boxNom.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        boxNom.setCursor(javafx.scene.Cursor.HAND);
        ligne.add(boxNom, 0, 0);

        // --- PARTIE DROITE : BARRE PROPORTIONNELLE ---
        int indexJourDebut = getIndexJour(t.getJour());
        if (indexJourDebut != -1) {
            AnchorPane conteneurBarre = new AnchorPane();
            ligne.add(conteneurBarre, 1, 0, 7, 1);

            double largeurUnJourPct = 100.0 / 7.0;
            double departXPct = indexJourDebut * largeurUnJourPct;
            double largeurBarrePct = (t.getDureeEstimee() / (double) HEURES_PAR_JOUR) * largeurUnJourPct;

            HBox barre = new HBox();
            barre.setAlignment(Pos.CENTER);
            barre.setPrefHeight(24);

            // permet de définir une couleur par défaut si la tache n'en a pas de base
            String couleurHex = t.getColor() != null ? t.getColor() : "#4A90E2";
            barre.setStyle("-fx-background-color: " + couleurHex + "; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-color: rgba(0,0,0,0.1); " +
                    "-fx-border-radius: 5; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");


            // Initialisation de l'élément graphique affichant le nombre d'heure sur la barre de remplissage
            Label lblDuree = new Label(t.getDureeEstimee() + "h");
            // On définit la couleur (soit via contrasteCouleur, soit Color.WHITE)
            Color couleurTexte = contrasteCouleur(couleurHex);
            // On applique la couleur via setTextFill au label lblDuree
            lblDuree.setTextFill(couleurTexte);
            // On force la couleur via CSS pour éviter des conflits de couleur
            lblDuree.setStyle("-fx-text-fill: " + toRGBCode(couleurTexte) + "; -fx-font-weight: bold;");
            barre.getChildren().add(lblDuree);

            // Permet que la taille du graphe soit proportionnel à la taille de la fenêtre javaFX
            barre.translateXProperty().bind(conteneurBarre.widthProperty().multiply(departXPct / 100.0));
            barre.prefWidthProperty().bind(conteneurBarre.widthProperty().multiply(largeurBarrePct / 100.0));

            // Permet de centrer verticalement la barre dans la ligne, pour les jours
            AnchorPane.setTopAnchor(barre, 8.0);

            // Interactions pour ouvrir l'interface d'éditeur de tache, et de modifier le curseur en tant que main quand le curseur est au dessus du rectangle d'une tache
            barre.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
            barre.setCursor(javafx.scene.Cursor.HAND);

            // Tooltip, permet d'afficher une petite bulle d'information lorsque l'on laisse sa souris sur le rectangle de la tache
            Tooltip tp = new Tooltip(t.getLibelle() + "\nDurée : " + t.getDureeEstimee() + "h\nÉtat : " + getEtatString(t.getEtat()));
            Tooltip.install(barre, tp);

            // Menu contextuel
            ContextMenu contextMenu = new ContextMenu();
            MenuItem itemArchiver = new MenuItem("Archiver la tâche");
            itemArchiver.setOnAction(new ControleurArchiverTache(modele, t));
            contextMenu.getItems().add(itemArchiver);
            barre.setOnContextMenuRequested(e -> contextMenu.show(barre, e.getScreenX(), e.getScreenY()));

            conteneurBarre.getChildren().add(barre);
        }

        conteneurLignes.getChildren().add(ligne);

        // Récursion pour les sous-tâches
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                ajouterLigneTache(enfant, niveauIndent + 1);
            }
        }
    }

    // renvoie le jour via un paramètre string
    private int getIndexJour(String jour) {
        if (jour == null) return -1;
        for (int i = 0; i < JOURS_SEMAINE.length; i++) {
            if (JOURS_SEMAINE[i].equalsIgnoreCase(jour)) return i;
        }
        return -1;
    }

    // méthode permettant de modifier la couleur du texte du nom d'une tâche si la couleur de la tâche fait en sorte que
    // on ne puisse pas voir le texte
    // -> calcule la lisibilité du texte par-dessus une couleur de fond. Son rôle est de décider si le texte écrit sur une barre de tâche doit être noir ou blanc pour rester parfaitement lisible
    private Color contrasteCouleur(String hexColor) {
        try {
            Color c = Color.web(hexColor);
            // Formule de luminosité perçue standard
            // on calcule la luminosité du fond pour savoir si le lblDuree est visible et difficilement percevable
            double brightness = c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114;

            // Si c'est clair (> 0.6), on écrit en noir. Sinon en blanc.
            return brightness > 0.6 ? Color.BLACK : Color.WHITE;
        } catch (Exception e) {
            return Color.BLACK; // Au cas où quelque chose donne une erreur
        }
    }

    // renvoie l'état correspondant à l'entier passé en paramètre
    private String getEtatString(int etat) {
        switch (etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            default: return "Inconnu";
        }
    }
    // Permet de convertir un code RGB d'une couleur , ici un Objet Color en une chaîne de caractères hexadécimales pouvant être utilisées par le CSS
    private String toRGBCode(Color color) {
        // renvoie dans le format suivant #FF5733
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}