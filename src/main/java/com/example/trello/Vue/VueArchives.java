package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurDesarchiverTache;
import com.example.trello.Controleur.ControleurOuvrirEditeur; // On garde l'éditeur pour voir les détails
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VueArchives extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurPrincipal;

    private static final DateTimeFormatter FORMAT_DATE_COMPLET = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    public VueArchives(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    private void initialiserInterface() {
        HBox entete = new HBox(10);
        entete.setPadding(new Insets(10));
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-border-width: 0 0 1 0;");

        Label titreVue = new Label("🗄 Corbeille / Archives");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        // Pas de bouton "Ajouter tâche" dans les archives

        entete.getChildren().add(titreVue);
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
            // IMPORTANT : On appelle la méthode spécifique pour les archives

            rafraichirDonnees(m.getTachesArchives());
        }
    }

    private void rafraichirDonnees(List<Tache> lesTaches) {
        conteneurPrincipal.getChildren().clear();

        if (lesTaches.isEmpty()) {
            Label lblVide = new Label("La corbeille est vide.");
            lblVide.setFont(Font.font("System", FontPosture.ITALIC, 14));
            lblVide.setTextFill(Color.GRAY);
            conteneurPrincipal.getChildren().add(lblVide);
            return;
        }

        // --- Logique de tri par date (similaire VueListe) ---

        Set<LocalDate> datesUtilisees = new HashSet<>();
        for (Tache t : lesTaches) {
            if (t.getDateDebut() != null) datesUtilisees.add(t.getDateDebut());
        }

        if (datesUtilisees.isEmpty()) return;

        LocalDate minDate = Collections.min(datesUtilisees);
        LocalDate maxDate = Collections.max(datesUtilisees);

        LocalDate currentDate = minDate;
        while (!currentDate.isAfter(maxDate)) {
            final LocalDate jourCourant = currentDate;

            List<Tache> tachesDuJour = lesTaches.stream()
                    .filter(t -> t.getDateDebut() != null && t.getDateDebut().isEqual(jourCourant))
                    .collect(Collectors.toList());

            if (!tachesDuJour.isEmpty()) {
                construireSectionJour(currentDate, tachesDuJour);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    private void construireSectionJour(LocalDate dateDuJour, List<Tache> taches) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #FFF; -fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #EEE; -fx-border-radius: 5;");

        String titreFormatte = dateDuJour.format(FORMAT_DATE_COMPLET);
        titreFormatte = titreFormatte.substring(0, 1).toUpperCase() + titreFormatte.substring(1);

        Label lblJour = new Label(titreFormatte);
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblJour.setTextFill(Color.GRAY); // Couleur grise pour montrer que c'est passé/archivé

        section.getChildren().add(lblJour);

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(8);
        grille.setPadding(new Insets(0, 0, 0, 10));

        ColumnConstraints colLibelle = new ColumnConstraints(280);
        ColumnConstraints colEtat = new ColumnConstraints(100);
        ColumnConstraints colColonne = new ColumnConstraints(100);
        ColumnConstraints colDuree = new ColumnConstraints(80);
        ColumnConstraints colComm = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        colComm.setHgrow(Priority.ALWAYS);
        ColumnConstraints colActions = new ColumnConstraints(120); // Plus large pour le texte "Restaurer"

        grille.getColumnConstraints().addAll(colLibelle, colEtat, colColonne, colDuree, colComm, colActions);

        int row = 0;
        for (Tache t : taches) {
            // Note: Dans les archives, on affiche tout à plat ou on garde la hiérarchie ?
            // Ici j'utilise une version simplifiée sans indentation complexe pour éviter
            // les bugs si le parent n'est pas archivé mais l'enfant si.
            row = ajouterLigneTache(grille, t, row);
        }
        section.getChildren().add(grille);
        conteneurPrincipal.getChildren().add(section);
    }

    private int ajouterLigneTache(GridPane grille, Tache t, int row) {
        HBox boxTitre = new HBox(5);
        boxTitre.setAlignment(Pos.CENTER_LEFT);

        Label lLibelle = new Label(t.getLibelle());
        lLibelle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lLibelle.setTextFill(Color.DARKGRAY); // Texte grisé
        // On permet quand même d'ouvrir pour voir les infos
        lLibelle.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        lLibelle.setCursor(javafx.scene.Cursor.HAND);

        boxTitre.getChildren().add(lLibelle);

        Label lEtat = new Label("Archivé");
        lEtat.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11px;");

        Label lColonne = new Label(t.getColonne());
        lColonne.setTextFill(Color.GRAY);

        Label lDuree = new Label(t.getDureeEstimee() + "j");
        lDuree.setTextFill(Color.GRAY);

        Label lComm = new Label(t.getCommentaire());
        lComm.setTextFill(Color.GRAY);
        lComm.setFont(Font.font("System", FontPosture.ITALIC, 12));

        // BOUTON RESTAURER
        Button btnRestaurer = new Button("⟲ RESTAURER");
        btnRestaurer.setStyle("-fx-background-color: transparent; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px;");
        btnRestaurer.setTooltip(new Tooltip("Désarchiver la tâche"));
        btnRestaurer.setOnAction(new ControleurDesarchiverTache(modele, t));

        grille.add(boxTitre, 0, row);
        grille.add(lEtat, 1, row);
        grille.add(lColonne, 2, row);
        grille.add(lDuree, 3, row);
        grille.add(lComm, 4, row);
        grille.add(btnRestaurer, 5, row);

        return row + 1;
    }
}