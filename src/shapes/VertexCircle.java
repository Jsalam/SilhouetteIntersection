package shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.*;

/**
 * Implementation of curveVertex() which is a function that implements
 * Catmull-Rom splines. One of the features of the Catmull-Rom spline is that
 * the specified curve will pass through all of the control points. Hence it
 * would require at least three control points to describe a closed shape. Here
 * I am experimenting with approximations to circles. With six control points
 * you can get a decent approximation, but it is recommended not to use less
 * than 8
 * 
 * @author juan salamanca
 *
 */
public class VertexCircle {

	/*
	 * The circle requires three more vertex than the number of sections
	 * parameterized: origin, close and end. The origin is the last one and guides
	 * the first vertex. The close is the same as the first. The end is the second
	 * one and guides the close one.
	 */

	private ArrayList<Node> nodes;
	public float radius;
	public PVector orig;
	public String id;

	/**
	 * Constructor
	 * 
	 * @param ox
	 *            origin X
	 * @param oy
	 *            origin Y
	 * @param radius
	 *            radius
	 * @param sections
	 *            number of sections of the shape. Minimum three
	 */
	public VertexCircle(float ox, float oy, float radius, int sections, String id) {
		if (sections >= 3) {
			this.radius = radius;
			this.id = id;
			orig = new PVector(ox, oy);
			nodes = new ArrayList<Node>();
			setNodesCoordinates(sections);

		} else {
			System.out.println("Section must be larger than 2");
		}
	}

	private void setNodesCoordinates(int sections) {
		float angle = 0;
		float section = PApplet.TWO_PI / sections;

		for (int i = 0; i < sections; i++) {
			float x = PApplet.cos(angle) * radius;
			float y = PApplet.sin(angle) * radius;
			nodes.add(new Node(orig.x + x, orig.y + y, angle, i));
			angle += section;
		}
	}

	private void updateNodesCoordinates() {
		float angle = 0;
		float section = PApplet.TWO_PI / nodes.size();

		for (Node n : nodes) {
			float x = orig.x + PApplet.cos(angle) * radius;
			float y = orig.y + PApplet.sin(angle) * radius;
			n.x = x;
			n.y = y;
			angle += section;
		}
	}

	/**
	 * Show with nodes
	 * 
	 * @param app
	 */
	public void show(PApplet app) {
		app.text(id, orig.x, orig.y);
		app.beginShape();
		app.stroke(0, 100);
		app.curveVertex(nodes.get(nodes.size() - 1).x, nodes.get(nodes.size() - 1).y);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).show(app, Color.RED);
			app.stroke(0, 100);
			app.curveVertex(nodes.get(i).x, nodes.get(i).y);
		}
		app.noFill();
		app.curveVertex(nodes.get(0).x, nodes.get(0).y);
		app.curveVertex(nodes.get(1).x, nodes.get(1).y);
		app.endShape();
	}

	/**
	 * show with center and no nodes
	 * 
	 * @param app
	 */
	public void show2(PApplet app) {
		app.stroke(0, 255, 0);
		app.ellipse(orig.x, orig.y, 5, 5);

		app.beginShape();
		app.noStroke();
		app.fill(0, 20);
		app.curveVertex(nodes.get(nodes.size() - 1).x, nodes.get(nodes.size() - 1).y);
		for (int i = 0; i < nodes.size(); i++) {
			app.curveVertex(nodes.get(i).x, nodes.get(i).y);
		}
		app.curveVertex(nodes.get(0).x, nodes.get(0).y);
		app.curveVertex(nodes.get(1).x, nodes.get(1).y);
		app.endShape();
		app.noFill();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * Returns a collection of nodes which angle relative to the center of the shape
	 * fall between the boundaries of the parameter angle. If 'inclusive'is true,
	 * the resulting list includes nodes with angles equal to the boundaries.
	 * 
	 * 
	 * @param start
	 *            initial reference of parameter angle
	 * @param end
	 *            final reference of parameter angle
	 * @param inclusive
	 *            true if nodes equals to boundaries are included
	 * @return
	 */
	public HashMap<String, ArrayList<Node>> getNodesBetweenAngle(float start, float end, boolean inclusive) {

		ArrayList<Node> inScope = new ArrayList<Node>();

		ArrayList<Node> outScope = new ArrayList<Node>();

		for (Node n : nodes) {
			n.unmarkNode();

			// If the angle scope is continue
			if (start < end) {

				if (inclusive) {
					if (start <= n.originalAngle && n.originalAngle <= end) {
						n.markNode();
						inScope.add(n);
					} else {
						outScope.add(n);
					}
				} else {
					if (start < n.originalAngle && n.originalAngle < end) {
						n.markNode();
						inScope.add(n);
					} else {
						outScope.add(n);
					}
				}
				// If the angle scope goes beyond TWO PI
			} else {

				if (inclusive) {
					if (end >= n.originalAngle || n.originalAngle >= start) {
						n.markNode();
						inScope.add(n);
					} else {
						outScope.add(n);
					}
				} else {
					if (end > n.originalAngle || n.originalAngle > start) {
						n.markNode();
						inScope.add(n);
					} else {
						outScope.add(n);
					}

				}
			}
		}

		HashMap<String, ArrayList<Node>> rtn = new HashMap<String, ArrayList<Node>>();
		rtn.put("inScope", inScope);
		rtn.put("outScope", outScope);
		return rtn;
	}

	/**
	 * Set true the 'isIn' variable of nodes if the are between the boundaries of a
	 * given angle
	 * 
	 * @param start
	 *            initial reference of parameter angle
	 * @param end
	 *            final reference of parameter angle
	 * @param inclusive
	 *            true if nodes equals to boundaries are included
	 * @return the list or marked nodes
	 * @deprecated
	 */
	public void markNodesBetweenAngle(float start, float end, boolean inclusive) {
		for (Node n : nodes) {
			n.unmarkNode();
		}

		for (Node n : getNodesBetweenAngle(start, end, inclusive).get("inScope")) {
			n.markNode();
		}
	}

	public void unmarkNodesBetweenAngle() {
		for (Node n : nodes) {
			n.unmarkNode();
		}
	}

	public void updateCenter(PVector center) {
		orig.set(center);
		updateNodesCoordinates();
	}

	/*
	 * Equality is defined by location and radius
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object vC) {
		try {
			VertexCircle tmp = (VertexCircle) vC;
			if (tmp.orig.equals(orig) && tmp.radius == radius)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

}
