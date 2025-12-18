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
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        Set<Tache> sontDesEnfants = new HashSet<>();
        for (Tache t : lesTaches) {
            if (t.aDesEnfants()) {
                sontDesEnfants.addAll(t.getEnfants());
            }
        }

        for (String jour : ORDRE_JOURS) {
            List<Tache> tachesDuJour = lesTaches.stream()
                    .filter(t -> jour.equals(t.getJour()) && !sontDesEnfants.contains(t))
                    .collect(Collectors.toList());

            if (!tachesDuJour.isEmpty()) {
                construireSectionJour(jour, tachesDuJour);
            }
        }
    }

    private void construireSectionJour(String titreJour, List<Tache> tachesRacines) {
        VBox section = new VBox(10);

        Label lblJour = new Label(titreJour);
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblJour.setTextFill(Color.DARKSLATEBLUE);
        section.getChildren().add(lblJour);

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(8);
        grille.setPadding(new Insets(0, 0, 0, 10));

        // --- DÉFINITION DES COLONNES (Mise à jour) ---
        ColumnConstraints colLibelle = new ColumnConstraints(280);
        ColumnConstraints colEtat = new ColumnConstraints(100);
        ColumnConstraints colColonne = new ColumnConstraints(100);
        ColumnConstraints colDuree = new ColumnConstraints(60);

        // Nouvelle colonne Commentaire (Prend toute la place restante)
        ColumnConstraints colComm = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        colComm.setHgrow(Priority.ALWAYS);

        ColumnConstraints colActions = new ColumnConstraints(50);

        grille.getColumnConstraints().addAll(colLibelle, colEtat, colColonne, colDuree, colComm, colActions);

        int row = 0;
        for (Tache t : tachesRacines) {
            row = ajouterLigneTache(grille, t, row, 0);
        }

        section.getChildren().add(grille);
        conteneurPrincipal.getChildren().add(section);
    }

    private int ajouterLigneTache(GridPane grille, Tache t, int row, int niveauIndent) {

        //Libellé
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

        //Autres infos
        Label lEtat = creerBadgeEtat(t.getEtat());
        Label lColonne = new Label(t.getColonne());
        Label lDuree = new Label(t.getDureeEstimee() + "j");
        //Description/commentaire
        String commentaireText = t.getCommentaire() != null ? t.getCommentaire() : "";
        Label lComm = new Label(commentaireText);
        lComm.setTextFill(Color.GRAY);
        lComm.setFont(Font.font("System", FontPosture.ITALIC, 12));
        lComm.setWrapText(false); // On évite que ça casse la ligne, ou true si tu préfères

        //Actions
        Button btnArchiver = new Button("✖");
        btnArchiver.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-cursor: hand;");
        btnArchiver.setTooltip(new Tooltip("Archiver la tâche"));
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, t));

        // Ajout à la grille dans l'ordre des colonnes
        grille.add(boxTitre, 0, row);
        grille.add(lEtat, 1, row);
        grille.add(lColonne, 2, row);
        grille.add(lDuree, 3, row);
        grille.add(lComm, 4, row);     // Nouvelle colonne
        grille.add(btnArchiver, 5, row); // Décalé en 5ème position

        row++;

        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                row = ajouterLigneTache(grille, enfant, row, niveauIndent + 1);
            }
        }
        return row;
    }

    private Label creerBadgeEtat(int etat) {
        String text = "";
        String colorStyle = "-fx-background-color: #E0E0E0;";
        switch (etat) {
            case Tache.ETAT_A_FAIRE:
                text = "À faire";
                colorStyle = "-fx-background-color: #ddd; -fx-text-fill: black;";
                break;
            case Tache.ETAT_EN_COURS:
                text = "En cours";
                colorStyle = "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
                break;
            case Tache.ETAT_TERMINE:
                text = "Terminé";
                colorStyle = "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
                break;
            case Tache.ETAT_ARCHIVE:
                text = "Archivé";
                colorStyle = "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
                break;
        }
        Label badge = new Label(text);
        badge.setStyle(colorStyle + " -fx-background-radius: 10; -fx-padding: 2 8 2 8; -fx-font-size: 11px;");
        return badge;
    }
}
