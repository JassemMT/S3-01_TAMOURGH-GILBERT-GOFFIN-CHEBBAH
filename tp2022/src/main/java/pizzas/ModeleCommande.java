package pizzas;

import java.util.ArrayList;
import java.util.List;

/**
 * Modele du MVC de l'application de commandes de pizzas 
 * COMMENTAIRE A COMPLETER
 */
public class ModeleCommande implements Sujet {

	private int nbPizza,numPizzaCourante;
	private double prixCommande;
	
	/**
	 * Liste des observateurs 
	 */
	private ArrayList<Observateur> observateurs;

	
	public ModeleCommande(){
		super();

		this.observateurs = new ArrayList<Observateur>();
		
		this.nbPizza=0;
		this.prixCommande=0.0;
		this.numPizzaCourante=0;
	}
	
	
	/**
	 * Methode appelee apres un ajout de pizza 
	 */

	
	/**
	 * Calcul du prix de la commande de pizzas
	 */

	
	/**
	 * @return the nbPizza
	 */
	public int getNbPizza() {
		return nbPizza;
	}

	
	/**
	 * @return the prixPizza
	 */
	public double getPrixCommande() {
		return prixCommande;
	}

	
	/**
	 * @return the listePizza
	 */

	
	/**
	 * @return the numPizzaCourante
	 */
	public int getNumPizzaCourante() {
		return numPizzaCourante;
	}


	/**
	 * @param num de la PizzaCourante to set
	 */
	public void setNumPizzaCourante(int num) {
		this.numPizzaCourante = num;
		System.out.println("Pizza "+this.numPizzaCourante+" selectionnee");
		notifierObservateurs();
	}

	/*
	 * Modifie la fidelite du client 
	 */


	/**
	 * Ajoute un observateur a la liste 
	 */
	public void enregistrerObservateur(Observateur o) {
		this.observateurs.add(o);
	}
	
	
	/**
	 * Supprime un observateur a la liste
	 */
	public void supprimerObservateur(Observateur o) {
		int i = this.observateurs.indexOf(o);
		if (i >= 0) {
			this.observateurs.remove(i);
		}
	}
	
	
	/**
	 * Informe tous les observateurs de la liste des
	 * modifications en appelant leurs methodes actualiser
	 */
	public void notifierObservateurs() {
		for (int i = 0; i < this.observateurs.size(); i++) {
			Observateur observer = this.observateurs.get(i);
			observer.actualiser(this);
		}
	}

	
}
