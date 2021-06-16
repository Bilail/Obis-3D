package tests.application;

import static org.junit.Assert.*;

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
		
		
		
	}

	

}
