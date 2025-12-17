package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Contrôleur pour ouvrir l'éditeur d'une tâche
 */
public class ControleurOuvrirEditeur implements EventHandler<MouseEvent> {
    private Tache tache;

    public ControleurOuvrirEditeur(Tache tache) {
        this.tache = tache;
    }

    @Override
    public void handle(MouseEvent event) {
        // Double-clic pour ouvrir l'éditeur
        if (event.getClickCount() == 2) {
            VueEditeurTache editeur = new VueEditeurTache(tache);
            editeur.afficher();
        }
    }
}