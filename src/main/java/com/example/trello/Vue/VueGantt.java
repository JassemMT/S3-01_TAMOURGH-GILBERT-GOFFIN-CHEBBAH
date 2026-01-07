package com.example.trello.Vue;

import com.example.trello.Controleur.*;
import com.example.trello.Modele.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;

import java.util.*;

public class VueGantt extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurLignes;
    private Pane layerFleches;
    // Map pour stocker la relation entre une tâche et son composant graphique
    private Map<Tache, HBox> barresGraphiques = new HashMap<>();

    private static final int HEURES_PAR_JOUR = 8;
    private static final String[] JOURS_SEMAINE = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

    public VueGantt(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        Label titreVue = new Label("Gantt : Hiérarchie Filles (Haut) -> Mères (Bas)");
        titreVue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");
        setTop(titreVue);

        // StackPane pour superposer les flèches sur les tâches
        StackPane stackCentral = new StackPane();

        VBox layoutTableau = new VBox(0);

        // --- Header ---
        GridPane header = new GridPane();
        header.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
        header.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(30); }});
        for (int i = 0; i < 7; i++) {
            header.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(10); }});
            Label lbl = new Label(JOURS_SEMAINE[i]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-padding: 8; -fx-font-weight: bold; -fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");
            header.add(lbl, i + 1, 0);
        }
        layoutTableau.getChildren().add(header);

        conteneurLignes = new VBox(0);
        layoutTableau.getChildren().add(conteneurLignes);

        layerFleches = new Pane();
        layerFleches.setMouseTransparent(true); // Pour cliquer sur les barres à travers

        stackCentral.getChildren().addAll(layoutTableau, layerFleches);

        ScrollPane scroll = new ScrollPane(stackCentral);
        scroll.setFitToWidth(true);
        setCenter(scroll);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            barresGraphiques.clear();
            layerFleches.getChildren().clear();
            construireGantt(((Modele) s).getTaches());

            // On force un petit délai pour laisser le temps au layout de se dessiner
            // indispensable pour calculer les positions (Bounds)
            Platform.runLater(() -> {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                dessinerToutesLesFleches();
            });
        }
    }

    private void construireGantt(List<Tache> taches) {
        conteneurLignes.getChildren().clear();
        Set<Tache> enfants = new HashSet<>();
        for (Tache t : taches) if (t.aDesEnfants()) enfants.addAll(t.getEnfants());

        // On commence par les tâches racines
        for (Tache t : taches) {
            if (!enfants.contains(t)) {
                parcoursEtAjout(t, 0);
            }
        }
    }

    private void parcoursEtAjout(Tache t, int niveau) {
        // INVERSION : On traite les enfants d'abord pour qu'ils soient au-dessus
        if (t.aDesEnfants()) {
            for (Tache fille : t.getEnfants()) {
                parcoursEtAjout(fille, niveau + 1);
            }
        }
        ajouterLigneTache(t, niveau);
    }

    private void ajouterLigneTache(Tache t, int niveauIndent) {
        GridPane ligne = new GridPane();
        ligne.setMinHeight(45);
        ligne.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0; -fx-background-color: white;");

        ligne.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(30); }});
        for (int i = 0; i < 7; i++) {
            ligne.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(10); }});
            Pane guide = new Pane();
            guide.setStyle("-fx-border-color: #f9f9f9; -fx-border-width: 0 1 0 0;");
            ligne.add(guide, i + 1, 0);
        }

        // Nom de la tâche
        HBox boxNom = new HBox(5);
        boxNom.setAlignment(Pos.CENTER_LEFT);
        boxNom.setPadding(new Insets(0, 0, 0, 10 + (niveauIndent * 20)));
        Label lblNom = new Label((niveauIndent > 0 ? "↳ " : "") + t.getLibelle());
        lblNom.setFont(Font.font("System", niveauIndent == 0 ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        boxNom.getChildren().add(lblNom);
        ligne.add(boxNom, 0, 0);

        // Barre de temps
        int idx = getIndexJour(t.getJour());
        if (idx != -1) {
            AnchorPane conteneur = new AnchorPane();
            ligne.add(conteneur, 1, 0, 7, 1);

            double jourPct = 100.0 / 7.0;
            HBox barre = new HBox();
            barre.setAlignment(Pos.CENTER);
            barre.setPrefHeight(25);

            String col = t.getColor() != null ? t.getColor() : "#4A90E2";
            barre.setStyle("-fx-background-color: " + col + "; -fx-background-radius: 4;");

            Label lblH = new Label(t.getDureeEstimee() + "h");
            lblH.setTextFill(contrasteCouleur(col));
            barre.getChildren().add(lblH);

            // Bindings pour le responsive
            barre.translateXProperty().bind(conteneur.widthProperty().multiply((idx * jourPct) / 100.0));
            barre.prefWidthProperty().bind(conteneur.widthProperty().multiply(((t.getDureeEstimee() / (double)HEURES_PAR_JOUR) * jourPct) / 100.0));
            AnchorPane.setTopAnchor(barre, 10.0);

            conteneur.getChildren().add(barre);
            barresGraphiques.put(t, barre); // Enregistrement pour les flèches

            barre.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        }

        conteneurLignes.getChildren().add(ligne);
    }

    private void dessinerToutesLesFleches() {
        layerFleches.getChildren().clear();
        for (Tache mere : barresGraphiques.keySet()) {
            if (mere.aDesEnfants()) {
                for (Tache fille : mere.getEnfants()) {
                    if (barresGraphiques.containsKey(fille)) {
                        tracerLien(fille, mere);
                    }
                }
            }
        }
    }

    private void tracerLien(Tache fille, Tache mere) {
        HBox bFille = barresGraphiques.get(fille);
        HBox bMere = barresGraphiques.get(mere);

        // On récupère les coordonnées relatives au layerFleches
        Bounds fBounds = bFille.localToScene(bFille.getBoundsInLocal());
        Bounds mBounds = bMere.localToScene(bMere.getBoundsInLocal());
        Bounds sceneBounds = layerFleches.localToScene(layerFleches.getBoundsInLocal());

        if (sceneBounds == null) return;

        // Point de départ : Fin de la fille (à droite)
        double startX = fBounds.getMaxX() - sceneBounds.getMinX();
        double startY = fBounds.getMinY() + (fBounds.getHeight() / 2) - sceneBounds.getMinY();

        // Point d'arrivée : Début de la mère (à gauche)
        double endX = mBounds.getMinX() - sceneBounds.getMinX();
        double endY = mBounds.getMinY() + (mBounds.getHeight() / 2) - sceneBounds.getMinY();

        CubicCurve courbe = new CubicCurve();
        courbe.setStartX(startX); courbe.setStartY(startY);
        courbe.setEndX(endX); courbe.setEndY(endY);

        // Points de contrôle pour créer la courbe en S
        courbe.setControlX1(startX + 30); courbe.setControlY1(startY);
        courbe.setControlX2(endX - 30); courbe.setControlY2(endY);

        courbe.setStroke(Color.web("#666666", 0.6));
        courbe.setStrokeWidth(1.5);
        courbe.setFill(null);

        layerFleches.getChildren().add(courbe);
    }

    // --- Helpers ---
    private int getIndexJour(String j) {
        for (int i = 0; i < JOURS_SEMAINE.length; i++) if (JOURS_SEMAINE[i].equalsIgnoreCase(j)) return i;
        return -1;
    }

    private Color contrasteCouleur(String hex) {
        try {
            Color c = Color.web(hex);
            return (c.getRed()*0.299 + c.getGreen()*0.587 + c.getBlue()*0.114) > 0.6 ? Color.BLACK : Color.WHITE;
        } catch (Exception e) { return Color.BLACK; }
    }
}