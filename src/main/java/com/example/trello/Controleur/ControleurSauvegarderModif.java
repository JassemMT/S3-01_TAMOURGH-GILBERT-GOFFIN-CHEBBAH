package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ControleurSauvegarderModif implements EventHandler<ActionEvent> {

    private Modele modele;
    private Tache tache;
    private VueEditeurTache vue;

    public ControleurSauvegarderModif(Modele modele, Tache tache, VueEditeurTache vue) {
        this.modele = modele;
        this.tache = tache;
        this.vue = vue;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        // 1. Récupération
        String nouveauTitre = vue.getTitreSaisi();
        String nouveauCom = vue.getCommentaireSaisi();
        String nouvelEtatStr = vue.getEtatSelectionne();
        String nouvelleColonne = vue.getColonneSelectionnee(); // NOUVEAU
        String nouveauJour = vue.getJourSelectionne();
        int duree = vue.getDureeSaisie();
        String couleur = vue.getCouleurChoisie();

        // 2. Modification des attributs simples
        tache.setLibelle(nouveauTitre);
        tache.setCommentaire(nouveauCom);
        tache.setJour(nouveauJour);
        tache.setDureeEstimee(duree);
        tache.setColor(couleur);

        // 3. Modification INDÉPENDANTE de la colonne
        if (nouvelleColonne != null) {
            tache.setColonne(nouvelleColonne);
        }

        // 4. Modification INDÉPENDANTE de l'état
        if ("À faire".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_A_FAIRE);
        else if ("En cours".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_EN_COURS);
        else if ("Terminé".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_TERMINE);
        else if ("Archivé".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_ARCHIVE);

        // 5. Notification
        modele.notifierObservateur();
        vue.fermer();
    }
}