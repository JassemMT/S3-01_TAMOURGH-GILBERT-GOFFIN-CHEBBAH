/**
 * 
 */
package pizzas;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * un controleur --> gestion des boutons du JPanel nord de l'IG
 *
 */
public class ControlDebutCommande implements EventHandler<ActionEvent> {
	
	private ModeleCommande model;
		
	/**
	 * Construction du panel des boutons pour ajouter des pizzas a la commande
	 * COMMENTAIRE A COMPLETER
	 */
	public ControlDebutCommande(ModeleCommande mo) {
		this.model=mo;	
	}

	
	/* (non-Javadoc)
	 *
	 */
	@Override
	public void handle(ActionEvent e) {
		String s= (String) e.toString();   /// Attention grande chaine de caractère
		System.out.println(s);
		


	}

}
