package pizzas;


import javafx.scene.control.Label;

/**
 * Vue indiquant le prix total de la commande
 * COMMENTAIRE A COMPLETER
 */
public class VuePrix extends Label implements Observateur {
	
	public VuePrix(){
		super("vue ");
	}

	@Override
	public void actualiser(Sujet s) {
		double prix= ((ModeleCommande)s).getPrixCommande(); 
		this.setText("Prix total de votre commande : "+String.format("%.2f", prix)+" euros\n");

	}

}
