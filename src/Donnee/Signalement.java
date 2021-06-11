package Donnee;

import javafx.geometry.Point3D;
import sun.util.calendar.BaseCalendar.Date;

public class Signalement {
	
	private String scientificName;
	private String species;
	private String order;
	private String superClass;
	private String recordBy;
	
	// Il manque la region, un signalement ce fais dans une region donnée

	public Signalement(String scientificName, String species, String order, String superClass, String recordBy) {
		
		this.scientificName=scientificName;
		this.species=species;
		this.order=order;
		this.superClass= superClass;
		this.recordBy= recordBy;

	}
}
