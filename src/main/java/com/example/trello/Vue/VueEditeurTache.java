package com.example.trello.Vue;

import com.example.trello.Controleur.ControleurSauvegarderModif;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Fenêtre modale d'édition des détails d'une tâche.
 * <p>
 * Cette vue permet à l'utilisateur de modifier tous les attributs d'une tâche existante :
 * titre, colonne, état, dates, durée, couleur et commentaire.
 * Elle sert d'intermédiaire entre l'utilisateur et le {@link ControleurSauvegarderModif}.
 * </p>
 */
public class VueEditeurTache {

    private Tache tache;
    private Modele modele;
    private Stage stage;

    // --- Composants graphiques (Champs de saisie) ---
    private TextField champTitre;
    private TextArea champCommentaire;
    private ComboBox<String> comboEtat;
    private ComboBox<String> comboColonne;
    private DatePicker datePicker;
    private Spinner<Integer> spinnerDuree;
    private ColorPicker colorPicker;

    /**
     * Constructeur de la vue éditeur.
     * Prépare l'interface mais ne l'affiche pas encore (appeler {@link #afficher()}).
     *
     * @param tache  La tâche à modifier.
     * @param modele Le modèle de données (nécessaire pour connaître les colonnes disponibles).
     */
    public VueEditeurTache(Tache tache, Modele modele) {
        this.tache = tache;
        this.modele = modele;
        initialiserInterface();
    }

    /**
     * Construit l'interface graphique, initialise les composants et pré-remplit les champs
     * avec les valeurs actuelles de la tâche.
     */
    private void initialiserInterface() {
        // 1. Configuration de la fenêtre (Stage)
        stage = new Stage();
        // APPLICATION_MODAL bloque les interactions avec les autres fenêtres tant que celle-ci est ouverte
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Éditer : " + tache.getLibelle());

        // 2. Création de la grille de mise en page (GridPane)
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); // Marge extérieure
        grid.setHgap(10); // Espace horizontal entre les colonnes
        grid.setVgap(10); // Espace vertical entre les lignes

        // --- Ligne 0 : Titre ---
        grid.add(new Label("Titre:"), 0, 0);
        champTitre = new TextField(tache.getLibelle());
        grid.add(champTitre, 1, 0);

        // --- Ligne 1 : Colonne ---
        grid.add(new Label("Colonne:"), 0, 1);
        comboColonne = new ComboBox<>();
        // On récupère la liste dynamique des colonnes depuis le modèle
        comboColonne.getItems().addAll(modele.getColonnesDisponibles());
        comboColonne.setValue(tache.getColonne());
        grid.add(comboColonne, 1, 1);

        // --- Ligne 2 : État (Combobox statique) ---
        grid.add(new Label("État:"), 0, 2);
        comboEtat = new ComboBox<>();
        comboEtat.getItems().addAll("À faire", "En cours", "Terminé", "Archivé");
        // Conversion de l'entier stocké (ex: 0) en String lisible (ex: "À faire")
        comboEtat.setValue(getEtatString(tache.getEtat()));
        grid.add(comboEtat, 1, 2);

        // --- Ligne 3 : Date de début (DatePicker) ---
        grid.add(new Label("Date début:"), 0, 3);
        datePicker = new DatePicker();
        // Pré-remplissage avec la date actuelle de la tâche
        datePicker.setValue(tache.getDateDebut());
        grid.add(datePicker, 1, 3);

        // --- Ligne 4 : Durée (Spinner numérique) ---
        grid.add(new Label("Durée (jours):"), 0, 4);
        // Spinner configuré de 0 à 365 jours, valeur initiale = durée actuelle
        spinnerDuree = new Spinner<>(0, 365, tache.getDureeEstimee());
        spinnerDuree.setEditable(true); // Permet de taper le chiffre au clavier
        grid.add(spinnerDuree, 1, 4);

        // --- Ligne 5 : Couleur (ColorPicker) ---
        grid.add(new Label("Couleur:"), 0, 5);
        // Conversion du String Hex (Modele) vers un objet Color (JavaFX)
        String webColor = tache.getColor() != null ? tache.getColor() : "#FFFFFF";
        colorPicker = new ColorPicker(Color.web(webColor));
        grid.add(colorPicker, 1, 5);

        // --- Ligne 6 : Commentaire (TextArea) ---
        grid.add(new Label("Commentaire:"), 0, 6);
        champCommentaire = new TextArea(tache.getCommentaire());
        champCommentaire.setPrefRowCount(3); // Hauteur par défaut de 3 lignes
        grid.add(champCommentaire, 1, 6);

        // --- Ligne 7 : Boutons d'action ---
        Button btnSauvegarder = new Button("Sauvegarder");
        Button btnAnnuler = new Button("Annuler");

        // Liaison avec le Contrôleur dédié à la sauvegarde
        btnSauvegarder.setOnAction(new ControleurSauvegarderModif(modele, tache, this));

        // Fermeture simple pour l'annulation
        btnAnnuler.setOnAction(e -> stage.close());

        // Regroupement horizontal des boutons
        HBox boutons = new HBox(10, btnSauvegarder, btnAnnuler);
        grid.add(boutons, 1, 7);

        // 3. Finalisation de la scène
        Scene scene = new Scene(grid);
        stage.setScene(scene);
    }

    /**
     * Convertit le code état entier du modèle en chaîne de caractères lisible pour l'interface.
     *
     * @param etat L'entier représentant l'état (ex: Tache.ETAT_A_FAIRE).
     * @return Le libellé correspondant (ex: "À faire").
     */
    private String getEtatString(int etat) {
        switch (etat) {
            case Tache.ETAT_A_FAIRE: return "À faire";
            case Tache.ETAT_EN_COURS: return "En cours";
            case Tache.ETAT_TERMINE: return "Terminé";
            case Tache.ETAT_ARCHIVE: return "Archivé";
            default: return "À faire";
        }
    }

    /**
     * Affiche la fenêtre en mode bloquant (showAndWait).
     * L'exécution du code appelant s'arrête tant que la fenêtre n'est pas fermée.
     */
    public void afficher() { stage.showAndWait(); }

    /**
     * Ferme la fenêtre d'édition.
     */
    public void fermer() { stage.close(); }

    // --- GETTERS (Utilisés par le Contrôleur pour récupérer les saisies) ---

    /** @return Le contenu du champ titre. */
    public String getTitreSaisi() { return champTitre.getText(); }

    /** @return Le contenu du champ commentaire. */
    public String getCommentaireSaisi() { return champCommentaire.getText(); }

    /** @return La valeur sélectionnée dans la liste des états. */
    public String getEtatSelectionne() { return comboEtat.getValue(); }

    /** @return La valeur sélectionnée dans la liste des colonnes. */
    public String getColonneSelectionnee() { return comboColonne.getValue(); }

    /** @return La date sélectionnée dans le calendrier (LocalDate). */
    public LocalDate getDateSelectionnee() { return datePicker.getValue(); }

    /** @return La valeur numérique du spinner de durée. */
    public int getDureeSaisie() { return spinnerDuree.getValue(); }

    /**
     * Récupère la couleur choisie et la convertit en format Hexadécimal String.
     * <p>
     * Le modèle stocke la couleur sous forme "#RRGGBB" (String) pour la sérialisation,
     * alors que la vue utilise un objet {@link Color}. Cette méthode fait la conversion.
     * </p>
     * @return La chaîne hexadécimale de la couleur (ex: "#FF0000").
     */
    public String getCouleurChoisie() {
        Color c = colorPicker.getValue();
        // Formatage RGB en Hexadécimal : #RRGGBB
        return String.format("#%02X%02X%02X",
                (int)(c.getRed() * 255),
                (int)(c.getGreen() * 255),
                (int)(c.getBlue() * 255));
    }
}