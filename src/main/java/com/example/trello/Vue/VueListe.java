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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VueListe extends BorderPane implements Observateur {

    private Modele modele;
    private VBox conteneurPrincipal;

    // Formatter pour afficher "Lundi 12 Octobre 2026"
    private static final DateTimeFormatter FORMAT_DATE_COMPLET = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    public VueListe(Modele modele) {
        this.modele = modele;
        this.modele.ajouterObservateur(this);
        initialiserInterface();
        actualiser(modele);
    }

    // initialisation de l'intergace graphique
    private void initialiserInterface() {
        // hbox gérant l'entête
        HBox entete = new HBox(10);
        entete.setPadding(new Insets(10));
        entete.setAlignment(Pos.CENTER_LEFT);

        // titre de la vue + style
        Label titreVue = new Label("Vue Liste (Chronologique)");
        titreVue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // bouton ajouter + style + action
        Button btnAjouter = new Button("+ Nouvelle Tâche");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(new ControleurCreerTache(modele, "Principal"));

        // ajout des élements dans la hbox entete
        entete.getChildren().addAll(titreVue, btnAjouter);
        setTop(entete);

        // vbox pour le conteneur principal
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));

        // scrollPane contenant les éléments graphiques
        ScrollPane scroll = new ScrollPane(conteneurPrincipal);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        setCenter(scroll);
    }

    // permet de rafraichir mes donnees du modele
    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            Modele m = (Modele) s;
            rafraichirDonnees(m.getTaches());
        }
    }

    // met a jour les donnees du modele en nettoyant celui-ci
    private void rafraichirDonnees(List<Tache> lesTaches) {
        conteneurPrincipal.getChildren().clear();

        if (lesTaches.isEmpty()) {
            conteneurPrincipal.getChildren().add(new Label("Aucune tâche dans le projet."));
            return;
        }

        // Calculer la plage de dates (Min -> Max)
        Set<LocalDate> datesUtilisees = new HashSet<>();
        for (Tache t : lesTaches) {
            // On ignore les tâches qui auraient une date null (bug de migration)
            if (t.getDateDebut() != null) {
                datesUtilisees.add(t.getDateDebut());
            }
        }

        // Si après filtrage, aucune date valide n'est trouvée, on arrête
        if (datesUtilisees.isEmpty()) {
            conteneurPrincipal.getChildren().add(new Label("Aucune date valide trouvée pour les tâches."));
            return;
        }

        LocalDate minDate = Collections.min(datesUtilisees);
        LocalDate maxDate = Collections.max(datesUtilisees);

        // Map pour retrouver rapidement le parent d'une tâche
        Map<Tache, Tache> parentMap = new HashMap<>();
        for (Tache t : lesTaches) {
            if (t.aDesEnfants()) {
                for (Tache enfant : t.getEnfants()) {
                    parentMap.put(enfant, t);
                }
            }
        }

        // Boucle temporelle du premier jour au dernier jour
        LocalDate currentDate = minDate;

        // On boucle jusqu'à maxDate inclus
        while (!currentDate.isAfter(maxDate)) {
            final LocalDate jourCourant = currentDate; // pour la lambda

            // Récupérer les tâches de ce jour précis
            List<Tache> toutesTachesDuJour = lesTaches.stream()
                    .filter(t -> t.getDateDebut() != null && t.getDateDebut().isEqual(jourCourant))
                    .collect(Collectors.toList());

            // Filtrage visuel : On n'affiche que les "racines visuelles" pour ce jour
            List<Tache> racinesVisuellesDuJour = new ArrayList<>();
            for (Tache t : toutesTachesDuJour) {
                Tache parent = parentMap.get(t);
                boolean estRacineAbsolue = (parent == null);

                if (!estRacineAbsolue) {
                    // Si j'ai un parent, je ne m'affiche que si mon parent n'est PAS aujourd'hui
                    // (S'il est aujourd'hui, je serai affiché en tant que sous-tâche de lui)
                    boolean parentEstAujourdhui = parent.getDateDebut() != null && parent.getDateDebut().isEqual(jourCourant);
                    if (!parentEstAujourdhui) {
                        racinesVisuellesDuJour.add(t);
                    }
                } else {
                    racinesVisuellesDuJour.add(t);
                }
            }

            construireSectionJour(currentDate, racinesVisuellesDuJour);

            // Jour suivant
            currentDate = currentDate.plusDays(1);
        }
    }

    // permet de construire l'entete des taches
    private void construireSectionJour(LocalDate dateDuJour, List<Tache> tachesRacines) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #FAFAFA; -fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #EEE; -fx-border-radius: 5;");

        // Titre : "Lundi 12 Octobre 2024"
        String titreFormatte = dateDuJour.format(FORMAT_DATE_COMPLET);
        // Mettre 1ere lettre majuscule
        titreFormatte = titreFormatte.substring(0, 1).toUpperCase() + titreFormatte.substring(1);

        Label lblJour = new Label(titreFormatte);
        lblJour.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblJour.setTextFill(Color.DARKSLATEBLUE);

        // Indication visuelle si c'est aujourd'hui
        if (dateDuJour.isEqual(LocalDate.now())) {
            lblJour.setText(titreFormatte + " (Aujourd'hui)");
            lblJour.setTextFill(Color.DARKGREEN);
        }

        section.getChildren().add(lblJour);

        if (tachesRacines.isEmpty()) {
            Label lblVide = new Label("Aucune tâche"); // Texte discret
            lblVide.setTextFill(Color.LIGHTGRAY);
            lblVide.setFont(Font.font("System", FontPosture.ITALIC, 11));
            section.getChildren().add(lblVide);
        } else {
            // création de la grille
            GridPane grille = new GridPane();
            grille.setHgap(10);
            grille.setVgap(8);
            grille.setPadding(new Insets(0, 0, 0, 10));

            // Définition des colonnes
            ColumnConstraints colLibelle = new ColumnConstraints(280);
            ColumnConstraints colEtat = new ColumnConstraints(100);
            ColumnConstraints colColonne = new ColumnConstraints(100);
            ColumnConstraints colDuree = new ColumnConstraints(80); // un peu plus large pour "jours"
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

    // permet d'ajouter une tache dans une grille, sur une ligne donnée avec un niveau d'indentation spécifique
    private int ajouterLigneTache(GridPane grille, Tache t, int row, int niveauIndent) {
        // titre + style + placement
        HBox boxTitre = new HBox(5);
        boxTitre.setAlignment(Pos.CENTER_LEFT);
        boxTitre.setPadding(new Insets(0, 0, 0, niveauIndent * 20));

        if (niveauIndent > 0) {
            Label indicateur = new Label("↳");
            indicateur.setTextFill(Color.GRAY);
            boxTitre.getChildren().add(indicateur);
        }

        // libelle
        Label lLibelle = new Label(t.getLibelle());
        lLibelle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        lLibelle.setCursor(javafx.scene.Cursor.HAND);
        lLibelle.setTooltip(new Tooltip("Double-cliquez pour éditer"));
        lLibelle.setOnMouseClicked(new ControleurOuvrirEditeur(t, modele));
        boxTitre.getChildren().add(lLibelle);

        // etat
        Label lEtat = creerBadgeEtat(t.getEtat());
        Label lColonne = new Label(t.getColonne());
        Label lDuree = new Label(t.getDureeEstimee() + "j"); // Affichage en Jours

        // commentaire
        String commentaireText = t.getCommentaire() != null ? t.getCommentaire() : "";
        Label lComm = new Label(commentaireText);
        lComm.setTextFill(Color.GRAY);
        lComm.setFont(Font.font("System", FontPosture.ITALIC, 12));
        lComm.setWrapText(false); // On évite que ça prenne trop de place verticalement

        // bouton archiver + style + en tant que popup + action
        Button btnArchiver = new Button("ARCHIVER");
        btnArchiver.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-cursor: hand;");
        btnArchiver.setTooltip(new Tooltip("Archiver la tâche"));
        btnArchiver.setOnAction(new ControleurArchiverTache(modele, t));

        // placement des éléments graphiques dans la grille
        grille.add(boxTitre, 0, row);
        grille.add(lEtat, 1, row);
        grille.add(lColonne, 2, row);
        grille.add(lDuree, 3, row);
        grille.add(lComm, 4, row);
        grille.add(btnArchiver, 5, row);

        row++;

        // Gestion récursive des enfants
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                // Si l'enfant est le même jour que le parent, on l'affiche en dessous (indenté)
                if (enfant.getDateDebut().isEqual(t.getDateDebut())) {
                    row = ajouterLigneTache(grille, enfant, row, niveauIndent + 1);
                }
                else {
                    // Si l'enfant est un autre jour, on met juste un lien indicatif
                    HBox boxInfo = new HBox(5);
                    boxInfo.setAlignment(Pos.CENTER_LEFT);
                    boxInfo.setPadding(new Insets(0, 0, 0, (niveauIndent + 1) * 20));

                    Label lblFleche = new Label("↳");
                    lblFleche.setTextFill(Color.LIGHTGRAY);

                    // Format court pour l'info enfant décalé
                    String dateEnfantStr = enfant.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM"));
                    Label lblInfo = new Label("📅 (" + dateEnfantStr + ") : " + enfant.getLibelle());
                    lblInfo.setTextFill(Color.GRAY);
                    lblInfo.setFont(Font.font("System", FontPosture.ITALIC, 12));

                    boxInfo.getChildren().addAll(lblFleche, lblInfo);

                    grille.add(boxInfo, 0, row);
                    GridPane.setColumnSpan(boxInfo, 5); // Prend toute la largeur
                    row++;
                }
            }
        }
        return row;
    }

    // permet la création d'un certain style aux différents etat de tache
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