package Donnee;

import java.util.ArrayList;

public class Espece {
	private String nomScientifique;
	private ArrayList<Signalement> listeSignalement;
	
	public Espece (String nom) {
		nomScientifique = nom;
		listeSignalement = new ArrayList<Signalement>();
	}
	
	public int NbrSign (String g) {
		int nbr = 0 ; 
		
		return nbr;
	}
}
