package Donnee;

import javafx.geometry.Point3D;
import sun.util.calendar.BaseCalendar.Date;

public class Signalement {
	
	private String scientificName;
	private String species;
	private String order;
	private String superclass;
	private String recordedBy;
	
	// Il manque la region, un signalement ce fais dans une region donnée

	public Signalement(String scientificName, String species, String order, String superclass, String recordedBy) {
		
		this.scientificName=scientificName;
		this.species=species;
		this.order=order;
		this.superclass= superclass;
		this.recordedBy= recordedBy;

	}
	
	public String getscientificName() {
		return this.scientificName;
	}
	
	@Override
	public String toString() {
		return "\n" 
				+ "scientificName : " + scientificName 
				+ "\n" + "species : " + species 
				+ "\n" + "order : " +  order 
				+ "\n" + "superclass : " +  superclass 
				+ "\n" + "recordedBy : " +  recordedBy 
				+ "\n" ;
	}
}
