package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;






import java.io.Reader;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.TextFields;
import org.json.JSONObject;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import Donnee.Region;
import Donnee.Signalement;
import geohash.GeoHashHelper;
import geohash.Location;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import json.Json;
import Model.EarthModel;

public class EarthController {
	
	@FXML
	private Pane pane3D;
	
	@FXML
	private Button btnLecture;
	
	@FXML
	private Button btnPause;
	
	@FXML
	private Button btnStop;
	
	@FXML
	private Button btnValider;
	
	@FXML
	private DatePicker dateDebut;
	
	@FXML
	private DatePicker dateFin;
	
	@FXML
	private TextField champRecherche;
	
	@FXML
	private ListView<String> listeEspeces;
	
	@FXML
	private TextField nbrIntervalle;
	
	@FXML
	private TextField duree;
	
	@FXML
	private TextArea description;
	
	@FXML
	private TextField precision;
	
	@FXML
	private ComboBox<String> combo;
	
	@FXML
	private Label L1;
	
	@FXML
	private Label L2;
	
	@FXML
	private Label L3;
	
	@FXML
	private Label L4;
	
	@FXML
	private Label L5;
	
	@FXML
	private Label L6;
	
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    private static final float TEXTURE_OFFSET = 1.01f;
    
    public Group earth;
    

    public void initialize() throws FileNotFoundException, IOException {
    	
    	description.setEditable(false);
    	
    	//on initialise les donnée avec un fichier json local
    	try (Reader reader = new FileReader("Delphinidae.json")){
    		BufferedReader rd = new BufferedReader(reader);
    		String JsonText = Json.readAll(rd);
    		JSONObject json = new JSONObject(JsonText);
    		
    		//parcer le json pour afficher les données	
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	precision.setText("3");
    	
    	L1.setText("< 0");
    	L2.setText("< 0");
    	L3.setText("< 0");
    	L4.setText("< 0");
    	L5.setText("< 0");
    	L6.setText("< 0");

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        
        //Auto completion 
        champRecherche.setOnKeyReleased(new EventHandler<KeyEvent>() {
        	@Override
        	public void handle(KeyEvent event) {	
        		
                	//TextFields.bindAutoCompletion(champRecherche, Json.completerNoms(champRecherche.getText()));
        			ObservableList<String> items = FXCollections.observableArrayList(Json.completerNoms(champRecherche.getText()));
            		combo.setItems(items);
            		//TextFields.bindAutoCompletion(champRecherche, items);
            		
            		
            		if (items.size() == 0 && champRecherche.getLength() > 0 ) {
            			
            			Alert alert = new Alert(AlertType.WARNING);
            			alert.setTitle("Message d'alerte");
            			alert.setHeaderText(null);
            			alert.setContentText("Nom d'espèce non repertoriée");
            			//alert.initModality(Modality.NONE);
            			alert.initModality(Modality.APPLICATION_MODAL);

            			alert.showAndWait();	
        		}
        	}
        });
        
        combo.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {	
                	
        			champRecherche.setText(combo.getSelectionModel().getSelectedItem());
        	}
        });
        
        
   
        btnValider.setOnAction( new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		
        	if(precision.getText().matches("[2-4]")) {
        		
        		System.out.println(earth.getChildren());
        		earth.getChildren().subList(1, earth.getChildren().size()).clear();
        		System.out.println(earth.getChildren());
    		
        		ArrayList<Pair<Integer, Region>> signalements;
        		
        		if(nbrIntervalle.getText().isBlank() | duree.getText().isBlank()) {

	        		if(dateDebut.getValue()!=null) {
	        			System.out.println(dateDebut.getValue());
	        			
	        			if(dateFin.getValue() !=null) {
	        				System.out.println(dateFin.getValue());
	        				signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(), Integer.valueOf(precision.getText()), dateDebut.getValue(), dateFin.getValue());	
	        			}
	        			else { signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(), Integer.valueOf(precision.getText()), dateDebut.getValue(), LocalDate.now());
	        			}	
	        		}
	        		else { signalements = Json.nbSignalementsRegions(champRecherche.getText(), Integer.valueOf(precision.getText()));}
	        		
	        			L6.setText("< " + computeLegend(signalements)[0]);
	        			L5.setText("< " + computeLegend(signalements)[1]);
	        			L4.setText("< " + computeLegend(signalements)[2]);
	        			L3.setText("< " + computeLegend(signalements)[3]);
	        			L2.setText("< " + computeLegend(signalements)[4]);
	        			L1.setText("< " + computeLegend(signalements)[5]);
	        		
	        			for (Pair<Integer,Region> signalement : signalements) {
	        			
	        				final PhongMaterial material = new PhongMaterial();
	        			
	        				if(signalement.getKey() <= computeLegend(signalements)[0]) {material.setDiffuseColor(new Color(0.0, 0.0, 0.5, 0.3));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[1]) {material.setDiffuseColor(new Color(0.0, 1.0, 1.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[2]) {material.setDiffuseColor(new Color(0.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[3]) {material.setDiffuseColor(new Color(1.0, 1.0, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[4]) {material.setDiffuseColor(new Color(1.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[5]) {material.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));}
	        			
	        				Region region = signalement.getValue();
	        			
	        				AddQuadrilateral(earth, region.getPoints()[2], region.getPoints()[1], region.getPoints()[0], region.getPoints()[3], material);
	        				AddBarreHistogramme(earth,signalement,material);
	        			}
	        			StringBuilder donnee = new StringBuilder();
	        			donnee.append("nbr d'occurence | point 3D \n");
	        			for (Pair<Integer,Region> signalement : signalements) {
	        				donnee.append("\n" + signalement.getKey().toString() + "\n"
	        				+"\t" + signalement.getValue().getPoints()[0] +"\n\t" + signalement.getValue().getPoints()[1] + 
	        				"\n\t" + signalement.getValue().getPoints()[2]+ "\n\t" + signalement.getValue().getPoints()[3]);	
	        			}
	        			description.setText(donnee.toString());
        			}
        			else if (dateFin.getValue() == null && !nbrIntervalle.getText().isBlank() && !duree.getText().isBlank()){

    					ArrayList<Object[]> intervalles = Json.nbSignalementsIntervalles(champRecherche.getText(), Integer.valueOf(precision.getText()), 
   						 dateDebut.getValue(), Integer.valueOf(duree.getText()), Integer.valueOf(nbrIntervalle.getText()));
    				
    					StringBuilder donnee = new StringBuilder();
    					donnee.append("nbr d'occurence | point 3D \n");
        			
    					for (Object[] intervalle : intervalles) {
    						donnee.append("\n" + intervalle[0] + " / " + intervalle[1]);
    						signalements=(ArrayList<Pair<Integer, Region>>) intervalle[2];
        				
        					for (Pair<Integer,Region> signalement : signalements) {
        						donnee.append("\n" + signalement.getKey().toString() + "\n"
        								+"\t" + signalement.getValue().getPoints()[0] +"\n\t" + signalement.getValue().getPoints()[1] + 
        								"\n\t" + signalement.getValue().getPoints()[2]+ "\n\t" + signalement.getValue().getPoints()[3]);	
        					}	
    					}
        			description.setText(donnee.toString());
        			} 
        		}
        		else {
        			
            		Alert alert = new Alert(AlertType.WARNING);
            		alert.setTitle("Message d'alerte");
            		alert.setHeaderText("Erreur Precision GeoHash");
            		alert.initModality(Modality.APPLICATION_MODAL);
            		alert.showAndWait();	
            	}
        	}	
        });
        
        
        btnLecture.setOnAction( new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		
        		
        		
        		// Ici il faut faire en sorte que on ai date de début + n * duree/nbrIntervalle pour passer au donné nième
    			if (dateDebut.getValue() != null && dateFin.getValue()!=null){
    				
    				System.out.println(earth.getChildren());
            		earth.getChildren().subList(1, earth.getChildren().size()).clear();
            		System.out.println(earth.getChildren());
            		
            		boolean animationEnCours = false;

        			int anneeFin = dateFin.getValue().getYear() - (dateFin.getValue().getYear()-dateDebut.getValue().getYear())%5;
      
        			int annee1 = dateDebut.getValue().getYear();
        			int annee2 = dateDebut.getValue().getYear()+5;
        			
        			while(annee2 <= anneeFin & animationEnCours==false ){
        			
	        			
        				
        				ArrayList<Pair<Integer, Region>> signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(),
        						Integer.valueOf(precision.getText()),
        						LocalDate.of(annee1, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()),
        						LocalDate.of(annee2, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()));
        				System.out.println(LocalDate.of(annee1, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()));
        				System.out.println(LocalDate.of(annee2, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()));
        				
        				
        				for (Pair<Integer,Region> signalement : signalements) {
        					
        					L6.setText("< " + computeLegend(signalements)[0]);
                			L5.setText("< " + computeLegend(signalements)[1]);
                			L4.setText("< " + computeLegend(signalements)[2]);
                			L3.setText("< " + computeLegend(signalements)[3]);
                			L2.setText("< " + computeLegend(signalements)[4]);
                			L1.setText("< " + computeLegend(signalements)[5]);
                			
	        				final PhongMaterial material = new PhongMaterial();
	        			
	        				if(signalement.getKey() <= computeLegend(signalements)[0]) {material.setDiffuseColor(new Color(0.0, 0.0, 0.5, 0.3));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[1]) {material.setDiffuseColor(new Color(0.0, 1.0, 1.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[2]) {material.setDiffuseColor(new Color(0.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[3]) {material.setDiffuseColor(new Color(1.0, 1.0, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[4]) {material.setDiffuseColor(new Color(1.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(signalements)[5]) {material.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));}
	        			
	        				Region region = signalement.getValue();
	        			
	        				//AddQuadrilateral(earth, region.getPoints()[2], region.getPoints()[1], region.getPoints()[0], region.getPoints()[3], material);
	        				animationEnCours=AddBarreHistogrammeAnimation(earth,signalement,material);
	        				//wait();
	        				
		    				
        				}
        				annee1=annee2;
        				annee2=annee1 + 5;
        			}
    			}
    			else {
    				
    			}
        		
        	}
        });
        
        
        root3D.addEventHandler(MouseEvent.ANY, event ->{
      		if(event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isAltDown()) {
      			
      			PickResult pickResult = event.getPickResult();
      			Point3D spaceCoord = pickResult.getIntersectedPoint();
      			
      			displayPoint3D(spaceCoord,root3D);
      			System.out.println(champRecherche.getText());
      			Point2D geoCoord = SpaceCoordToGeoCoord(spaceCoord);
      			String geohash = GeoHashHelper.getGeohash(new Location("selectedGeoHash", geoCoord.getX(), geoCoord.getY()));
      			geohash = geohash.substring(0, 2);
      			System.out.println(geohash);
      			
      			ArrayList<Signalement> signalements = Json.rechercherSignalements(champRecherche.getText(), geohash);
      			System.out.println(signalements);
      			
      			StringBuilder sb = new StringBuilder();
      			ArrayList<String> noms = new ArrayList<String>();
      			
      			for(Signalement signalement : signalements) {
      				sb.append(signalement.toString());
      				noms.add(signalement.getscientificName());
      			}
      			
      			ObservableList<String> items = FXCollections.observableArrayList(noms);
      			listeEspeces.setItems(items);
      			description.setText(sb.toString());
      			
      			Alert alert = new Alert(AlertType.INFORMATION);
        		alert.setTitle("information sur la zone");
        		alert.setHeaderText("Espece présente dans la Zone");
        		alert.setResizable(true);
        		alert.setContentText(sb.toString());
        		alert.initModality(Modality.NONE);
        		alert.showAndWait();
      		}
      	});
        
        listeEspeces.setOnMouseClicked(new EventHandler<MouseEvent>(){
        	@Override
        	public void handle(MouseEvent event) {
        		champRecherche.setText(listeEspeces.getSelectionModel().getSelectedItem());
        		combo.getSelectionModel().select(null);
        	}
        });
        
        // Load geometry
      	ObjModelImporter objImporter = new ObjModelImporter();
      	
      	try {
      		URL modelUrl = this.getClass().getResource("Earth/earth.obj");
      		objImporter.read(modelUrl);
      	}
      	catch (ImportException e) {
      		System.out.println(e.getMessage());
      	}
      	
      	MeshView[] meshViews = objImporter.getImport();
      	earth = new Group(meshViews);
      	earth.setId("id");
      	root3D.getChildren().add(earth);
        root3D.setFocusTraversable(true);


        // Draw city on the earth
      	//displayPoint(root3D, "Brest",  -4.418539f,48.447911f);
      	displayPoint(earth, "Marseille", 43.435555f, 5.213611f);
      	/*displayPoint(root3D, "New York ", 40.639751f, -73.778925f);
      	displayPoint(root3D, "Cape Town", -33.964806f, 18.601667f);
      	displayPoint(root3D, "Istanbul", 40.976922f, 28.814606f);
      	displayPoint(root3D, "Reykjavik", 64.13f, -21.940556f);
      	displayPoint(root3D, "Singapore", 1.350189f, 103.994433f);
      	displayPoint(root3D, "Seoul", 37.469075f, 126.450517f);
      	
      	final PhongMaterial redMaterial = new PhongMaterial();
      	redMaterial.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));
      	
      	Point3D topRight = geoCoordTo3dCoord(48.447911f + 2,-4.418539f + 2);
      	Point3D topLeft = geoCoordTo3dCoord(48.447911f - 2,-4.418539f + 2);
      	Point3D bottomRight = geoCoordTo3dCoord(48.447911f + 2,-4.418539f - 2);
      	Point3D bottomLeft = geoCoordTo3dCoord(48.447911f - 2,-4.418539f - 2);
      	
      	displayPoint3D(topRight, root3D);
      	displayPoint3D(topLeft, root3D);
      	displayPoint3D(bottomRight, root3D);
      	displayPoint3D(bottomLeft, root3D);
      	

      	AddQuadrilateral(root3D,topLeft ,bottomLeft , bottomRight,topRight , redMaterial);*/
      	
      	Point3D from = geoCoordTo3dCoord(43.435555f, 5.213611f);
    	Point3D to = Point3D.ZERO;
    	Point3D yDir = new Point3D(0, 1, 0);
    	
        Box box = new Box(0.01f,0.01f,0.01f);
        //box.setDepth(2);
        
        final long startNanoTime = System.nanoTime();
        
        float n = 0.0002f*50000;
        
        new AnimationTimer() {
        	public void handle(long currentNanoTime) {
        		double t = (currentNanoTime - startNanoTime) / 1000000000.0;
        		
        		if(box.getDepth()<n) {
        			
        		box.setDepth(box.getDepth() + 0.0005);
        		box.setTranslateZ((-box.getDepth())/2);
        		}
        	}
        }.start();


        Affine affine = new Affine();
        affine.append(lookAt(from,to,yDir));
        
        Group group = new Group();
        group.getChildren().add(box);
        
        group.getTransforms().setAll(affine);
        earth.getChildren().addAll(group);
       
        System.out.println(earth.getChildren().contains(group));
		
        
      	
      	
        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane3D, root3D);

        // Add point light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-180);
        light.setTranslateY(-90);
        light.setTranslateZ(-120);
        light.getScope().addAll(root3D);
        root3D.getChildren().add(light);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        // Create scene
        SubScene subscene = new SubScene(root3D, 472, 556, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(camera);
        subscene.setFill(Color.GREY);
        pane3D.getChildren().addAll(subscene);
        
        
        

    }


    public Group displayPoint3D(Point3D p, Group parent) {
    	
    	Sphere sphere = new Sphere(0.005);
    	
    	sphere.setTranslateX(p.getX());
    	sphere.setTranslateY(p.getY());
    	sphere.setTranslateZ(p.getZ());
    	
      	parent.getChildren().add(sphere);
      	
      	return parent;
    }

    // From Rahel LÃ¼thy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }
    
    public static Affine lookAt(Point3D from, Point3D to, Point3D ydir) {
    	
        Point3D zVec = to.subtract(from).normalize();
        Point3D xVec = ydir.normalize().crossProduct(zVec).normalize();
        Point3D yVec = zVec.crossProduct(xVec).normalize();
        
        return new Affine(xVec.getX(), yVec.getX(), zVec.getX(), from.getX(),
                          xVec.getY(), yVec.getY(), zVec.getY(), from.getY(),
                          xVec.getZ(), yVec.getZ(), zVec.getZ(), from.getZ());
    }
    
    public static int[] computeLegend(ArrayList<Pair<Integer,Region>> signalements) {
    	
    	int max = signalements.get(0).getKey();

    	int pas = max/6;
    	
    	int[] valeursLegende = {pas,2*pas,3*pas,4*pas,5*pas,max};
    	
		return valeursLegende;

    }
    
    

    public static Point3D geoCoordTo3dCoord(float lat, float lon) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * TEXTURE_OFFSET,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor))* TEXTURE_OFFSET,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor))* TEXTURE_OFFSET);
    }
    
    public static Point2D SpaceCoordToGeoCoord(Point3D p) {
    	
    	float lat = (float) (Math.asin(-p.getY()/ TEXTURE_OFFSET)
    			* (180/Math.PI)-TEXTURE_LAT_OFFSET);
    	
    	float lon;
    	
    	if(p.getZ()<0) {
    		lon=180 -(float) (Math.asin(-p.getX()/(TEXTURE_OFFSET
    				* Math.cos((Math.PI / 180)
    				* (lat + TEXTURE_LAT_OFFSET)))) * 180 / Math.PI + TEXTURE_LON_OFFSET);
    	}
    	else {
    		lon = (float) (Math.asin(-p.getX() / (TEXTURE_OFFSET * Math.cos((Math.PI / 180)
    			   * (lat + TEXTURE_LAT_OFFSET)))) * 180 / Math.PI - TEXTURE_LON_OFFSET);
    	}
    	return new Point2D(lat,lon);
    }
    
    public void displayPoint(Group parent, String name, float latitude, float longitude) {

    	Point3D point= new Point3D(geoCoordTo3dCoord(latitude, longitude).getX(),
    							   geoCoordTo3dCoord(latitude, longitude).getY(),
    							   geoCoordTo3dCoord(latitude, longitude).getZ());
    	
    	displayPoint3D(point,parent).setId(name);
    }
    
    private void AddBarreHistogramme(Group parent, Pair<Integer,Region> signalement, PhongMaterial material) {
    	
    	Point3D from = signalement.getValue().getPoints()[0];
    	Point3D to = Point3D.ZERO;
    	Point3D yDir = new Point3D(0, 1, 0);
    	
        Box box = new Box(0.01f,0.01f,0.0002f*signalement.getKey());
        box.setTranslateZ(-box.getDepth()/2);
        box.setMaterial(material);

        Affine affine = new Affine();
        affine.append(lookAt(from,to,yDir));
        
        Group group = new Group();
        group.getChildren().add(box);
        
        group.getTransforms().setAll(affine);
        parent.getChildren().addAll(group);
    }
    
    private void AddBarreHistogrammeAnimation(Group parent, Pair<Integer,Region> signalement, PhongMaterial material, boolean animationEnCours) {
    	
    	//if(parent.getChildren().get)
    	
    	
    	
    	Point3D from = signalement.getValue().getPoints()[0];
    	Point3D to = Point3D.ZERO;
    	Point3D yDir = new Point3D(0, 1, 0);
    	
        Box box = new Box(0.01f,0.01f,0.01f);
        box.setMaterial(material);
        //System.out.println("test");
 
        float n = 0.0005f*signalement.getKey();
        
       new AnimationTimer() {
        	public void handle(long currentNanoTime) {
        		
        		if(box.getDepth()<n) {
        			
        		box.setDepth(box.getDepth() + 0.0005);
        		box.setTranslateZ((-box.getDepth())/2);
        		animationEnCours=true;
        		}
        		else {
        			animationEnCours=false;
        		}
        		
        	}
        }.start();
        
      
        
        
        

        Affine affine = new Affine();
        affine.append(lookAt(from,to,yDir));
        
        Group group = new Group();
        group.getChildren().add(box);
        
        group.getTransforms().setAll(affine);
        parent.getChildren().addAll(group);
        
    }

    private void AddQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft,
    		Point3D topLeft, PhongMaterial material) {
    	
    	final TriangleMesh triangleMesh = new TriangleMesh();
    	
    	final float[] points = {
    			(float) topRight.getX(), (float) topRight.getY(), (float) topRight.getZ(),
    			(float) topLeft.getX(), (float) topLeft.getY(), (float) topLeft.getZ(),
    			(float) bottomLeft.getX(), (float) bottomLeft.getY(), (float) bottomLeft.getZ(),
    			(float) bottomRight.getX(), (float) bottomRight.getY(), (float) bottomRight.getZ()	
    	};
    	
    	final float[] texCoords = {
    			1, 1,
    			1, 0,
    			0, 1,
    			0, 0		
    	};
    	
    	final int[] faces = {
    		0, 1, 1, 0, 2, 2,
    		0, 1, 2, 2, 3, 3
    	};
    	
    	triangleMesh.getPoints().setAll(points);
    	triangleMesh.getTexCoords().setAll(texCoords);
    	triangleMesh.getFaces().setAll(faces);
    	
    	final MeshView meshView = new MeshView(triangleMesh);
    	meshView.setMaterial(material);
    	parent.getChildren().add(meshView);
    	
    	}


}