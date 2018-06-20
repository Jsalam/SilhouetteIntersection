package utilities;

import java.util.ArrayList;

import processing.core.PVector;
import shapes.Node;

public class Centroid {
	
	public PVector centroid;
	
	public Centroid (ArrayList<Node> corners) {
		centroid = getCentroid(corners);
	}
	
	/*Implementation of wikipedia formula. This method is copied from the web
	  Corners is a list of nodes in space
	  */
	 public PVector getCentroid(ArrayList<Node> corners) {
	   double xsum = 0, ysum = 0, A = 0;

	   for (int i = 0; i < corners.size(); i++) {
	     int iPlusOne = (i==corners.size()-1)?0:i+1;

	     xsum += (corners.get(i).x + corners.get(iPlusOne).x) * (corners.get(i).x * corners.get(iPlusOne).y - corners.get(iPlusOne).x * corners.get(i).y);
	     ysum += (corners.get(i).y + corners.get(iPlusOne).y) * (corners.get(i).x * corners.get(iPlusOne).y - corners.get(iPlusOne).x * corners.get(i).y);
	     A += (corners.get(i).x * corners.get(iPlusOne).y - corners.get(iPlusOne).x * corners.get(i).y);
	   }
	   A = A / 2;
	   if (xsum==0 && ysum==0)
	   {
	     //area = averageHeight/2;
	     return new PVector (0, 0);
	   }
	   double x = xsum / (6 * A);
	   double y = ysum / (6 * A);

	   return new PVector((float) x, (float) y);
	 }

}
