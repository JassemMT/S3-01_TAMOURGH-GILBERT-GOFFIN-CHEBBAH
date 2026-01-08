package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurArchiverTache;
import com.example.trello.Controleur.ControleurOuvrirEditeur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
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

    private Pane layerFleches; // Couche transparente pour les flèches
    private java.util.Map<Tache, HBox> barresGraphiques = new java.util.HashMap<>(); // Stocke les barres pour trouver leurs positions

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

    // initialisation de l'interface graphique
    private void initialiserInterface() {
        // titre de la vue
        Label titreVue = new Label("Vue Gantt (Chronologique)");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15;");
        setTop(titreVue);

        // permet la superposition de plusieurs couches
        StackPane stackCentral = new StackPane(); // Permet la superposition
        VBox layoutCentral = new VBox(0);

        // grille contenant les éléments jours
        headerJours = new GridPane();
        headerJours.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-width: 1 0 1 0;");
        layoutCentral.getChildren().add(headerJours);

        // création d'un conteneur VBox pour les lignes
        conteneurLignes = new VBox(0);
        layoutCentral.getChildren().add(conteneurLignes);

        // Initialisation de la couche des flèches
        layerFleches = new Pane();
        layerFleches.setMouseTransparent(true); // Pour cliquer sur les tâches à travers

        // On empile : le tableau en dessous, les flèches au dessus
        stackCentral.getChildren().addAll(layoutCentral, layerFleches);

        // création d'un scrollPane
        scrollPane = new ScrollPane(stackCentral); // On met le stack dans le scroll
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }

    // méthode pour nettoyer la vue et reconstruire le tout
    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            // On vide tout pour repartir sur une base propre
            barresGraphiques.clear();
            layerFleches.getChildren().clear();
            conteneurLignes.getChildren().clear();

            // On reconstruit la structure (dates, colonnes, lignes)
            // C'est ici que calculerLimitesTemporelles est appelé
            construireGantt(((Modele) s).getTaches());

            // On attend que JavaFX ait fini de dessiner les barres
            // pour calculer les coordonnées des flèches
            Platform.runLater(() -> {
                dessinerToutesLesFleches();
            });
        }
    }
    // méthode permettant de créer les éléments du diagramme
    private void construireGantt(List<Tache> taches) {
        // nettoyage des éléments dans la vue gantt
        conteneurLignes.getChildren().clear();
        headerJours.getChildren().clear();
        headerJours.getColumnConstraints().clear();

        // test si la liste des taches est vide
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

    // permet de déterminer la date min et la date max à afficher
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

    // Permet de construire le header avec les dates, tache etc
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

    // Permet de créer les barres correspondantes aux taches
    private void ajouterLigneTache(Tache t, int niveauIndent) {
        GridPane ligne = new GridPane();
        ligne.setMinHeight(40);
        ligne.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

        // Configuration des colonnes (doit être IDENTIQUE au Header)
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

        // colonne 0 nom tache
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
        boxNom.setCursor(Cursor.HAND);
        ligne.add(boxNom, 0, 0);

        // barre gantt
        // Calculer le décalage par rapport au début du projet
        long joursDepuisDebut = ChronoUnit.DAYS.between(dateDebutProjet, t.getDateDebut());

        // Vérifie si la tâche commence avant le début (ne devrait pas arriver avec notre calcul min/max)
        if (joursDepuisDebut < 0) joursDepuisDebut = 0;

        // Calculer la durée en jours
        int dureeJours = t.getDureeEstimee();
        if (dureeJours < 1) dureeJours = 1; // Minimum visuel

        // Création du conteneur pour placer la barre
        AnchorPane conteneurBarre = new AnchorPane();
        // Le conteneur s'étend sur toutes les colonnes de jours (de 1 à la fin)
        ligne.add(conteneurBarre, 1, 0, (int)nombreJoursTotal, 1);

        // Calcul de position en pourcentage
        double largeurUnJour = 100.0 / nombreJoursTotal;
        double startPercent = joursDepuisDebut * largeurUnJour;
        double widthPercent = dureeJours * largeurUnJour;

        HBox barre = new HBox();
        barre.setAlignment(Pos.CENTER);
        barre.setPrefHeight(20);

        // implémentation d'une couleur par défaut pour les taches n'en ayant pas
        String couleurHex = t.getColor() != null ? t.getColor() : "#4A90E2";
        barre.setStyle("-fx-background-color: " + couleurHex + "; " +
                "-fx-background-radius: 4; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 2, 0, 0, 1);");

        // ajout de la durée en jour sur la barre gantt
        Label lblDuree = new Label(dureeJours + "j");
        lblDuree.setTextFill(contrasteCouleur(couleurHex));
        lblDuree.setFont(Font.font("System", FontWeight.BOLD, 9));
        barre.getChildren().add(lblDuree);

        // Liaison pour que la barre suive le redimensionnement de la fenêtre
        barre.translateXProperty().bind(conteneurBarre.widthProperty().multiply(startPercent / 100.0));
        barre.prefWidthProperty().bind(conteneurBarre.widthProperty().multiply(widthPercent / 100.0));

        AnchorPane.setTopAnchor(barre, 10.0);

        // Interactions
        barre.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        barre.setCursor(Cursor.HAND);

        // permet d'afficher une petite popup lorsque l'on survole une barre dans la vue gantt
        Tooltip tp = new Tooltip(t.getLibelle() + "\nDébut : " + t.getDateDebut() + "\nDurée : " + dureeJours + " jours");
        Tooltip.install(barre, tp);

        // création d'un menu pour gérer l'archivage
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemArchiver = new MenuItem("Archiver");
        itemArchiver.setOnAction(new ControleurArchiverTache(modele, t));
        contextMenu.getItems().add(itemArchiver);
        barre.setOnContextMenuRequested(e -> contextMenu.show(barre, e.getScreenX(), e.getScreenY()));

        // ajout des éléments graphiques dans les différents conteneurs
        conteneurBarre.getChildren().add(barre);
        barresGraphiques.put(t, barre);
        conteneurLignes.getChildren().add(ligne);


        // permet de créer les barres pour les sous-taches d'une tache mère
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                if (!enfant.isArchived()) {
                    ajouterLigneTache(enfant, niveauIndent + 1);
                }
            }
        }
    }

    // Méthode permettant de créer toutes les relations sur le diagramme de gantt
    private void dessinerToutesLesFleches() {
        layerFleches.getChildren().clear();
        for (Tache mere : barresGraphiques.keySet()) {
            if (mere.aDesEnfants()) {
                for (Tache enfant : mere.getEnfants()) {
                    if (barresGraphiques.containsKey(enfant) && !enfant.isArchived()) {
                        tracerLienBezier(enfant, mere);
                    }
                }
            }
        }
    }

    // Méthode permettant de créer un lien, ici une flèche pour représenter le lien entre les tâches mères et filles
    private void tracerLienBezier(Tache enfant, Tache mere) {
        HBox bEnfant = barresGraphiques.get(enfant);
        HBox bMere = barresGraphiques.get(mere);

        // Récupération des positions absolues dans la scène, les bordures des barres de taches
        Bounds boundsEnfant = bEnfant.localToScene(bEnfant.getBoundsInLocal());
        Bounds boundsMere = bMere.localToScene(bMere.getBoundsInLocal());
        Bounds boundsLayer = layerFleches.localToScene(layerFleches.getBoundsInLocal());

        if (boundsLayer == null) return;

        // Permet d'obtenir les coordonnées des deux barres (fille, mere) pour définir la flèche ensuite
        // point de départ
        double xSortie = boundsEnfant.getMaxX() - boundsLayer.getMinX();
        double ySortie = boundsEnfant.getMinY() + (boundsEnfant.getHeight() / 2) - boundsLayer.getMinY();

        // point d'arrivé
        double xEntree = boundsMere.getMinX() - boundsLayer.getMinX();
        double yEntree = boundsMere.getMinY() + (boundsMere.getHeight() / 2) - boundsLayer.getMinY();

        // Permet de dessiner la courbe, en donnant 4 points différents pour le tracet du S
        CubicCurve courbe = new CubicCurve();
        courbe.setStartX(xSortie);
        courbe.setStartY(ySortie);
        courbe.setEndX(xEntree);
        courbe.setEndY(yEntree);

        // Permet de définir le style de la flèche en forme de S et son amplitude via setControlX/Y
        double distance = Math.abs(xEntree - xSortie) * 0.5;
        courbe.setControlX1(xSortie + distance);
        courbe.setControlY1(ySortie);
        courbe.setControlX2(xEntree - distance);
        courbe.setControlY2(yEntree);

        // implémentation d'un style à la fleche
        courbe.setStroke(Color.web("#666666", 0.6));
        courbe.setStrokeWidth(1.5);
        courbe.setFill(null);

        // Permet de créer la pointe de la flèche
        Polygon pointe = new Polygon(0,0, -6,-4, -6,4);
        pointe.setFill(Color.web("#666666", 0.6));
        pointe.setTranslateX(xEntree);
        pointe.setTranslateY(yEntree);

        // ajout de la courbe + pointe pour former la fleche sur le layout layerFleche
        layerFleches.getChildren().addAll(courbe, pointe);
    }
    // Permet de savoir si le contraste entre la couleur du texte et la couleur du fond pose probème ou non
    // cela va déterminer le fait que le texte soit blanc ou noir
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