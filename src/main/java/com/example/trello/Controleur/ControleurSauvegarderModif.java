package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Contrôleur pour sauvegarder les modifications d'une tâche
 */
public class ControleurSauvegarderModif implements EventHandler<ActionEvent> {
    private Modele modele;
    private Tache tache;
    private VueEditeurTache vueEditeur;

    public ControleurSauvegarderModif(Modele modele, Tache tache, VueEditeurTache vueEditeur) {
        this.modele = modele;
        this.tache = tache;
        this.vueEditeur = vueEditeur;
    }

    @Override
    public void handle(ActionEvent event) {
        // Récupérer les valeurs modifiées
        String nouveauLibelle = vueEditeur.getChampTitre();
        String nouveauCommentaire = vueEditeur.getChampCommentaire();

        // Mettre à jour la tâche
        if (nouveauLibelle != null && !nouveauLibelle.trim().isEmpty()) {
            tache.setLibelle(nouveauLibelle);
        }

        if (nouveauCommentaire != null) {
            tache.setCommentaire(nouveauCommentaire);
        }

        // Notifier le modèle
        modele.notifierObservateur();

        // Fermer la fenêtre
        vueEditeur.fermer();
    }
}