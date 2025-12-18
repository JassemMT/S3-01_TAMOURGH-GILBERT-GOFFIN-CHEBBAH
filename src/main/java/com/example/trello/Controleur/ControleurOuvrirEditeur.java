package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Vue.VueEditeurTache;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class ControleurOuvrirEditeur implements EventHandler<MouseEvent> {
    private Tache tache;
    private Modele modele; // Nécessaire pour rafraichir après modif

    // Ajoute 'Modele modele' dans l'appel depuis VueKanban
    public ControleurOuvrirEditeur(Tache tache, Modele modele) {
        this.tache = tache;
        this.modele = modele;
    }

    // Constructeur de compatibilité si tu ne veux pas modifier VueKanban tout de suite
    // (Mais la mise à jour visuelle ne se fera pas automatiquement après édition)
    public ControleurOuvrirEditeur(Tache tache) {
        this.tache = tache;
        this.modele = null;
    }

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