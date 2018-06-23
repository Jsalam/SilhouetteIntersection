package shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

import processing.core.*;
import utilities.Centroid;
import utilities.CircleIntersector;
import utilities.HoleSorter;

/**
 * @author juan salamanca
 *
 */
public class Silhouette {

	private ArrayList<VertexCircle> vCs;
	private ArrayList<Node> silhouette;
	private PVector midpoint;
	// This is for storing references to nodes marked in each circle
	private TreeSet<Node> negativeMap;

	public Silhouette() {
		vCs = new ArrayList<VertexCircle>();
		negativeMap = new TreeSet<Node>();
		silhouette = new ArrayList<Node>();
	}

	public boolean addVertexCircle(VertexCircle vC) {
		return vCs.add(vC);
	}

	/**
	 * Creates a silhouette
	 */
	public void getSilhouette(PApplet app) {

		// If there is at least two vertexCircles
		if (vCs.size() >= 2) {

			getNegativeNodes();

			System.out.println(this.getClass().getName() + " negativeMap size:" + negativeMap.size());
			
			for(Node n: negativeMap) {
				n.print();
			}

			if (negativeMap.size() > 0) {

				// For each VertexCircle, reset the node marks and set them again based on the
				// negative collection
				for (VertexCircle vc : vCs) {
					// unmark all nodes
					// vc.unmarkAllNodes();

					// mark selected all the nodes of each vertexCircle that belong to the negative
					// collection
					// for (Node n : vc.getNodes()) {
					// if (negativeMap.contains(n)) {
					// n.markNode();
					// }
					// }

					// sort each collection
					// HoleSorter.sortHole(vc.getNodes());
				}
			}
			// concatenate them
			// concatenateSilhouette();
		}
	}

	/**
	 * Detects all the nodes inside intersections and mark then as 'negative'. The
	 * returned list contains an ordered sequence of non-negative nodes
	 */
	public void getNegativeNodes() {
		 negativeMap.clear();

		// For each VertexCircles
		for (int i = 0; i < vCs.size() - 1; i++) {
			VertexCircle A = vCs.get(i);

			for (int j = i + 1; j < vCs.size(); j++) {
				VertexCircle B = vCs.get(j);

				// if circles overlap
				if (CircleIntersector.validateIntersection(A, B)) {

					// ****** Add nodes of A to negative set and mark them ****
					// Find intersection point
					PVector[] intersectionPointsA = CircleIntersector.twoIntersectionPoints(A, B);
					 addNodesToNegativeMap(A, intersectionPointsA);

					// ****** Add nodes of B to negative set and mark them ****
					// Find intersection point
					PVector[] intersectionPointsB = CircleIntersector.twoIntersectionPoints(B, A);
					 addNodesToNegativeMap(B, intersectionPointsB);

					// else if A is included in B
				} else if (CircleIntersector.validateInclusion(A, B)) {
					// Mark all A nodes as selected and add the to negative list
					for (Node n : A.getNodes()) {
						n.markNode();
						negativeMap.add(n);
					}
					// else if B is included in A
				} else if (CircleIntersector.validateInclusion(B, A)) {
					// Mark all B nodes as selected and add the to negative list
					for (Node n : B.getNodes()) {
						n.markNode();
						negativeMap.add(n);
					}
					// else if no intersection and no inclusion
				} 
			}
		}
	}

	/**
	 * If vertexCircle's nodes are within the scope of intersection angle then nodes
	 * are marked and added to negative collection
	 * 
	 * @param negativeMap
	 * @param vCircle
	 * @param intersectionPoints
	 */
	private void addNodesToNegativeMap(VertexCircle vCircle, PVector[] intersectionPoints) {
		// Find angles of intersection scope
		float[] startEnd = CircleIntersector.startEndAngleIntersection(vCircle, intersectionPoints[0],
				intersectionPoints[1]);

		// mark A's nodes in scope of angle
		vCircle.markNodesBetweenAngle(startEnd[0], startEnd[1], true);

		// add all marked nodes to the negative collection
		for (Node n : vCircle.getNodes()) {
			if (n.selected) {
				negativeMap.add(n);
			}
		}
	}

	private void concatenateSilhouette() {
		silhouette.clear();

		// For each VertexCircle
		for (VertexCircle vc : vCs) {

			// retrieve the nonMarked nodes
			for (Node n : vc.getNodes()) {
				if (!n.selected) {
					silhouette.add(n);
				}
			}
		}
	}

	/**
	 * @deprecated
	 */
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

				// This is a temporal set of nodes for current circle
				ArrayList<Node> tmp = new ArrayList<Node>();

				// if circles overlap
				if (CircleIntersector.validateIntersection(A, B)) {

					// ****** find nodes in circle **** A **** that belong to the intersection
					PVector[] points = CircleIntersector.twoIntersectionPoints(A, B);

					float[] startEndA = CircleIntersector.startEndAngleIntersection(A, points[0], points[1]);

					// All A's nodes in scope of angle
					HashMap<String, ArrayList<Node>> tmpMapA = A.getNodeListsInOutScope(startEndA[0], startEndA[1],
							true);

					if (negativeMap.containsKey(A.id)) {
						negativeMap.get(A.id).addAll(tmpMapA.get("inScope"));

					} else {
						negativeMap.put(A.id, tmpMapA.get("inScope"));
					}
					// ***** find nodes in circle **** B **** that belong to the intersection
					points = CircleIntersector.twoIntersectionPoints(B, A);

					float[] startEndB = CircleIntersector.startEndAngleIntersection(B, points[0], points[1]);

					// All B's marked nodes
					HashMap<String, ArrayList<Node>> tmpMapB = B.getNodeListsInOutScope(startEndB[0], startEndB[1],
							true);

					if (negativeMap.containsKey(B.id)) {
						negativeMap.get(B.id).addAll(tmpMapB.get("inScope"));

					} else {
						negativeMap.put(B.id, tmpMapB.get("inScope"));
					}

					// aggregate nodes out of scope and non-negative

					for (Node n : tmpMapA.get("outScope")) {
						if (!negativeMap.get(A.id).contains(n)) {
							tmp.add(n);
						}
					}

					for (Node n : tmpMapB.get("outScope")) {
						if (!negativeMap.get(B.id).contains(n)) {
							tmp.add(n);
						}
					}

					// set angles for ordering
					midpoint = CircleIntersector.getMidIntersection(A, B);

					for (Node n : tmp) {
						// It might be necessary to use centroid here
						n.setAbsoluteAngle(midpoint);
					}

					// sort nodes
					Collections.sort(tmp);

					// Add temp to final result
					for (Node n : tmp) {
						if (!rtn.contains(n))
							rtn.add(n);
					}

					// set angles for ordering
					midpoint = Centroid.getCentroid(rtn);

					for (Node n : rtn) {
						// It might be necessary to use centroid here
						n.setAbsoluteAngle(midpoint);
					}

					// sort nodes
					Collections.sort(rtn);

				}
			}
		}

		silhouette = rtn;
	}

	public void showSilouette(PApplet app) {

		if (silhouette.size() > 3) {
			app.beginShape();
			app.stroke(0, 100);
			app.fill(225, 255, 155, 30);
			app.curveVertex(silhouette.get(silhouette.size() - 1).x, silhouette.get(silhouette.size() - 1).y);
			for (int i = 0; i < silhouette.size(); i++) {
				silhouette.get(i).show(app);
				app.stroke(0, 100);
				app.curveVertex(silhouette.get(i).x, silhouette.get(i).y);
			}
			app.curveVertex(silhouette.get(0).x, silhouette.get(0).y);
			app.curveVertex(silhouette.get(1).x, silhouette.get(1).y);
			app.endShape();
		}

		for (Node n : negativeMap) {
			n.show(app, Color.GREEN);
		}
	}

	public void showMidpoint(PApplet app) {
		if (midpoint != null) {
			app.fill(255, 0, 0);
			app.ellipse(midpoint.x, midpoint.y, 4, 4);
		}
	}
}
