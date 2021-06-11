package Model;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;



public class EarthModel {
	
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    private static final float TEXTURE_OFFSET = 1.01f;
	
	
public Group displayPoint3D(Point3D p, Group parent) {
    	
    	Sphere sphere = new Sphere(0.005);
    	
    	sphere.setTranslateX(p.getX());
    	sphere.setTranslateY(p.getY());
    	sphere.setTranslateZ(p.getZ());
    	
      	parent.getChildren().add(sphere);
      	
      	return parent;
    }
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

public void AddQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft,
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

	}
}
