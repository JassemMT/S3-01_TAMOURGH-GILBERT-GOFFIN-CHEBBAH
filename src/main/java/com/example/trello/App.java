package com.example.trello;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;


public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // GridPane principale
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.setStyle("-fx-background-color: #e0e0e0;");

        // Créer les trois colonnes
        VBox colonneTodo = creerColonne("À faire", "#d32f2f");
        VBox colonneEnCours = creerColonne("En cours", "#ff9800");
        VBox colonneFait = creerColonne("Fait", "#00ff00");

        // Ajouter les colonnes à la GridPane
        gridPane.add(colonneTodo, 0, 0);
        gridPane.add(colonneEnCours, 1, 0);
        gridPane.add(colonneFait, 2, 0);

        // Bouton + flottant en bas à droite
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);

        Button btnAjout = creerBoutonPlus();
        StackPane.setAlignment(btnAjout, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(btnAjout, new Insets(20));
        root.getChildren().add(btnAjout);

        // Scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Task Board - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox creerColonne(String titre, String couleur) {
        VBox colonne = new VBox(10);
        colonne.setPadding(new Insets(10));
        colonne.setStyle("-fx-background-color: #c0c0c0; -fx-background-radius: 5;");
        colonne.setPrefWidth(200);

        // En-tête de la colonne
        Label header = new Label(titre);
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #d0d0d0; " +
                "-fx-padding: 10; -fx-background-radius: 5;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // Zone pour les tâches
        VBox listeTaches = new VBox(5);
        listeTaches.setPadding(new Insets(5));

        // Bouton + en bas de la colonne
        Button btnAjoutColonne = new Button("+");
        btnAjoutColonne.setStyle(
                "-fx-background-color:  #808080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size:  20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 25; " +
                        "-fx-min-width: 40px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-max-width: 40px; " +
                        "-fx-max-height: 40px;"
        );
        btnAjoutColonne.setOnAction(e -> {
            listeTaches.getChildren().add(creerTache("Nouvelle tâche", couleur));
        });

        HBox boutonContainer = new HBox(btnAjoutColonne);
        boutonContainer.setAlignment(Pos.CENTER);
        boutonContainer.setPadding(new Insets(10, 0, 0, 0));

        colonne.getChildren().addAll(header, listeTaches, boutonContainer);
        VBox.setVgrow(listeTaches, Priority.ALWAYS);

        return colonne;
    }

    private HBox creerTache(String texte, String couleur) {
        HBox tache = new HBox();
        tache.setStyle("-fx-background-color: " + couleur + "; " +
                "-fx-background-radius: 3; " +
                "-fx-padding: 8;");
        tache.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(texte);
        label.setStyle("-fx-text-fill: " + (couleur. equals("#00ff00") ? "black" : "white") + "; " +
                "-fx-font-size: 12px;");

        tache.getChildren().add(label);
        return tache;
    }

    private Button creerBoutonPlus() {
        Button btn = new Button("+");
        btn.setStyle(
                "-fx-background-color: #606060; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 35; " +
                        "-fx-min-width: 70px; " +
                        "-fx-min-height: 70px; " +
                        "-fx-max-width: 70px; " +
                        "-fx-max-height: 70px;"
        );
        btn.setOnAction(e -> {
            System.out.println("Ajouter une nouvelle tâche");
        });
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}