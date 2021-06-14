package json;

import java.io.BufferedReader;



import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.beans.introspect.PropertyInfo.Name;

import Donnee.Region;
import Donnee.Signalement;
import application.EarthController;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Json {
	
	private static String readAll(Reader rd) throws IOException{
		StringBuilder sb = new StringBuilder();
		int cp;
		
		while((cp=rd.read())!=-1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static JSONObject readJsonFromUrl(String url) {
		
		String json = "";
		
		HttpClient client = HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.followRedirects(Redirect.NORMAL)
				.connectTimeout(Duration.ofSeconds(20))
				.build();
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofMinutes(2))
				.header("Content-Type", "application/json")
				.GET()
				.build();
		
		try {
			json=client.sendAsync(request, BodyHandlers.ofString())
					.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new JSONObject(json);
	}
	
	public static ArrayList<Pair<Integer, Region>> nbSignalementsRegions(String nom, int precision){
		
		ArrayList<Pair<Integer, Region>> nbParRegions = new ArrayList<Pair<Integer, Region>>();
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		
		for(int i=0 ; i<20 ; i++) {
			
			int nb=json.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getInt("n");
			
			Point2D[] coords= {null,null,null,null};
			
			for(int j=0; j<4; j++) {
				
				Point2D geoCoord = new Point2D(json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry")
									           .getJSONArray("coordinates").getJSONArray(0).getJSONArray(j).getBigDecimal(0).floatValue(),
									           json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry")
									           .getJSONArray("coordinates").getJSONArray(0).getJSONArray(j).getBigDecimal(1).floatValue());
				coords[j]=geoCoord;
			}
			
			Region region = new Region(coords);
			
			Pair<Integer, Region> nbRegion = new Pair<Integer, Region>(nb,region);

			nbParRegions.add(nbRegion);

		}
		return nbParRegions;
	}
	
	public static ArrayList<Pair<Integer, Region>> nbSignalementsRegionsDate(String nom, int precision, LocalDate depart, LocalDate arrivee){
		
		ArrayList<Pair<Integer, Region>> nbParRegions = new ArrayList<Pair<Integer, Region>>();
	
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom +
				"&startdate=" + depart + "&enddate=" + arrivee);
		
		for(int i=0 ; i<20 ; i++) {
			
			int nb=json.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getInt("n");
			
			Point2D[] coords= {null,null,null,null};
			
			for(int j=0; j<4; j++) {
				
				Point2D geoCoord = new Point2D(json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry")
									           .getJSONArray("coordinates").getJSONArray(0).getJSONArray(j).getBigDecimal(0).floatValue(),
									           json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry")
									           .getJSONArray("coordinates").getJSONArray(0).getJSONArray(j).getBigDecimal(1).floatValue());
				coords[j]=geoCoord;
			}
			
			Region region = new Region(coords);
			
			Pair<Integer, Region> nbRegion = new Pair<Integer, Region>(nb,region);

			nbParRegions.add(nbRegion);

		}
		return nbParRegions;
	}
	
	
	public static ArrayList<Object[]> nbSignalementsIntervalles(String nom, int precision, LocalDate depart, int duree, int nbIntervalles) {
		
		ArrayList<Object[]>signalementsIntervalles= new ArrayList<Object[]>();
		
		for(int i=0; i<nbIntervalles; i++) {
			
			LocalDate arrivee= LocalDate.of(depart.getYear() + duree, depart.getMonthValue(), depart.getDayOfMonth());
			
			ArrayList<Pair<Integer, Region>> nbParRegions =nbSignalementsRegionsDate(nom, precision, depart, arrivee);
			
			Object[] signalementsIntervalle = {depart, arrivee, nbParRegions};
			
			signalementsIntervalles.add(signalementsIntervalle);
			
			depart=arrivee;
		}
		return signalementsIntervalles;
	}
	
	public static ArrayList<Signalement> rechercherSignalements(String nom, String geohash){
		
		ArrayList<Signalement> signalements = new ArrayList<Signalement>();
		
		
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence?scientificname=" + nom +
				"&geometry=" + geohash);
		
		for(int i=0; i<json.getJSONArray("results").length(); i++) {
			
			Signalement signalement = new Signalement(json.getJSONArray("results").getJSONObject(i).getJSONObject("scientificName").toString(),
										  json.getJSONArray("results").getJSONObject(i).getJSONObject("order").toString(),
										  json.getJSONArray("results").getJSONObject(i).getJSONObject("superClass").toString(),
										  json.getJSONArray("results").getJSONObject(i).getJSONObject("recordedBy").toString(),
										  json.getJSONArray("results").getJSONObject(i).getJSONObject("species").toString());
			signalements.add(signalement);
		}
		return signalements;
	}
	
	public static String[] completerNoms(String debut) {
		
		String[] premiersNoms= new String[20];
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/taxon/complete/verbose/" + debut);
		
		for(int i =0 ; i<20; i++) {
			
			premiersNoms[i] =json.getJSONArray("").getJSONObject(i).getJSONObject("scientificName").toString();
			
		}
		return premiersNoms;
	}
	
	public static void main(String[] args) {
		
		
		int precision = 3;
		String nom = "Delphinidae";
		//JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		//System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0));
		//System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
		
		ArrayList<Pair<Integer, Region>> resultat1 = nbSignalementsRegions("Delphinidae",3);
		System.out.println(resultat1 + "\n");
		
		ArrayList<Pair<Integer, Region>> resultat2 = nbSignalementsRegionsDate("Delphinidae",3,LocalDate.of(2018, 9, 23),LocalDate.of(2019,9,23));
		System.out.println(resultat2 + "\n");

		ArrayList<Object[]> resultat3 = nbSignalementsIntervalles("Delphinidae",3,LocalDate.of(2008, 9, 23),2,5);
		System.out.println(Arrays.toString(resultat3.get(0)));
		System.out.println(Arrays.toString(resultat3.get(1)));
		System.out.println(Arrays.toString(resultat3.get(2)));
		System.out.println(Arrays.toString(resultat3.get(3)));
		System.out.println(Arrays.toString(resultat3.get(4)) + "\n");
		
		System.out.println(resultat1.get(0));
		
		/*ArrayList<Signalement> resultat4 = rechercherSignalements("Manta birostris", "spd");
		System.out.println(resultat4 + "\n");*/
		
		/*String[] resultat5 = completerNoms("a");
		System.out.println(Arrays.toString(resultat5));*/
		
		
	}
	
}