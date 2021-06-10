package json;

import java.io.BufferedReader;



import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import Donnee.Region;
import application.EarthController;
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
	
	public static ArrayList<JSONArray> nbSignalementsRegions(String nom, int precision){
		
		ArrayList<JSONArray> nbParRegions = new ArrayList<JSONArray>();
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		
		for(int i=0 ; i<200 ; i++) {
			
			int nb=json.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getInt("n");
			//System.out.println(nb);
			JSONArray coords=json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			coords.put(0, nb);

			nbParRegions.add(coords);

		}
		return nbParRegions;
	}
	
	public static ArrayList<JSONArray> nbSignalementsRegionsDate(String nom, int precision, LocalDate depart, LocalDate arrivee){
		
		ArrayList<JSONArray> nbParRegions = new ArrayList<JSONArray>();
		
		int moisDepart= depart.getMonthValue();
		int moisArrivee= arrivee.getMonthValue();
		int jourDepart= depart.getDayOfMonth();
		int jourArrivee= depart.getMonthValue();
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom +
				"&startdate=" + depart.getYear() + "-" + depart.getMonth() + "-" + depart.getDayOfMonth() + 
				"&enddate=" + arrivee.getYear() + "-" + arrivee.getMonth() + "-" + arrivee.getDayOfMonth());
		
		for(int i=0 ; i<200 ; i++) {
			
			int nb=json.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getInt("n");
			//System.out.println(nb);
			JSONArray coords=json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			coords.put(0, nb);

			nbParRegions.add(coords);

		}
		return nbParRegions;
	}
	
	public static void main(String[] args) {
		
		/*try(Reader reader = new FileReader("data.json")) {
			
			BufferedReader rd = new BufferedReader(reader);
			String jsonText = readAll(rd);
			JSONObject jsonRoot = new JSONObject(jsonText);
			
			JSONArray resultatRecherche = jsonRoot.getJSONObject("query").getJSONArray("search");
			JSONObject article = resultatRecherche.getJSONObject(0);
			System.out.println(article.getString("title"));
			System.out.println(article.getString("snippet"));
			System.out.println(article.getInt("wordcount"));
			
			JSONObject article2 = resultatRecherche.getJSONObject(1);
			System.out.println(article2.getString("title"));
			
			
			
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}*/
		int precision = 3;
		String nom = "Delphinidae";
		//JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		//System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0));
		//System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
		//ArrayList<JSONArray> resultat1 = nbSignalementsRegions("Delphinidae",3);
		//System.out.println(resultat1);
		ArrayList<JSONArray> resultat2 = nbSignalementsRegionsDate("Delphinidae",3,LocalDate.of(2018, 11, 23),LocalDate.of(2019,11,23));
		System.out.println(resultat2);
		
	}
	
}