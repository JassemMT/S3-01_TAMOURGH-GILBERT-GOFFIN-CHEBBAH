package pizzas;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;


/**
 * Vue visualisant les images des pizzas de la commande
 * COMMENTAIRE A COMPLETER
 */
public class VueCommIm extends GridPane implements Observateur {

	public VueCommIm(){
		this.setPadding(new Insets(15));
		this.setHgap(25);
	}
	
	@Override
	public void actualiser(Sujet s) {
		
		ModeleCommande model=(ModeleCommande)s;
		this.getChildren().clear();  // efface

		
	}
	


	
}
