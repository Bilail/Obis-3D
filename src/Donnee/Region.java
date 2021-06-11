package Donnee;



import application.EarthController;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class Region {
	
	private Point3D p1;
	private Point3D p2;
	private Point3D p3;
	private Point3D p4;
	
	public Region(Point2D[] coords) {
		this.p1= EarthController.geoCoordTo3dCoord((float) coords[0].getX(), (float) coords[0].getY());
		this.p2= EarthController.geoCoordTo3dCoord((float) coords[1].getX(), (float) coords[1].getY());;
		this.p3= EarthController.geoCoordTo3dCoord((float) coords[2].getX(), (float) coords[2].getY());;
		this.p4= EarthController.geoCoordTo3dCoord((float) coords[3].getX(), (float) coords[3].getY());;
	}
	
	public Point3D[] getPoints() {
		Point3D[] points = {p1, p2, p3, p4};
		return points;
	}
	
	@Override
	public String toString() {
		return "{" + p1 + "," + p2 + "," + p3 + "," + p4 + "}";
	}
}
