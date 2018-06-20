package shapes;

import java.awt.Color;

import processing.core.*;

public class Node implements Comparable<Node> {
	public float x;
	public float y;
	public float diam = 4;
	// The angle of the node relative to the center when it is instantiated
	float originalAngle;
	// The angle relative to a given centroid. Used to sort nodes in a silhouette
	float absoluteAngle;
	public int id;
	public boolean selected = false;
	public Node controlPointA;
	public Node controlPointB;
	

	public Node(float x, float y, float originalAngle, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.originalAngle = originalAngle;
	}

	public Node(float x, float y, float originalAngle) {
		this.x = x;
		this.y = y;
		this.originalAngle = originalAngle;
	}

	public void show(PApplet app) {
		if (selected) {
			app.stroke(0, 225, 250);
		} else {
			app.stroke(0, 125);
		}

		app.ellipse(x, y, diam, diam);
		app.text(id  , x + 5, y + 5);

		app.stroke(0, 200, 0, 100);
		if (controlPointA != null)
			app.line(controlPointA.x, controlPointA.y, x, y);
		app.stroke(200, 0, 0, 100);
		if (controlPointB != null)
			app.line(controlPointB.x, controlPointB.y, x, y);
	}

	public void show(PApplet app, Color c) {
		app.stroke(c.getRGB());
		if (selected) {
			app.stroke(0, 225, 250);
		}
		app.ellipse(x, y, diam, diam);
		app.stroke(0, 200, 0, 100);

		if (controlPointA != null)
			app.line(controlPointA.x, controlPointA.y, x, y);

		app.stroke(200, 0, 0, 100);
		if (controlPointB != null)
			app.line(controlPointB.x, controlPointB.y, x, y);

		app.fill(c.getRGB());
		app.text(id , x + 5, y + 5);
		app.noFill();
		app.stroke(0);
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setControlPointA(Node cPoint) {
		controlPointA = cPoint;
	}

	public void setControlPointB(Node cPoint) {
		controlPointB = cPoint;
	}

	public void markNode() {
		selected = true;
	}

	public void unmarkNode() {
		selected = false;
	}

	/**
	 * The angle of the node relative to a centroid. It is used to sort nodes.
	 * @param centroid
	 */
	public void setRelativeAngle(PVector centroid) {

		absoluteAngle = PApplet.atan2(centroid.y - y, centroid.x - x);
		if (absoluteAngle < 0)
			absoluteAngle += PApplet.TWO_PI;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(Node other) {
		float tmp = other.absoluteAngle - this.absoluteAngle;
		if (tmp > 0)
			return -1;
		else
			return 1;
	}

	public boolean equals(Node n) {
		return x == n.x && y == n.y && id == n.id;
	}
}
