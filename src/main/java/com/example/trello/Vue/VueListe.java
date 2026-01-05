package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurArchiverTache;
import com.example.trello.Controleur.ControleurCreerTache;
import com.example.trello.Controleur.ControleurOuvrirEditeur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VueListe extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurPrincipal;

    private static final String[] ORDRE_JOURS = {
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    };

    public VueListe(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        HBox entete = new HBox(10);
        entete.setPadding(new Insets(10));
        entete.setAlignment(Pos.CENTER_LEFT);

        Label titreVue = new Label("Vue Liste");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnAjouter = new Button("+ Nouvelle Tâche");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(new ControleurCreerTache(modele, "Principal"));

        entete.getChildren().addAll(titreVue, btnAjouter);
        setTop(entete);

        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(conteneurPrincipal);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        setCenter(scroll);
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            rafraichirDonnees(m.getTaches());
        }
    }

    private void rafraichirDonnees(List<Tache> lesTaches) {
        conteneurPrincipal.getChildren().clear();

        Map<Tache, Tache> parentMap = new HashMap<>();
        for (Tache t : lesTaches) {
            if (t.aDesEnfants()) {
                for (Tache enfant : t.getEnfants()) {
                    parentMap.put(enfant, t);
                }
            }
        }

        for (String jour : ORDRE_JOURS) {
            List<Tache> toutesTachesDuJour = lesTaches.stream()
                    .filter(t -> jour.equals(t.getJour()))
                    .collect(Collectors.toList());

            List<Tache> racinesVisuellesDuJour = new ArrayList<>();
            for (Tache t : toutesTachesDuJour) {
                Tache parent = parentMap.get(t);
                boolean estRacineAbsolue = (parent == null);

                if (!estRacineAbsolue) {
                    boolean parentEstAujourdhui = parent.getJour().equals(jour);
                    if (!parentEstAujourdhui) {
                        racinesVisuellesDuJour.add(t);
                    }
                } else {
                    racinesVisuellesDuJour.add(t);
                }
            }

            // --- MODIFICATION : On appelle construireSectionJour QUOI QU'IL ARRIVE ---
            // On a supprimé le "if (!racinesVisuellesDuJour.isEmpty())"
            construireSectionJour(jour, racinesVisuellesDuJour);
        }
    }

    private void construireSectionJour(String titreJour, List<Tache> tachesRacines) {
        VBox section = new VBox(10);

        // Style un peu plus léger pour le conteneur du jour
        section.setStyle("-fx-background-color: #FAFAFA; -fx-background-radius: 5; -fx-padding: 10;");

        Label lblJour = new Label(titreJour);
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblJour.setTextFill(Color.DARKSLATEBLUE);
        section.getChildren().add(lblJour);

        // --- GESTION DU VIDE ---
        if (tachesRacines.isEmpty()) {
            Label lblVide = new Label("Aucune tâche prévue");
            lblVide.setTextFill(Color.LIGHTGRAY);
            lblVide.setFont(Font.font("System", FontPosture.ITALIC, 12));
            lblVide.setPadding(new Insets(0, 0, 0, 10));
            section.getChildren().add(lblVide);
        } else {
            // Affichage normal s'il y a des tâches
            GridPane grille = new GridPane();
            grille.setHgap(10);
            grille.setVgap(8);
            grille.setPadding(new Insets(0, 0, 0, 10));

            ColumnConstraints colLibelle = new ColumnConstraints(280);
            ColumnConstraints colEtat = new ColumnConstraints(100);
            ColumnConstraints colColonne = new ColumnConstraints(100);
            ColumnConstraints colDuree = new ColumnConstraints(60);
            ColumnConstraints colComm = new ColumnConstraints(150, 150, Double.MAX_VALUE);
            colComm.setHgrow(Priority.ALWAYS);
            ColumnConstraints colActions = new ColumnConstraints(50);

            grille.getColumnConstraints().addAll(colLibelle, colEtat, colColonne, colDuree, colComm, colActions);

            int row = 0;
            for (Tache t : tachesRacines) {
                row = ajouterLigneTache(grille, t, row, 0);
            }
            section.getChildren().add(grille);
        }

        conteneurPrincipal.getChildren().add(section);
    }

    private int ajouterLigneTache(GridPane grille, Tache t, int row, int niveauIndent) {
        // ... (Cette méthode reste exactement la même que précédemment) ...

        HBox boxTitre = new HBox(5);
        boxTitre.setAlignment(Pos.CENTER_LEFT);
        boxTitre.setPadding(new Insets(0, 0, 0, niveauIndent * 20));

        if (niveauIndent > 0) {
            Label indicateur = new Label("↳");
            indicateur.setTextFill(Color.GRAY);
            boxTitre.getChildren().add(indicateur);
        }

        Label lLibelle = new Label(t.getLibelle());
        lLibelle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        lLibelle.setCursor(javafx.scene.Cursor.HAND);
        lLibelle.setTooltip(new Tooltip("Double-cliquez pour éditer"));
        lLibelle.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        boxTitre.getChildren().add(lLibelle);

        Label lEtat = creerBadgeEtat(t.getEtat());
        Label lColonne = new Label(t.getColonne());
        Label lDuree = new Label(t.getDureeEstimee() + "h");
        String commentaireText = t.getCommentaire() != null ? t.getCommentaire() : "";
        Label lComm = new Label(commentaireText);
        lComm.setTextFill(Color.GRAY);
        lComm.setFont(Font.font("System", FontPosture.ITALIC, 12));
        lComm.setWrapText(false);

        Button btnArchiver = new Button("✖");
        btnArchiver.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-cursor: hand;");
        btnArchiver.setTooltip(new Tooltip("Archiver la tâche"));
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, t));

        grille.add(boxTitre, 0, row);
        grille.add(lEtat, 1, row);
        grille.add(lColonne, 2, row);
        grille.add(lDuree, 3, row);
        grille.add(lComm, 4, row);
        grille.add(btnArchiver, 5, row);

        row++;

        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                if (enfant.getJour().equals(t.getJour())) {
                    row = ajouterLigneTache(grille, enfant, row, niveauIndent + 1);
                } else {
                    HBox boxInfo = new HBox(5);
                    boxInfo.setAlignment(Pos.CENTER_LEFT);
                    boxInfo.setPadding(new Insets(0, 0, 0, (niveauIndent + 1) * 20));

                    Label lblFleche = new Label("↳");
                    lblFleche.setTextFill(Color.LIGHTGRAY);

                    Label lblInfo = new Label("📅 (" + enfant.getJour() + ") : " + enfant.getLibelle());
                    lblInfo.setTextFill(Color.GRAY);
                    lblInfo.setFont(Font.font("System", FontPosture.ITALIC, 12));

                    boxInfo.getChildren().addAll(lblFleche, lblInfo);

                    grille.add(boxInfo, 0, row);
                    GridPane.setColumnSpan(boxInfo, 4);
                    row++;
                }
            }
        }
        return row;
    }

    private Label creerBadgeEtat(int etat) {
        String text = "";
        String colorStyle = "-fx-background-color: #E0E0E0;";
        switch (etat) {
            case Tache.ETAT_A_FAIRE: text="À faire"; colorStyle="-fx-background-color: #ddd; -fx-text-fill: black;"; break;
            case Tache.ETAT_EN_COURS: text="En cours"; colorStyle="-fx-background-color: #fff3cd; -fx-text-fill: #856404;"; break;
            case Tache.ETAT_TERMINE: text="Terminé"; colorStyle="-fx-background-color: #d4edda; -fx-text-fill: #155724;"; break;
            case Tache.ETAT_ARCHIVE: text="Archivé"; colorStyle="-fx-background-color: #f8d7da; -fx-text-fill: #721c24;"; break;
        }
        Label badge = new Label(text);
        badge.setStyle(colorStyle + " -fx-background-radius: 10; -fx-padding: 2 8 2 8; -fx-font-size: 11px;");
        return badge;
    }
}