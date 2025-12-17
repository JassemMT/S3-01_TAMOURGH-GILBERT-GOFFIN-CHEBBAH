package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.time.LocalDate;

public class ControleurCreerTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private String nomColonne;

    public ControleurCreerTache(Modele modele, String nomColonne) {
        this.modele = modele;
        this.nomColonne = nomColonne;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        TacheSimple nouvelleTache = new TacheSimple("Nouvelle Tâche", "Description...");
        nouvelleTache.setDates(LocalDate.now(), LocalDate.now().plusDays(1));
        nouvelleTache.setColonne(nomColonne);
        modele.ajouterTache(nouvelleTache);
    }
}