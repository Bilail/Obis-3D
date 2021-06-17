package tests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Donnee.Region;
import javafx.util.Pair;

import javafx.util.Pair;
import json.Json;


public class MainTest {

	@Before
	public void setUp() throws Exception { // on crée nos objets 
	ArrayList<Pair<Integer, Region>> signalements;
	int res;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNbSignalement() {
		ArrayList<Pair<Integer, Region>> signalements;
		//test de la fonction nbsignalement 
		signalements = Json.nbSignalementsRegions("Delphinidae", 3);
		int res  = 8147; 
		assertEquals(signalements.get(0).getKey().intValue() ,res);
	
	}
	
	@Test
	public void testNbSignalementavecDate() {
		ArrayList<Pair<Integer, Region>> signalements;
		int res;
		//test de la fonction si on ajoute une date
		signalements = Json.nbSignalementsRegionsDate("Delphinidae",3,LocalDate.of(2016, 6, 15),LocalDate.of(2018,6,14));
		res = 3346;
		assertEquals(signalements.get(0).getKey().intValue() ,res);
				
	}
	
	@Test
	public void test() {
		
	}
		
	

}
