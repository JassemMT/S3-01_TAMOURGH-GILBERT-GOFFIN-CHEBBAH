package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.time.LocalDate; // <--- Import indispensable

public class ControleurSauvegarderModif implements EventHandler<ActionEvent> {

    private Modele modele;
    private Tache tache;
    private VueEditeurTache vue;

    public ControleurSauvegarderModif(Modele modele, Tache tache, VueEditeurTache vue) {
        this.modele = modele;
        this.tache = tache;
        this.vue = vue;
    }

    // méthode permettant de sauvegarder les modifications apportées
    @Override
    public void handle(ActionEvent actionEvent) {
        // Récupération des nouvelles informations
        String nouveauTitre = vue.getTitreSaisi();
        String nouveauCom = vue.getCommentaireSaisi();
        String nouvelEtatStr = vue.getEtatSelectionne();
        String nouvelleColonne = vue.getColonneSelectionnee();
        int duree = vue.getDureeSaisie();
        String couleur = vue.getCouleurChoisie();

        LocalDate nouvelleDate = vue.getDateSelectionnee();

        // intégration des nouvelles données à la tache
        tache.setLibelle(nouveauTitre);
        tache.setCommentaire(nouveauCom);
        tache.setDureeEstimee(duree, modele.getParentDirect(tache));
        tache.setColor(couleur);

        // Mise à jour de la date (remplace setJour)
        if (nouvelleDate != null) {
            //tache.setDateDebut(nouvelleDate);
            modele.deplacerTacheDate(tache, nouvelleDate); // plus propre d'utiliser le modele pour modifier les données de l'app que la tâche directement
        }

        if (nouvelleColonne != null) {
            tache.setColonne(nouvelleColonne);
        }

        // Modification INDÉPENDANTE de l'état
        if ("À faire".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_A_FAIRE);
        else if ("En cours".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_EN_COURS);
        else if ("Terminé".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_TERMINE);
        else if ("Archivé".equals(nouvelEtatStr)) tache.setEtat(Tache.ETAT_ARCHIVE);

        // Actualisation du modèle ainsi que des vues
        modele.notifierObservateur();

        // Fermeture de la fenêtre
        vue.fermer();
    }
}