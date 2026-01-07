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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VueGantt extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurLignes;
    private GridPane headerJours;
    private ScrollPane scrollPane;

    // Plage de dates globale du projet pour l'affichage
    private LocalDate dateDebutProjet;
    private long nombreJoursTotal;

    private static final DateTimeFormatter FORMAT_JOUR = DateTimeFormatter.ofPattern("dd/MM");

    public VueGantt(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);

        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        // 1. En-tête de la vue
        Label titreVue = new Label("Vue Gantt (Chronologique)");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15;");
        setTop(titreVue);

        VBox layoutCentral = new VBox(0);

        // 2. Header des Jours (La grille temporelle)
        headerJours = new GridPane();
        headerJours.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-width: 1 0 1 0;");
        layoutCentral.getChildren().add(headerJours);

        // 3. Conteneur des lignes de tâches
        conteneurLignes = new VBox(0);
        layoutCentral.getChildren().add(conteneurLignes);

        scrollPane = new ScrollPane(layoutCentral);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: white;");

        setCenter(scrollPane);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            construireGantt(((Modele) s).getTaches());
        }
    }

    private void construireGantt(List<Tache> taches) {
        conteneurLignes.getChildren().clear();
        headerJours.getChildren().clear();
        headerJours.getColumnConstraints().clear();

        if (taches.isEmpty()) {
            conteneurLignes.getChildren().add(new Label("Aucune tâche à afficher."));
            return;
        }

        calculerLimitesTemporelles(taches);
        construireHeader();

        Set<Tache> sontDesEnfants = new HashSet<>();
        for (Tache t : taches) {
            if (t.aDesEnfants()) sontDesEnfants.addAll(t.getEnfants());
        }

        for (Tache t : taches) {
            if (!sontDesEnfants.contains(t) && !t.isArchived()) {
                ajouterLigneTache(t, 0);
            }
        }
    }

    private void calculerLimitesTemporelles(List<Tache> taches) {
        LocalDate min = LocalDate.MAX;
        LocalDate max = LocalDate.MIN;

        for (Tache t : taches) {
            if (t.getDateDebut().isBefore(min)) min = t.getDateDebut();
            // La date de fin théorique est dateDebut + durée
            LocalDate finTache = t.getDateDebut().plusDays(Math.max(1, t.getDureeEstimee()));
            if (finTache.isAfter(max)) max = finTache;

            // On regarde aussi les enfants récursivement au cas où
            if (t.aDesEnfants()) {
                // Simplification : on suppose que la boucle principale parcourt tout le monde
                // car modele.getTaches() retourne tout.
            }
        }

        // On ajoute une petite marge (1 jour avant, 2 jours après)
        this.dateDebutProjet = min.minusDays(1);
        LocalDate dateFinProjet = max.plusDays(2);

        // Calcul du nombre de jours total à afficher
        this.nombreJoursTotal = ChronoUnit.DAYS.between(dateDebutProjet, dateFinProjet);
        if (nombreJoursTotal < 7) nombreJoursTotal = 7; // Minimum une semaine d'affichage
    }

    private void construireHeader() {
        // Colonne 0 : Titre de la tâche (Fixe, 25%)
        ColumnConstraints colTitre = new ColumnConstraints();
        colTitre.setPercentWidth(25);
        headerJours.getColumnConstraints().add(colTitre);

        // Colonne vide au dessus des titres
        Label lblTitre = new Label("Tâche");
        lblTitre.setPadding(new Insets(0,0,0,10));
        lblTitre.setFont(Font.font("System", FontWeight.BOLD, 12));
        headerJours.add(lblTitre, 0, 0);

        // Colonnes Suivantes : Les Jours (Répartition équitable sur les 75% restants)
        double largeurJourPct = 75.0 / nombreJoursTotal;

        for (int i = 0; i < nombreJoursTotal; i++) {
            ColumnConstraints colJour = new ColumnConstraints();
            colJour.setPercentWidth(largeurJourPct);
            headerJours.getColumnConstraints().add(colJour);

            LocalDate jourCourant = dateDebutProjet.plusDays(i);
            Label lblJour = new Label(jourCourant.format(FORMAT_JOUR));
            lblJour.setFont(Font.font("System", FontWeight.NORMAL, 10));
            lblJour.setMaxWidth(Double.MAX_VALUE);
            lblJour.setAlignment(Pos.CENTER);

            // Bordure pour séparer les jours
            lblJour.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 0 1;");

            // Marquer "Aujourd'hui"
            if (jourCourant.isEqual(LocalDate.now())) {
                lblJour.setTextFill(Color.RED);
                lblJour.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 0 1; -fx-background-color: #ffebeb;");
            }

            headerJours.add(lblJour, i + 1, 0);
        }
    }

    private void ajouterLigneTache(Tache t, int niveauIndent) {
        GridPane ligne = new GridPane();
        ligne.setMinHeight(40);
        ligne.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

        // --- Configuration des colonnes (Doit être IDENTIQUE au Header) ---
        ColumnConstraints colTitre = new ColumnConstraints();
        colTitre.setPercentWidth(25);
        ligne.getColumnConstraints().add(colTitre);

        double largeurJourPct = 75.0 / nombreJoursTotal;
        for (int i = 0; i < nombreJoursTotal; i++) {
            ColumnConstraints colJour = new ColumnConstraints();
            colJour.setPercentWidth(largeurJourPct);
            ligne.getColumnConstraints().add(colJour);

            // Quadrillage vertical
            Pane guide = new Pane();
            guide.setStyle("-fx-border-color: #f9f9f9; -fx-border-width: 0 0 0 1;");
            // Marquer la colonne d'aujourd'hui
            if (dateDebutProjet.plusDays(i).isEqual(LocalDate.now())) {
                guide.setStyle("-fx-border-color: #ffcccc; -fx-border-width: 0 0 0 1; -fx-background-color: rgba(255, 0, 0, 0.03);");
            }
            ligne.add(guide, i + 1, 0);
        }

        // --- COLONNE 0 : NOM TACHE ---
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

        // --- BARRE GANTT ---
        // 1. Calculer le décalage (offset) par rapport au début du projet
        long joursDepuisDebut = ChronoUnit.DAYS.between(dateDebutProjet, t.getDateDebut());

        // Sécurité : si la tâche commence avant le début (ne devrait pas arriver avec notre calcul min/max)
        if (joursDepuisDebut < 0) joursDepuisDebut = 0;

        // 2. Calculer la durée en jours
        int dureeJours = t.getDureeEstimee();
        if (dureeJours < 1) dureeJours = 1; // Minimum visuel

        // 3. Création du conteneur pour placer la barre
        AnchorPane conteneurBarre = new AnchorPane();
        // Le conteneur s'étend sur toutes les colonnes de jours (de 1 à la fin)
        ligne.add(conteneurBarre, 1, 0, (int)nombreJoursTotal, 1);

        // 4. Calculs de position en pourcentage
        double largeurUnJour = 100.0 / nombreJoursTotal;
        double startPercent = joursDepuisDebut * largeurUnJour;
        double widthPercent = dureeJours * largeurUnJour;

        HBox barre = new HBox();
        barre.setAlignment(Pos.CENTER);
        barre.setPrefHeight(20);

        String couleurHex = t.getColor() != null ? t.getColor() : "#4A90E2";
        barre.setStyle("-fx-background-color: " + couleurHex + "; " +
                "-fx-background-radius: 4; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 2, 0, 0, 1);");

        Label lblDuree = new Label(dureeJours + "j");
        lblDuree.setTextFill(contrasteCouleur(couleurHex));
        lblDuree.setFont(Font.font("System", FontWeight.BOLD, 9));
        barre.getChildren().add(lblDuree);

        // Liaison (Binding) pour que la barre suive le redimensionnement de la fenêtre
        barre.translateXProperty().bind(conteneurBarre.widthProperty().multiply(startPercent / 100.0));
        barre.prefWidthProperty().bind(conteneurBarre.widthProperty().multiply(widthPercent / 100.0));

        AnchorPane.setTopAnchor(barre, 10.0);

        // Interactions
        barre.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        barre.setCursor(javafx.scene.Cursor.HAND);

        Tooltip tp = new Tooltip(t.getLibelle() + "\nDébut : " + t.getDateDebut() + "\nDurée : " + dureeJours + " jours");
        Tooltip.install(barre, tp);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemArchiver = new MenuItem("Archiver");
        itemArchiver.setOnAction(new ControleurArchiverTache(modele, t));
        contextMenu.getItems().add(itemArchiver);
        barre.setOnContextMenuRequested(e -> contextMenu.show(barre, e.getScreenX(), e.getScreenY()));

        conteneurBarre.getChildren().add(barre);

        conteneurLignes.getChildren().add(ligne);


        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                // --- IL MANQUAIT LE FILTRE ---
                if (!enfant.isArchived()) {
                    ajouterLigneTache(enfant, niveauIndent + 1);
                }
            }
        }
    }

    private Color contrasteCouleur(String hexColor) {
        try {
            Color c = Color.web(hexColor);
            double brightness = c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114;
            return brightness > 0.6 ? Color.BLACK : Color.WHITE;
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
}