/**
 * 
 */
package pizzas;


import javafx.scene.control.Label;

import static javafx.geometry.Pos.*;

/**
 * Vue donnant la description textuelle des pizzas de la commande
 * COMMENTAIRE A COMPLETER
 */
public class VueCommText extends Label implements Observateur {
	
	public VueCommText(){

		super("Vue Tache");
		this.setAlignment(TOP_LEFT);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void actualiser(Sujet su) {
		ModeleCommande m=(ModeleCommande)su;
		String s=" Vue Tache:\n";
		this.setText(s);
			
	}

}
