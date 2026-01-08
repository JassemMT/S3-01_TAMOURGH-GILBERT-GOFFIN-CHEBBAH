package com.example.trello.Controleur;
import com.example.trello.Modele.Modele;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class ControleurAjouterColonne implements EventHandler<ActionEvent> {
    private Modele modele;
    // constructeur du Controleur pour ajouter une colonne
    public ControleurAjouterColonne(Modele modele) { this.modele = modele; }

    // méthode handle permettant la création du nouvelle colonne
    @Override
    public void handle(ActionEvent actionEvent) {
        // Création de la fenêtre de dialogue avec les différentes informations qui vont devoir être renseigné
        TextInputDialog dialog = new TextInputDialog("Nouvelle Colonne");
        dialog.setTitle("Ajouter une colonne");
        dialog.setHeaderText("Nom de la colonne :");
        dialog.setContentText("Veuillez entrer le titre :");

        // Affichage de la fenêtre et attente de la réponse (Bloquant)
        // Le résultat est un Optional car l'utilisateur peut cliquer sur "Annuler"
        Optional<String> result = dialog.showAndWait();

        // Traitement du résultat
        // ifPresent n'exécute le code que si l'utilisateur a validé avec une valeur non nulle
        result.ifPresent(nom -> {
            // Appel au modèle pour modifier les données
            // Le modèle se chargera ensuite de notifier les vues (Pattern Observateur)
            modele.ajouterColonne(nom);
        });
    }
}
