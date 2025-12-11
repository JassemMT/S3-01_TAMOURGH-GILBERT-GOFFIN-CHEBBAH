package pizzas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Principale_IGTP7 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        ModeleCommande modele;  //le modele
        VueCommText vuecommtxt; //Vue du texte de la commande
        VuePrix		vueprix;	// vue du prix total de la commande
        VueCommIm	vuecommim;	//Vue des images des pizzas commandees


        //Instanciation du modele
        modele = new ModeleCommande();

        //Les controleurs
        ControlDebutCommande controleDeb = new ControlDebutCommande(modele);

        //Instanciation des vues
        vuecommtxt= new VueCommText();
        vueprix = new VuePrix();
        vuecommim = new VueCommIm();


        // Ajout des vues comme observateurs du modele
        modele.enregistrerObservateur((Observateur)vuecommtxt);
        modele.enregistrerObservateur((Observateur)vueprix);
        modele.enregistrerObservateur((Observateur)vuecommim);


        BorderPane bp= new BorderPane();
        bp.setPadding(new Insets(10));


        // Panneau situe au nord de l'IG contenant les 2 boutons
        // permettant le choix de la base des pizzas et le niveau de fidelite du client
        HBox pnord = new HBox(20);
        pnord.setPadding(new Insets(10));
        pnord.setAlignment(Pos.CENTER);


        Button  AddvueListe= new Button(" Vue Liste ");
        AddvueListe.setOnAction(controleDeb);
        Button AddvueKanban= new Button(" Vue Kanban ");
        Button  AddvueGantt= new Button(" Vue Gantt ");
        AddvueGantt.setOnAction(controleDeb);
        AddvueKanban.setOnAction(controleDeb);
        pnord.getChildren().addAll(AddvueKanban,AddvueListe, AddvueGantt);
        bp.setTop(pnord); //place pnord en haut de l'IG


        // Panneau au centre de l'IG contenant la vision du choix des pizzas
        // ainsi que les boutons pour ajouter des ingredients
        BorderPane pcentral= new BorderPane();
        pcentral.setMaxHeight(300);

        //--> La vue avec la vision des images des pizzas
        pcentral.setCenter(vuecommim);//association du controleur à la vue

        //--> Le panneau contenant les boutons des ingredrients
        GridPane pingr= new GridPane();
        pingr.setAlignment(Pos.CENTER);
        pingr.setHgap(10);


        pcentral.setBottom(pingr);
        bp.setCenter(pcentral); //place pcentral au centre de l'IG

        // Panneau au sud de l'IG dans lequel se trouve l'affichage
        // du contenu de la commande et son prix
        BorderPane psud= new BorderPane();
        psud.setMinHeight(300);


        vuecommtxt.setFont(new Font("Times", 12));
        psud.setCenter(vuecommtxt); //Ajout de la vue de la commande

        VBox vb= new VBox();
        vb.setAlignment(Pos.CENTER);

        vueprix.setFont(new Font("Times", 14));
        vueprix.setTextAlignment(TextAlignment.CENTER);
        vb.getChildren().add(vueprix);
        psud.setBottom(vb);
        bp.setBottom(psud);


        Scene scene = new Scene(bp,935,670);
        stage.setTitle("SAE-Java");
        stage.setScene(scene);
        stage.show();
    }
}
