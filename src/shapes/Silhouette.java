package shapes;

import java.util.ArrayList;
import java.util.Collections;

import processing.core.*;

public class Silhouette {

  public ArrayList <VertexCircle> vCs;
  private ArrayList<Node> silouette;
  private PVector midpoint;

  public Silhouette() {
    vCs = new ArrayList<VertexCircle>();
  }

  public boolean addVertexCircle(VertexCircle vC) {
    return vCs.add(vC);
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


  public void getSilhouette() {

    // This is for storing the nodes removed from each circle
    ArrayList<ArrayList <Node>> negativeCircles = new ArrayList <ArrayList <Node>>();

    // Populate collection with empty node lists
    for (int i=0; i < vCs.size(); i++) {
      negativeCircles.add(new ArrayList <Node>());
    }

    // This list stores the intersection of all circles after removing negative nodes
    ArrayList<Node>  rtn = new ArrayList<Node>();

    // for each pair of circles
    for (int i=0; i< vCs.size() -1; i++) {
      VertexCircle A = vCs.get(i);

      for (int j=i+1; j<vCs.size(); j++) {
        VertexCircle B = vCs.get(j);

        // Midpoint
        midpoint = getMidIntersection(A, B);

        // This is a temporal set of nodes
        ArrayList <Node> tmp = new ArrayList <Node>();

        // if circles overlap
        if (PApplet.dist(A.orig.x, A.orig.y, B.orig.x, B.orig.y) < A.radius + B.radius) {

          // find nodes in circle A that belong to the intersection
          for (Node n : A.getNodes()) {
            if (PApplet.dist(n.x, n.y, B.orig.x, B.orig.y) < B.radius) {

              // mark the nodes
              n.markNode();

              // store the set in a separate negative circle
              if (!negativeCircles.get(i).contains(n))
                negativeCircles.get(i).add(n);
            } else {
              if (n.selected && tmp.contains(n))
                tmp.remove(n);
              else if (!n.selected)
                tmp.add(n);
            }
          }

          // find nodes in circle B that belong to the intersection
          for (Node n : B.getNodes()) {
            if (PApplet.dist(n.x, n.y, A.orig.x, A.orig.y) < A.radius) {

              // mark the nodes
              n.markNode();

              // store the set in a separate negative circle
              if (!negativeCircles.get(j).contains(n))
                negativeCircles.get(j).add(n);
            } else {
              if (n.selected && tmp.contains(n))
                tmp.remove(n);
              else if (!n.selected)
                tmp.add(n);
            }
          }

          // set angles for ordering
          for (Node n : tmp) {
            ///// use get centorids here **********
            n.setRelativeAngle(midpoint);
          }

          // sort nodes
          Collections.sort(tmp);

          // Add temp to final result
          rtn.addAll(tmp);
        }
      }
    }

    silouette = rtn;
  }

  public void showSilouette(PApplet app) {
    if (silouette.size() > 0) {
      app.beginShape();
      app.stroke(0, 100);
      app.fill(225, 255, 155, 30);
      app.curveVertex(silouette.get(silouette.size() -1).x, silouette.get(silouette.size() -1).y);
      for (int i = 0; i< silouette.size(); i++) {
        silouette.get(i).show(app);
        app.stroke(0, 100);
        app.curveVertex(silouette.get(i).x, silouette.get(i).y);
      }
      app.curveVertex(silouette.get(0).x, silouette.get(0).y);
      app.curveVertex(silouette.get(1).x, silouette.get(1).y);
      app.endShape();
    }
  }

  public void showMidpoint(PApplet app) {
    if (midpoint != null) {
    	app.fill(255, 0, 0);
    	app.ellipse (midpoint.x, midpoint.y, 4, 4);
    }
  }

  public PVector getMidIntersection(VertexCircle A, VertexCircle B) {
    // find the distance to midpoint
    float d = A.radius - ((B.radius + A.radius) - PApplet.dist(A.orig.x, A.orig.y, B.orig.x, B.orig.y)) /2;

    // find the angle between centers
    float angle = PApplet.atan2(A.orig.y - B.orig.y, A.orig.x - B.orig.x);

    // find the coordinates of midpoint on the direction of the angle
    float x = A.orig.x - PApplet.cos(angle) * d;
    float y = A.orig.y - PApplet.sin(angle) * d;

    // return the coordinates
    return new PVector(x, y);
  }
}
