package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurCreationTacheVueListe;
import com.example.trello.Controleur.ControleurModificationTacheVueListe;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example. trello.Modele. Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene. control.*;
import javafx.scene.layout.*;
import javafx.scene. paint.Color;
import java.util.List;
import java.util.Map;

public class VueListe extends VBox implements Observateur {
    private Modele modele;
    private VBox joursContainer;

    private final String[] joursNoms = {"Lundi", "Mardi", "Mercredi", "Jeudi",
            "Vendredi", "Samedi", "Dimanche"};
    private final String[] joursMinuscules = {"lundi", "mardi", "mercredi", "jeudi",
            "vendredi", "samedi", "dimanche"};

    public VueListe(Modele modele) {
        this.modele = modele;
        initialiserVue();
        modele.ajouterObservateur(this);
        actualiser(modele);
    }

    private void initialiserVue() {
        this.setSpacing(0);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #F5F5F5;");

        // Conteneur principal avec ScrollPane
        joursContainer = new VBox(15);
        joursContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(joursContainer);
        scrollPane.setFitToWidth(true);
        scrollPane. setStyle("-fx-background-color: #F5F5F5;");

        this.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Crée les lignes pour chaque jour
        for (int i = 0; i < 7; i++) {
            HBox ligneJour = creerLigneJour(i);
            joursContainer.getChildren().add(ligneJour);
        }
    }

    private HBox creerLigneJour(int indexJour) {
        HBox ligneJour = new HBox(15);
        ligneJour.setAlignment(Pos. CENTER_LEFT);
        ligneJour.setPrefHeight(120);

        String jourNom = joursNoms[indexJour];
        String jourCle = joursMinuscules[indexJour];

        // Label du jour (colonne fixe à gauche)
        VBox labelJourContainer = new VBox();
        labelJourContainer.setAlignment(Pos.CENTER);
        labelJourContainer.setPrefWidth(120);
        labelJourContainer.setMinWidth(120);
        labelJourContainer.setMaxWidth(120);
        labelJourContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #E0E0E0; -fx-border-radius: 5;");
        labelJourContainer.setPadding(new Insets(10));

        Label labelJour = new Label(jourNom);
        labelJour.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        labelJourContainer.getChildren().add(labelJour);

        // Conteneur des tâches (scrollable horizontalement)
        HBox tachesContainer = new HBox(10);
        tachesContainer. setAlignment(Pos.CENTER_LEFT);
        tachesContainer.setPadding(new Insets(5));
        tachesContainer. setId("taches-" + jourCle);

        ScrollPane scrollTaches = new ScrollPane(tachesContainer);
        scrollTaches.setFitToHeight(true);
        scrollTaches.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollTaches.setStyle("-fx-background-color: transparent; -fx-background:  transparent;");
        HBox.setHgrow(scrollTaches, Priority.ALWAYS);

        // Bouton pour ajouter une tâche
        Button btnAjouter = creerBoutonRond("+");
        btnAjouter.setStyle("-fx-background-color: #A0A0A0; -fx-text-fill: white; " +
                "-fx-font-size: 20px; -fx-background-radius: 15; -fx-cursor: hand;");
        btnAjouter.setOnAction(e -> ouvrirControleurCreation(jourCle));

        VBox buttonContainer = new VBox(btnAjouter);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPrefWidth(60);

        ligneJour.getChildren().addAll(labelJourContainer, scrollTaches, buttonContainer);

        return ligneJour;
    }

    /**
     * Crée un bouton rond
     */
    private Button creerBoutonRond(String text) {
        Button button = new Button(text);
        button.setMinSize(50, 50);
        button.setMaxSize(50, 50);
        return button;
    }

    /**
     * Ouvre le contrôleur de création de tâche
     */
    private void ouvrirControleurCreation(String jour) {
        ControleurCreationTacheVueListe controleur = new ControleurCreationTacheVueListe(modele, jour);
        controleur.showAndWait();
    }

    /**
     * Ouvre le contrôleur de modification de tâche
     */
    private void ouvrirControleurModification(Tache tache) {
        ControleurModificationTacheVueListe controleur = new ControleurModificationTacheVueListe(modele, tache);
        controleur.showAndWait();
    }

    private Node creerVueTache(Tache tache, String jour) {
        VBox vueTache = new VBox(5);
        vueTache.setPadding(new Insets(10));
        vueTache.setPrefWidth(180);
        vueTache.setMinWidth(180);
        vueTache.setMaxWidth(180);
        vueTache.setPrefHeight(90);

        Color couleur = determinerCouleurJour(jour);
        vueTache.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 5; -fx-cursor: hand;",
                toHexString(couleur)));

        Label titre = new Label(tache.getLibelle());
        titre.setStyle(String.format("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: %s;",
                couleur.getBrightness() > 0.5 ? "black" : "white"));
        titre.setWrapText(true);
        titre.setMaxWidth(160);

        vueTache.getChildren().add(titre);

        if (tache.getCommentaire() != null && !tache.getCommentaire().isEmpty()) {
            Label commentaire = new Label(tache. getCommentaire());
            commentaire.setStyle(String.format("-fx-font-size: 11px; -fx-text-fill: %s;",
                    couleur.getBrightness() > 0.5 ? "#333" : "#EEE"));
            commentaire. setWrapText(true);
            commentaire.setMaxWidth(160);
            commentaire.setMaxHeight(40);
            vueTache.getChildren().add(commentaire);
        }

        // État de la tâche
        Label etat = new Label("📌 " + tache.getEtat());
        etat.setStyle(String.format("-fx-font-size: 10px; -fx-text-fill: %s;",
                couleur.getBrightness() > 0.5 ? "#666" : "#CCC"));
        vueTache.getChildren().add(etat);

        // Effet au survol
        final String styleNormal = vueTache.getStyle();
        vueTache.setOnMouseEntered(e -> {
            Color couleurPlus = couleur.brighter();
            vueTache. setStyle(String.format("-fx-background-color: %s; -fx-background-radius:  5; " +
                            "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);",
                    toHexString(couleurPlus)));
        });

        vueTache.setOnMouseExited(e -> vueTache.setStyle(styleNormal));

        // Double-clic pour modifier
        vueTache.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ouvrirControleurModification(tache);
            }
        });

        return vueTache;
    }

    /**
     * Détermine la couleur d'un jour
     */
    private Color determinerCouleurJour(String jour) {
        switch (jour. toLowerCase()) {
            case "lundi":
                return Color.rgb(255, 182, 193); // Rose clair
            case "mardi":
                return Color.rgb(255, 218, 185); // Pêche
            case "mercredi":
                return Color.rgb(255, 255, 153); // Jaune clair
            case "jeudi":
                return Color.rgb(173, 216, 230); // Bleu clair
            case "vendredi":
                return Color.rgb(221, 160, 221); // Violet clair
            case "samedi":
                return Color.rgb(144, 238, 144); // Vert clair
            case "dimanche":
                return Color.rgb(255, 160, 122); // Saumon
            default:
                return Color. LIGHTBLUE;
        }
    }

    /**
     * Convertit une couleur en format hexadécimal
     */
    private String toHexString(Color color) {
        return String. format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @Override
    public void actualiser(Sujet sujet) {
        if (sujet instanceof Modele) {
            Map<String, List<Tache>> tachesSemaine = modele.getTachesSemaine();

            // Met à jour chaque ligne de jour
            for (int i = 0; i < 7; i++) {
                HBox ligneJour = (HBox) joursContainer.getChildren().get(i);
                ScrollPane scrollPane = (ScrollPane) ligneJour.getChildren().get(1);
                HBox conteneur = (HBox) scrollPane.getContent();
                conteneur.getChildren().clear();

                String jourCle = joursMinuscules[i];
                List<Tache> tachesJour = tachesSemaine.get(jourCle);

                if (tachesJour != null && !tachesJour.isEmpty()) {
                    for (Tache tache : tachesJour) {
                        conteneur.getChildren().add(creerVueTache(tache, jourCle));
                    }
                } else {
                    // Message si aucune tâche
                    Label aucuneTache = new Label("Aucune tâche");
                    aucuneTache.setStyle("-fx-text-fill: #999; -fx-font-style: italic; " +
                            "-fx-padding: 30 0 0 20;");
                    conteneur. getChildren().add(aucuneTache);
                }
            }
        }
    }
}