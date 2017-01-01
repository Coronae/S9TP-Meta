import java.util.ArrayList;

public class Objet {

	Boolean variable;//si objet prit ou non
	int valeur; //valeur de l'objet
	ArrayList<Integer> contrainteCapacite = new ArrayList<Integer>();//valeur des poids des contraintes de capacite pour cette objet i 
	ArrayList<Integer> contrainteDemande = new ArrayList<Integer>();//valeur des poids des contraintes de demande pour cette objet i 
	public Objet(int valeur) {
		super();
		variable = false;
		this.valeur = valeur;
	}
	public Boolean getVariable() {
		return variable;
	}
	public void setVariable(Boolean variable) {
		this.variable = variable;
	}
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}
	public ArrayList<Integer> getContrainteCapacite() {
		return contrainteCapacite;
	}
	public void setContrainteCapacite(ArrayList<Integer> contrainteCapacite) {
		this.contrainteCapacite = contrainteCapacite;
	}
	public ArrayList<Integer> getContrainteDemande() {
		return contrainteDemande;
	}
	public void setContrainteDemande(ArrayList<Integer> contrainteDemande) {
		this.contrainteDemande = contrainteDemande;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\n	" + "valeur : " + valeur + "\n	" + "variable (pris ou non ) : " + variable + " / contrainteCapacite : " + contrainteCapacite + " / contrainteDemande : " + contrainteDemande;
	}
}
