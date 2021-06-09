package json;

import java.io.BufferedReader;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import Donnee.Region;
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
	
	/*public ArrayList<Pair<Region, Integer>> nbSignalementsRegions(String nom, int precision){
		
		ArrayList<Pair<Region, Integer>> nbParRegions = new ArrayList<Pair<Region, Integer>>();
		
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		
		for(int i=0 ; i<json.getJSONArray("features").length() ; i++) {
			
			int nb=json.getJSONArray("features").getJSONObject(i).getJSONObject("properties").getInt("n");
			
			JSONArray coords=json.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			
			BigDecimal[] p1= { coords.getJSONArray(1).get(0), coords.getJSONArray(1).get(1)};
			BigDecimal[] p2= { coords.getJSONArray(2).get(0), coords.getJSONArray(2).get(1)};
			BigDecimal[] p3= { coords.getJSONArray(3).get(0), coords.getJSONArray(3).get(1)};
			BigDecimal[] p4= { coords.getJSONArray(4).get(0), coords.getJSONArray(4).get(1)};
			
			Region region = new Region(p1, p2, p3, p4);
			
			Pair<Region, Integer> pair= new Pair<Region, Integer>(region, nb);
			
			nbParRegions.add(pair);
		}
		return nbParRegions;
	}*/
	
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
		JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/" + precision + "?scientificname=" + nom);
		//JSONObject json=readJsonFromUrl("https://api.obis.org/v3/occurrence/grid/3?scientificname=Delphinidae");
		System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0).getJSONArray(1).get(0).getClass());
		System.out.println(json.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getInt("n"));
		//System.out.println(json);
		//JSONObject json2=readJsonFromUrl("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Whale&format=json");
		//System.out.println(json2);
		Json jSon = new Json();
		//System.out.println(jSon.nbSignalementsRegions("Delphinidae",3));
	}
	
}