package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class ControleurOuvrirEditeur implements EventHandler<MouseEvent> {
    private Tache tache;
    private Modele modele;


    public ControleurOuvrirEditeur(Tache tache, Modele modele) {
        this.tache = tache;
        this.modele = modele;
    }

    public ControleurOuvrirEditeur(Tache tache) {
        this.tache = tache;
        this.modele = null;
    }

    // méthode permettant d'ouvrir la VueEditeurTache si un double click a été constaté
    @Override
    public void handle(MouseEvent event) {
        if (event.getClickCount() == 2) {
            // Si le modèle est null, l'éditeur ne pourra pas notifier les changements
            if(modele != null) {
                VueEditeurTache editeur = new VueEditeurTache(tache, modele);
                editeur.afficher();
            } else {
                System.out.println("Erreur: Le modèle n'a pas été passé au contrôleur.");
            }
        }
    }
}