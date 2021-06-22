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
    
    public boolean stop=false;
    
    public boolean pause=false;
    
    public int nbPas;

    public void initialize() throws FileNotFoundException, IOException {
    	
		ObjModelImporter objImporter = new ObjModelImporter();
		
		//Create a Pane et graph scene root for the 3D content
		Group root3D = new Group();
      	
      	try {
      		URL modelUrl = this.getClass().getResource("Earth/earth.obj");
      		objImporter.read(modelUrl);
      	}
      	catch (ImportException e) {
      		System.out.println(e.getMessage());
      	}
      	
      	MeshView[] meshViews = objImporter.getImport();
      	earth = new Group(meshViews);
      	root3D.getChildren().add(earth);
        root3D.setFocusTraversable(true);

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
        SubScene subscene = new SubScene(root3D, 400, 604, true, SceneAntialiasing.BALANCED);
        subscene.setCamera(camera);
        subscene.setFill(Color.GREY);
        pane3D.getChildren().addAll(subscene);
    	
    	description.setEditable(false);
    	precision.setText("3");
    	champRecherche.setText("Delphinidae");
    	
    	ArrayList<Pair<Integer, Region>> signalementsJsonFile = Json.JsonFile("Delphinidae.Json") ;
    	
    	int max = signalementsJsonFile.get(0).getKey();
		
		L6.setText("< " + computeLegend(max)[0]);
		L5.setText("< " + computeLegend(max)[1]);
		L4.setText("< " + computeLegend(max)[2]);
		L3.setText("< " + computeLegend(max)[3]);
		L2.setText("< " + computeLegend(max)[4]);
		L1.setText("< " + computeLegend(max)[5]);
		
		StringBuilder donnee = new StringBuilder();
		donnee.append("nombre d'occurence | point 3D \n");
	
		for (Pair<Integer,Region> signalement : signalementsJsonFile) {
		
			final PhongMaterial material = new PhongMaterial();
		
			if(signalement.getKey() <= computeLegend(max)[0]) {material.setDiffuseColor(new Color(0.0, 0.0, 0.5, 0.3));}
			else if(signalement.getKey() <= computeLegend(max)[1]) {material.setDiffuseColor(new Color(0.0, 1.0, 1.0, 0.1));}
			else if(signalement.getKey() <= computeLegend(max)[2]) {material.setDiffuseColor(new Color(0.0, 0.5, 0.0, 0.1));}
			else if(signalement.getKey() <= computeLegend(max)[3]) {material.setDiffuseColor(new Color(1.0, 1.0, 0.0, 0.1));}
			else if(signalement.getKey() <= computeLegend(max)[4]) {material.setDiffuseColor(new Color(1.0, 0.5, 0.0, 0.1));}
			else if(signalement.getKey() <= computeLegend(max)[5]) {material.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));}
		
			Region region = signalement.getValue();
		
			AddQuadrilateral(earth, region.getPoints()[2], region.getPoints()[1], region.getPoints()[0], region.getPoints()[3], material);
			
			donnee.append("\n" + signalement.getKey().toString() + "\n"
    		+"\t" + region.getPoints()[0] +"\n\t" + region.getPoints()[1] + 
    		"\n\t" + region.getPoints()[2]+ "\n\t" + region.getPoints()[3]);
		}
		description.setText(donnee.toString());
    	
  
        
        //Auto completion 
        champRecherche.setOnKeyReleased(new EventHandler<KeyEvent>() {
        	@Override
        	public void handle(KeyEvent event) {	
        		
        			ObservableList<String> items = FXCollections.observableArrayList(Json.completerNoms(champRecherche.getText()));
            		combo.setItems(items);
 
            		
            		stop=false;
            		
            		if (items.size() == 0 && champRecherche.getLength() > 0 ) {
            			
            			Alert alert = new Alert(AlertType.WARNING);
            			alert.setTitle("Message d'alerte");
            			alert.setHeaderText(null);
            			alert.setContentText("Nom d'espèce non repertoriée");
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
        		
        		earth.getChildren().subList(1, earth.getChildren().size()).clear();
    		
        		ArrayList<Pair<Integer, Region>> signalements;
        		
        		if(nbrIntervalle.getText().isBlank() | duree.getText().isBlank()) {

	        		if(dateDebut.getValue()!=null) {
	        			System.out.println(dateDebut.getValue());
	        			
	        			if(dateFin.getValue() !=null) {
	        				signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(), Integer.valueOf(precision.getText()), dateDebut.getValue(), dateFin.getValue());	
	        			}
	        			else { signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(), Integer.valueOf(precision.getText()), dateDebut.getValue(), LocalDate.now());
	        			}	
	        		}
	        		else { signalements = Json.nbSignalementsRegions(champRecherche.getText(), Integer.valueOf(precision.getText()));}
	        		
	        			int max = signalements.get(0).getKey();
	        		
	        			L6.setText("< " + computeLegend(max)[0]);
	        			L5.setText("< " + computeLegend(max)[1]);
	        			L4.setText("< " + computeLegend(max)[2]);
	        			L3.setText("< " + computeLegend(max)[3]);
	        			L2.setText("< " + computeLegend(max)[4]);
	        			L1.setText("< " + computeLegend(max)[5]);
	        			
	        			StringBuilder donnee = new StringBuilder();
	        			donnee.append("nombre d'occurence | point 3D \n");
	        		
	        			for (Pair<Integer,Region> signalement : signalements) {
	        			
	        				final PhongMaterial material = new PhongMaterial();
	        			
	        				if(signalement.getKey() <= computeLegend(max)[0]) {material.setDiffuseColor(new Color(0.0, 0.0, 0.5, 0.3));}
	        				else if(signalement.getKey() <= computeLegend(max)[1]) {material.setDiffuseColor(new Color(0.0, 1.0, 1.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(max)[2]) {material.setDiffuseColor(new Color(0.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(max)[3]) {material.setDiffuseColor(new Color(1.0, 1.0, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(max)[4]) {material.setDiffuseColor(new Color(1.0, 0.5, 0.0, 0.1));}
	        				else if(signalement.getKey() <= computeLegend(max)[5]) {material.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));}
	        			
	        				Region region = signalement.getValue();
	        			
	        				AddQuadrilateral(earth, region.getPoints()[2], region.getPoints()[1], region.getPoints()[0], region.getPoints()[3], material);
	        				AddBarreHistogrammeAnimation(earth,signalement,max,material);
	        				
	        				donnee.append("\n" + signalement.getKey().toString() + "\n"
	    	        		+"\t" + region.getPoints()[0] +"\n\t" + region.getPoints()[1] + 
	    	        		"\n\t" + region.getPoints()[2]+ "\n\t" + region.getPoints()[3]);
	        			}
	        			description.setText(donnee.toString());
        			}
        			else if (dateFin.getValue() == null && !nbrIntervalle.getText().isBlank() && !duree.getText().isBlank()){

    					ArrayList<Object[]> intervalles = Json.nbSignalementsIntervalles(champRecherche.getText(), Integer.valueOf(precision.getText()), 
   						 dateDebut.getValue(), Integer.valueOf(duree.getText()), Integer.valueOf(nbrIntervalle.getText()));
    				
    					StringBuilder donnee = new StringBuilder();
    					donnee.append("nombre d'occurence | annee | point 3D \n");
        			
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
        		
        		if (dateDebut.getValue() != null && dateFin.getValue()!=null){
        			
        			pause=false;
        		
        			earth.getChildren().subList(1, earth.getChildren().size()).clear();
	        		
	        		final long startNanoTime = System.nanoTime();

	                AnimationTimer timer = new AnimationTimer() {
	                	
	                	ArrayList<Pair<Integer, Region>> signalements = new ArrayList<Pair<Integer,Region>>();

	        			int annee1 = dateDebut.getValue().getYear()+5*nbPas;
	        			int annee2 = annee1+5;
	                	
	                	public void handle(long currentNanoTime) {
	                		
	                		double t = (currentNanoTime - startNanoTime) / 1000000000.0;

	    	        		if(annee2>dateFin.getValue().getYear() | pause==true | stop==true) {
	    	        			if(annee2>dateFin.getValue().getYear()) {
	    	        				nbPas=0;
	    	        			}
	    	        			this.stop();
	    	        		}
	    	        		else if(t%13<=0.1) {
	    	        			
	    	        			earth.getChildren().subList(1, earth.getChildren().size()).clear();
	               
	                			
	                			signalements = Json.nbSignalementsRegionsDate(champRecherche.getText(),
	            					Integer.valueOf(precision.getText()),
	            					LocalDate.of(annee1, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()),
	            					LocalDate.of(annee2, dateDebut.getValue().getMonthValue(), dateDebut.getValue().getDayOfMonth()));
	                				
	                			int max = signalements.get(0).getKey();
	                				
	                			L6.setText("< " + computeLegend(max)[0]);
	                    		L5.setText("< " + computeLegend(max)[1]);
	                    		L4.setText("< " + computeLegend(max)[2]);
	                    		L3.setText("< " + computeLegend(max)[3]);
	                    		L2.setText("< " + computeLegend(max)[4]);
	                    		L1.setText("< " + computeLegend(max)[5]);
	                				
	                			StringBuilder donnee = new StringBuilder();
	        	        		donnee.append("nombre d'occurence | annee | point 3D \n");
	                			
		                		for (Pair<Integer,Region> signalement : signalements) {

		    	        			final PhongMaterial material = new PhongMaterial();
		    	        			
		    	        			if(signalement.getKey() <= computeLegend(max)[0]) {material.setDiffuseColor(new Color(0.0, 0.0, 0.5, 0.3));}
		    	        			else if(signalement.getKey() <= computeLegend(max)[1]) {material.setDiffuseColor(new Color(0.0, 1.0, 1.0, 0.1));}
		    	        			else if(signalement.getKey() <= computeLegend(max)[2]) {material.setDiffuseColor(new Color(0.0, 0.5, 0.0, 0.1));}
		    	        			else if(signalement.getKey() <= computeLegend(max)[3]) {material.setDiffuseColor(new Color(1.0, 1.0, 0.0, 0.1));}
		    	        			else if(signalement.getKey() <= computeLegend(max)[4]) {material.setDiffuseColor(new Color(1.0, 0.4, 0.0, 0.3));}
		    	        			else if(signalement.getKey() <= computeLegend(max)[5]) {material.setDiffuseColor(new Color(0.5, 0.0, 0.0, 0.1));}
		    	        			
		    	        			Region region = signalement.getValue();
		    	        			
		    	        			AddBarreHistogrammeAnimation(earth,signalement,max,material);
		    	        				
		    	        			donnee.append("\n" + signalement.getKey().toString() + " entre : " + annee1 + "-" + annee2 + "\n"
		    	    	    	        		+"\t" + region.getPoints()[0] +"\n\t" + region.getPoints()[1] + 
		    	    	    	        		"\n\t" + region.getPoints()[2]+ "\n\t" + region.getPoints()[3]);
		            			}	
	    	        			description.setText(donnee.toString());
	    	        			nbPas=nbPas+1;
		                		annee1=annee1+5;
		            			annee2=annee2+5;
	                		}
	                	}	
	                };
	                timer.start(); 
        		}
        	}
        });
        
        btnPause.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {	
        		pause=true;
        	}
        });
        
        btnStop.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {	
        		stop=true;
        		nbPas=0;
        		earth.getChildren().subList(1, earth.getChildren().size()).clear();
        		champRecherche.setText("");
        		combo.getItems().clear();
        		dateDebut.getEditor().clear();
        		dateFin.getEditor().clear();
        	}
        });

       
        root3D.addEventHandler(MouseEvent.ANY, event ->{
      		if(event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isAltDown()) {
      			
      			PickResult pickResult = event.getPickResult();
      			Point3D spaceCoord = pickResult.getIntersectedPoint();
      			
      			displayPoint3D(spaceCoord,root3D);
      			Point2D geoCoord = SpaceCoordToGeoCoord(spaceCoord);
      			String geohash = GeoHashHelper.getGeohash(new Location("selectedGeoHash", geoCoord.getX(), geoCoord.getY()));
      			geohash = geohash.substring(0, 2);
      			
      			ArrayList<Signalement> signalements = Json.rechercherSignalements(champRecherche.getText(), geohash);
      			
      			StringBuilder sb = new StringBuilder();
      			ArrayList<String> noms = new ArrayList<String>();
      			
      			for(Signalement signalement : signalements) {
      				sb.append(signalement.toString());
      				if(noms.contains(signalement.getscientificName())==false) {
          				noms.add(signalement.getscientificName());
      				}
      			}
      			
      			ObservableList<String> items = FXCollections.observableArrayList(noms);
      			listeEspeces.setItems(items);
      			description.setText(sb.toString());
      			
      			
      		}
      	});
        
        listeEspeces.setOnMouseClicked(new EventHandler<MouseEvent>(){
        	@Override
        	public void handle(MouseEvent event) {
        		champRecherche.setText(listeEspeces.getSelectionModel().getSelectedItem());
        		combo.getSelectionModel().select(null);
        	}
        });

    }
    
    
    
    
    public void displayPoint(Group parent, String name, float latitude, float longitude) {

    	Point3D point= new Point3D(geoCoordTo3dCoord(latitude, longitude).getX(),
    							   geoCoordTo3dCoord(latitude, longitude).getY(),
    							   geoCoordTo3dCoord(latitude, longitude).getZ());
    	
    	displayPoint3D(point,parent).setId(name);
    }

    public Group displayPoint3D(Point3D p, Group parent) {
    	
    	Sphere sphere = new Sphere(0.005);
    	
    	sphere.setTranslateX(p.getX());
    	sphere.setTranslateY(p.getY());
    	sphere.setTranslateZ(p.getZ());
    	
      	parent.getChildren().add(sphere);
      	
      	return parent;
    }

    public static Affine lookAt(Point3D from, Point3D to, Point3D ydir) {
    	
        Point3D zVec = to.subtract(from).normalize();
        Point3D xVec = ydir.normalize().crossProduct(zVec).normalize();
        Point3D yVec = zVec.crossProduct(xVec).normalize();
        
        return new Affine(xVec.getX(), yVec.getX(), zVec.getX(), from.getX(),
                          xVec.getY(), yVec.getY(), zVec.getY(), from.getY(),
                          xVec.getZ(), yVec.getZ(), zVec.getZ(), from.getZ());
    }
    
    /**
     * Fonction parmettant d'échelonner la légende
     * @param max la valeur max prise par la légende
     * @return les valeurs de légende
     */
    public static int[] computeLegend(int max) {

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

    /**
     * Fonction permettant de calculer le centre d'une zone pour centrer les barres d'histogramme
     * @param signalement
     * @return
     */
    public Point3D calculerCentre(Pair<Integer,Region> signalement) {
    	
    	Point3D centre = signalement.getValue().getPoints()[0].midpoint(signalement.getValue().getPoints()[2]);
    	
    	return centre;
    }
    
    /**
     * Fonction affichant des barres d'histogrammes qui augmentent progressivement
     * @param parent le parent
     * @param signalement un signalement
     * @param max le nombre d'occurrence maximal pour échelonner la taille des barres d'histogrammes
     * @param material 
     */
    private void AddBarreHistogrammeAnimation(Group parent, Pair<Integer,Region> signalement, int max, PhongMaterial material) {

    	Point3D from = calculerCentre(signalement);
    	Point3D to = Point3D.ZERO;
    	Point3D yDir = new Point3D(0, 1, 0);
    	
    	float cote;
    	
    	if(Integer.valueOf(precision.getText())==4) {
    		cote=0.005f;
    	}
    	else if(Integer.valueOf(precision.getText())==3) {
			cote=0.015f;
		}
    	else {
    		cote=0.05f;
    	}
    	
        Box box = new Box(cote,cote,0.01f);
        box.setMaterial(material);
 
        float n = 0.0001f*signalement.getKey()*8000/max; //échelonnage
        
        final long startNanoTime = System.nanoTime();
        
        new AnimationTimer() {
        	public void handle(long currentNanoTime) {
        		
        		double t = (currentNanoTime - startNanoTime) / 1000000000.0;
        		
        		if(box.getDepth()<n) {
        			
        		box.setDepth(t*0.07);
        		box.setTranslateZ((-box.getDepth())/2);

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