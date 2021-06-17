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
	public void setUp() throws Exception { // on cr�e nos objets 

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		//test de la fonction nbsignalement 
		ArrayList<Pair<Integer, Region>> signalements;
		signalements = Json.nbSignalementsRegions("Delphinidae", 3);
		int res  = 8147; 
		assertEquals(signalements.get(0).getKey().intValue() ,res);
		
		//test de la fonction si on ajoute une date
		signalements = Json.nbSignalementsRegionsDate("Delphinidae",3,LocalDate.of(2016, 6, 15),LocalDate.of(2018,6,14));
		res = 3346;
		assertEquals(signalements.get(0).getKey().intValue() ,res);
		
		
	}

	

}