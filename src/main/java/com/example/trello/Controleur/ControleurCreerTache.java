package com.example.trello.Controleur;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Contrôleur pour créer une nouvelle tâche dans une colonne
 */
public class ControleurCreerTache implements EventHandler<ActionEvent> {
    private Modele modele;
    private String colonneCible;

    public ControleurCreerTache(Modele modele, String colonneCible) {
        this.modele = modele;
        this.colonneCible = colonneCible;
    }

    @Override
    public void handle(ActionEvent event) {
        // Dialogue pour saisir le libellé
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle tâche");
        dialog.setHeaderText("Créer une nouvelle tâche dans: " + colonneCible);
        dialog.setContentText("Libellé de la tâche:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(libelle -> {
            if (!libelle.trim().isEmpty()) {
                // Créer une tâche simple avec des valeurs par défaut
                TacheSimple nouvelleTache = new TacheSimple(
                        libelle.trim(),
                        "",
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        colonneCible,
                        1
                );

                modele.ajouterTache(nouvelleTache);
            }
        });
    }
}