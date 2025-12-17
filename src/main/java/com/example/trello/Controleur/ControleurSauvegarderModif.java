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
        // 1. Récupération des valeurs depuis la Vue
        String nouveauTitre = vue.getTitreSaisi();
        String nouveauCom = vue.getCommentaireSaisi();
        String nouvelEtat = vue.getEtatSelectionne();
        String nouveauJour = vue.getJourSelectionne(); // MODIFIÉ : String au lieu de LocalDate
        int duree = vue.getDureeSaisie();
        String couleur = vue.getCouleurChoisie();

        // 2. Modification des attributs de la Tâche
        tache.setLibelle(nouveauTitre);
        tache.setCommentaire(nouveauCom);
        tache.setJour(nouveauJour); // MODIFIÉ : Setter simple
        tache.setDureeEstimee(duree);
        tache.setColor(couleur);

        // 3. Mise à jour de la colonne en fonction de l'état
        if ("À faire".equals(nouvelEtat)) {
            tache.setEtat(Tache.ETAT_A_FAIRE);
            tache.setColonne("À faire");
        }
        else if ("En cours".equals(nouvelEtat)) {
            tache.setEtat(Tache.ETAT_EN_COURS);
            tache.setColonne("En cours");
        }
        else if ("Terminé".equals(nouvelEtat)) {
            tache.setEtat(Tache.ETAT_TERMINE);
            tache.setColonne("Terminé");
        }
        else if ("Archivé".equals(nouvelEtat)) {
            tache.setEtat(Tache.ETAT_ARCHIVE);
        }

        // 4. Notification et Fermeture
        modele.notifierObservateur();
        vue.fermer();
    }
}