package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Vue.VueKanban;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

public class MainKanban extends Application {

    @Override
    public void start(Stage primaryStage) {
        Modele modele = new Modele();

        // Données de test
        TacheSimple t1 = new TacheSimple("Tache Test", "Description");
        t1.setDates(LocalDate.now(), LocalDate.now().plusDays(1));
        t1.setColonne("A faire");
        modele.ajouterTache(t1);

        // VueKanban est un Pane (conteneur), on l'utilise comme racine (root) de la Scene
        VueKanban root = new VueKanban(modele);

        // On crée la Scene avec ce root
        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setTitle("Kanban App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}