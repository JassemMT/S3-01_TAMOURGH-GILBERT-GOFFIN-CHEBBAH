package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurDesarchiverTache;
import com.example.trello.Controleur.ControleurOuvrirEditeur;
import com.example.trello.Controleur.ControleurSupprimerTache;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Sujet;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vue dédiée à l'affichage des tâches archivées (Corbeille).
 * <p>
 * Cette vue joue le rôle de "poubelle" ou d'historique. Elle n'affiche que les tâches
 * dont l'état est {@link Tache#ETAT_ARCHIVE}.
 * Elle permet deux actions principales :
 * <ul>
 * <li><b>Restaurer :</b> Remet la tâche dans le tableau actif (état "À FAIRE").</li>
 * <li><b>Supprimer :</b> Efface définitivement la tâche du modèle.</li>
 * </ul>
 * </p>
 */
public class VueArchives extends BorderPane implements Observateur {

    private Modele modele;
    /** Conteneur vertical accueillant la liste des jours/tâches. */
    private VBox conteneurPrincipal;

    /** Formatteur pour afficher les dates en français (ex: "Lundi 12 Octobre 2023"). */
    private static final DateTimeFormatter FORMAT_DATE_COMPLET = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    /**
     * Constructeur de la vue Archives.
     * S'abonne au modèle pour les mises à jour et initialise la structure graphique.
     *
     * @param modele Le modèle de données.
     */
    public VueArchives(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    /**
     * Met en place la structure statique de la page (En-tête, ScrollPane).
     * Utilise un style rougeâtre pour symboliser la zone "Corbeille/Danger".
     */
    private void initialiserInterface() {
        // --- Création de l'entête ---
        HBox entete = new HBox(10);
        entete.setPadding(new Insets(10));
        entete.setAlignment(Pos.CENTER_LEFT);
        // Style CSS inline : fond rouge clair pour l'ambiance "Archives/Corbeille"
        entete.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ffcdd2; -fx-border-width: 0 0 1 0;");

        // Titre de la vue
        Label titreVue = new Label("🗄 Corbeille / Archives");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #c62828;");

        entete.getChildren().add(titreVue);
        setTop(entete); // Placement en haut du BorderPane

        // --- Création du contenu central ---
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));

        // Ajout d'un ScrollPane pour faire défiler la liste si elle est longue
        ScrollPane scroll = new ScrollPane(conteneurPrincipal);
        scroll.setFitToWidth(true); // Force le contenu à prendre toute la largeur
        scroll.setStyle("-fx-background-color: transparent;");
        setCenter(scroll); // Placement au centre
    }

    /**
     * Méthode appelée lors de la notification par le Modèle.
     * Récupère uniquement les tâches archivées pour l'affichage.
     *
     * @param s Le sujet observé (Modèle).
     */
    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            // IMPORTANT : On filtre ici pour ne récupérer que les archives
            rafraichirDonnees(m.getTachesArchives());
        }
    }

    /**
     * Reconstruit l'affichage de la liste des tâches archivées.
     * Trie et regroupe les tâches par date.
     *
     * @param lesTaches La liste des tâches archivées.
     */
    private void rafraichirDonnees(List<Tache> lesTaches) {
        // 1. Nettoyage de l'interface
        conteneurPrincipal.getChildren().clear();

        // 2. Gestion du cas vide
        if (lesTaches.isEmpty()) {
            Label lblVide = new Label("La corbeille est vide.");
            lblVide.setFont(Font.font("System", FontPosture.ITALIC, 14));
            lblVide.setTextFill(Color.GRAY);
            conteneurPrincipal.getChildren().add(lblVide);
            return;
        }

        // 3. Algorithme de tri par dates (similaire à VueListe)
        // On collecte toutes les dates uniques présentes
        Set<LocalDate> datesUtilisees = new HashSet<>();
        for (Tache t : lesTaches) {
            if (t.getDateDebut() != null) datesUtilisees.add(t.getDateDebut());
        }

        if (datesUtilisees.isEmpty()) return;

        // On détermine la plage temporelle (Min -> Max)
        LocalDate minDate = Collections.min(datesUtilisees);
        LocalDate maxDate = Collections.max(datesUtilisees);

        // On boucle jour par jour pour créer les sections
        LocalDate currentDate = minDate;
        while (!currentDate.isAfter(maxDate)) {
            final LocalDate jourCourant = currentDate;

            // Filtre : quelles tâches correspondent à ce jour 'currentDate' ?
            List<Tache> tachesDuJour = lesTaches.stream()
                    .filter(t -> t.getDateDebut() != null && t.getDateDebut().isEqual(jourCourant))
                    .collect(Collectors.toList());

            // Si on a des tâches ce jour-là, on construit le bloc visuel
            if (!tachesDuJour.isEmpty()) {
                construireSectionJour(currentDate, tachesDuJour);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Construit le bloc visuel pour une journée donnée (Date + Liste de tâches).
     *
     * @param dateDuJour La date de la section.
     * @param taches     La liste des tâches pour ce jour.
     */
    private void construireSectionJour(LocalDate dateDuJour, List<Tache> taches) {
        // Conteneur visuel pour le jour (carte blanche avec bordure)
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #FFF; -fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #EEE; -fx-border-radius: 5;");

        // Formatage du titre de la date (ex: "Lundi...")
        String titreFormatte = dateDuJour.format(FORMAT_DATE_COMPLET);
        titreFormatte = titreFormatte.substring(0, 1).toUpperCase() + titreFormatte.substring(1);

        Label lblJour = new Label(titreFormatte);
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblJour.setTextFill(Color.GRAY); // Gris pour montrer que c'est du passé/archive

        section.getChildren().add(lblJour);

        // Utilisation d'un GridPane pour aligner proprement les colonnes
        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(8);
        grille.setPadding(new Insets(0, 0, 0, 10));

        // Définition des largeurs de colonnes
        ColumnConstraints colLibelle = new ColumnConstraints(280);
        ColumnConstraints colEtat = new ColumnConstraints(100);
        ColumnConstraints colColonne = new ColumnConstraints(100);
        ColumnConstraints colDuree = new ColumnConstraints(80);
        ColumnConstraints colComm = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        colComm.setHgrow(Priority.ALWAYS); // Le commentaire prend l'espace restant
        ColumnConstraints colActions = new ColumnConstraints(160); // Espace pour 2 boutons

        grille.getColumnConstraints().addAll(colLibelle, colEtat, colColonne, colDuree, colComm, colActions);

        // Remplissage des lignes
        int row = 0;
        for (Tache t : taches) {
            row = ajouterLigneTache(grille, t, row);
        }
        section.getChildren().add(grille);
        conteneurPrincipal.getChildren().add(section);
    }

    /**
     * Ajoute une ligne représentant une tâche dans le GridPane.
     *
     * @param grille Le GridPane parent.
     * @param t      La tâche à afficher.
     * @param row    L'index de la ligne actuelle.
     * @return L'index de la ligne suivante (row + 1).
     */
    private int ajouterLigneTache(GridPane grille, Tache t, int row) {
        // 1. Titre de la tâche
        HBox boxTitre = new HBox(5);
        boxTitre.setAlignment(Pos.CENTER_LEFT);

        Label lLibelle = new Label(t.getLibelle());
        lLibelle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        lLibelle.setTextFill(Color.DARKGRAY); // Texte grisé car archivé
        // Permet d'ouvrir l'éditeur en lecture seule pour voir les détails
        lLibelle.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        lLibelle.setCursor(javafx.scene.Cursor.HAND);

        boxTitre.getChildren().add(lLibelle);

        // 2. État (Label fixe "Archivé")
        Label lEtat = new Label("Archivé");
        lEtat.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11px;");

        // 3. Ancienne colonne
        Label lColonne = new Label(t.getColonne());
        lColonne.setTextFill(Color.GRAY);

        // 4. Durée
        Label lDuree = new Label(t.getDureeEstimee() + "j");
        lDuree.setTextFill(Color.GRAY);

        // 5. Commentaire
        Label lComm = new Label(t.getCommentaire());
        lComm.setTextFill(Color.GRAY);
        lComm.setFont(Font.font("System", FontPosture.ITALIC, 12));

        // --- 6. ZONE ACTIONS (Restaurer + Supprimer) ---
        HBox boxActions = new HBox(10);
        boxActions.setAlignment(Pos.CENTER_RIGHT);

        // A. Bouton Restaurer (Icône flèche ⟲)
        // Action : Remet la tâche dans le Kanban actif
        Button btnRestaurer = new Button("⟲");
        btnRestaurer.setStyle("-fx-background-color: white; -fx-text-fill: #2E7D32; -fx-border-color: #2E7D32; -fx-border-radius: 3; -fx-cursor: hand; -fx-font-weight: bold;");
        btnRestaurer.setTooltip(new Tooltip("Restaurer la tâche (Désarchiver)"));
        btnRestaurer.setOnAction(new ControleurDesarchiverTache(modele, t));

        // B. Bouton Supprimer Définitivement (Icône poubelle 🗑)
        // Action : Supprime totalement la tâche de la mémoire
        Button btnSupprimer = new Button("🗑");
        btnSupprimer.setStyle("-fx-background-color: white; -fx-text-fill: #c0392b; -fx-border-color: #c0392b; -fx-border-radius: 3; -fx-cursor: hand;");
        btnSupprimer.setTooltip(new Tooltip("Supprimer définitivement (Irréversible)"));
        btnSupprimer.setOnAction(new ControleurSupprimerTache(modele, t));

        boxActions.getChildren().addAll(btnRestaurer, btnSupprimer);

        // Ajout des éléments à la grille aux coordonnées (colonne, ligne)
        grille.add(boxTitre, 0, row);
        grille.add(lEtat, 1, row);
        grille.add(lColonne, 2, row);
        grille.add(lDuree, 3, row);
        grille.add(lComm, 4, row);
        grille.add(boxActions, 5, row);

        return row + 1;
    }
}