package com.example. trello.Vue;

import com.example.trello.Modele.Sujet;
import javafx.geometry.Insets;
import javafx.scene. layout.GridPane;
import com.example.trello.Modele.Modele;
import com.example.trello. Modele.Tache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VueKanban extends GridPane implements Observateur {

    private Modele modele;
    private List<VueKanbanColonne> vuesColonnes;

    public VueKanban(Modele modele) {
        super();
        this.modele = modele;
        this.vuesColonnes = new ArrayList<>();

        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #e0e0e0;");

        afficher();

        // S'enregistrer comme observateur du modèle
        modele.ajouterObservateur(this);
    }

    /**
     * Affiche le Kanban en lisant les colonnes depuis le modèle via getColonnes()
     */
    private void afficher() {
        // Vider l'affichage actuel
        this.getChildren().clear();
        vuesColonnes.clear();

        // Lire toutes les colonnes depuis le modèle
        Map<String, List<Tache>> colonnes = modele.getColonnes();

        // Créer une vue pour chaque colonne lue depuis le modèle
        int index = 0;
        for (String nomColonne : colonnes.keySet()) {

            // Créer la vue colonne
            VueKanbanColonne vueColonne = new VueKanbanColonne(nomColonne, modele);
            vuesColonnes.add(vueColonne);

            // Ajouter à la GridPane
            this.add(vueColonne, index++, 0);
        }
    }

    @Override
    public void actualiser(Sujet s) {
        if (s instanceof Modele) {
            // Relire toutes les colonnes depuis le modèle et rafraîchir l'affichage
            afficher();
        }
    }

    public List<VueKanbanColonne> getVuesColonnes() {
        return vuesColonnes;
    }
}