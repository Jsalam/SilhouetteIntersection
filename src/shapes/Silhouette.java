package shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import processing.core.*;
import utilities.CircleIntersector;

public class Silhouette {

	public ArrayList<VertexCircle> vCs;
	private ArrayList<Node> silouette;
	private PVector midpoint;

	public Silhouette() {
		vCs = new ArrayList<VertexCircle>();
	}

	public boolean addVertexCircle(VertexCircle vC) {
		return vCs.add(vC);
	}

	public void getSilhouette() {

		// This is for storing the nodes removed from each circle
		ArrayList<ArrayList<Node>> negativeCircles = new ArrayList<ArrayList<Node>>();

		// Populate collection with empty node lists
		for (int i = 0; i < vCs.size(); i++) {
			negativeCircles.add(new ArrayList<Node>());
		}

		// This list stores the intersection of all circles after removing negative
		// nodes
		ArrayList<Node> rtn = new ArrayList<Node>();

		// for each pair of circles
		for (int i = 0; i < vCs.size() - 1; i++) {
			VertexCircle A = vCs.get(i);

			for (int j = i + 1; j < vCs.size(); j++) {
				VertexCircle B = vCs.get(j);

				// Midpoint
				midpoint = CircleIntersector.getMidIntersection(A, B);

				// This is a temporal set of nodes
				ArrayList<Node> tmp = new ArrayList<Node>();

				// if circles overlap
				if (CircleIntersector.validateIntersection(A, B)) {

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

	public void getSilhouette2() {

		// This is for storing references to nodes marked in each circle
		HashMap<String, ArrayList<Node>> negativeMap = new HashMap<String, ArrayList<Node>>();

		// This list stores the intersection of all circles after removing negative
		// nodes
		ArrayList<Node> rtn = new ArrayList<Node>();

		// for each pair of circles
		for (int i = 0; i < vCs.size() - 1; i++) {
			VertexCircle A = vCs.get(i);

			for (int j = i + 1; j < vCs.size(); j++) {
				VertexCircle B = vCs.get(j);

				// Midpoint
				midpoint = CircleIntersector.getMidIntersection(A, B);

				// This is a temporal set of nodes for current circle
				ArrayList<Node> tmp = new ArrayList<Node>();

				// if circles overlap
				if (CircleIntersector.validateIntersection(A, B)) {

					// ****** find nodes in circle **** A **** that belong to the intersection
					PVector[] points = CircleIntersector.twoIntersectionPoints(A, B);

					float[] startEndA = CircleIntersector.startEndAngleIntersection(A, points[0], points[1]);

					// All A's nodes in scope of angle
					HashMap<String, ArrayList<Node>> tmpMapA = A.getNodesBetweenAngle(startEndA[0], startEndA[1], true);
					negativeMap.put(A.id, tmpMapA.get("inScope"));

					// ***** find nodes in circle **** B **** that belong to the intersection
					points = CircleIntersector.twoIntersectionPoints(B, A);

					float[] startEndB = CircleIntersector.startEndAngleIntersection(B, points[0], points[1]);

					// All B's marked nodes
					HashMap<String, ArrayList<Node>> tmpMapB = B.getNodesBetweenAngle(startEndB[0], startEndB[1], true);
					negativeMap.put(B.id, tmpMapB.get("inScope"));

					// aggregate nodes out of scope
					tmp.addAll(tmpMapA.get("outScope"));
					tmp.addAll(tmpMapB.get("outScope"));

					// set angles for ordering
					for (Node n : tmp) {
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
			app.curveVertex(silouette.get(silouette.size() - 1).x, silouette.get(silouette.size() - 1).y);
			for (int i = 0; i < silouette.size(); i++) {
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
			app.ellipse(midpoint.x, midpoint.y, 4, 4);
		}
	}
}
