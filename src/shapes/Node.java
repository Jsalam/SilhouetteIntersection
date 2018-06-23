package shapes;

import java.awt.Color;

import processing.core.*;

public class Node implements Comparable<Node> {
	public float x;
	public float y;
	public String shapeId;
	public float diam = 4;
	// The angle of the node relative to the center when it is instantiated
	float originalAngle;
	// The angle relative to a given centroid. Used to sort nodes in a silhouette
	float absoluteAngle;
	public int id;
	public boolean selected = false;
	public Node controlPointA;
	public Node controlPointB;

	public Node(float x, float y, float originalAngle, String shapeId, int id) {
		this.x = x;
		this.y = y;
		this.shapeId = shapeId;
		this.id = id;
		this.originalAngle = originalAngle;
	}

	public void show(PApplet app) {

		app.stroke(0, 125);
		app.noFill();

		app.ellipse(x, y, diam, diam);
		app.text(id, x + 5, y + 5);

		app.stroke(0, 200, 0, 100);
		if (controlPointA != null)
			app.line(controlPointA.x, controlPointA.y, x, y);
		app.stroke(200, 0, 0, 100);
		if (controlPointB != null)
			app.line(controlPointB.x, controlPointB.y, x, y);

		app.stroke(0);
		app.noFill();
	}

	public void show(PApplet app, Color c) {

		app.fill(c.getRGB());
		app.stroke(c.getRGB());

		app.ellipse(x, y, diam, diam);

		app.stroke(0, 200, 0, 100);

		if (controlPointA != null)
			app.line(controlPointA.x, controlPointA.y, x, y);

		if (controlPointB != null)
			app.line(controlPointB.x, controlPointB.y, x, y);
		app.fill(0);
		app.text(id, x + 5, y + 5);
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
	 * 
	 * @param centroid
	 * @deprecated
	 */
	public void setAbsoluteAngle(PVector centroid) {

		absoluteAngle = PApplet.atan2(centroid.y - y, centroid.x - x);
		if (absoluteAngle < 0)
			absoluteAngle += PApplet.TWO_PI;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(Node other) {
		float tmp = other.originalAngle - this.originalAngle;

		if (this.equals(other)) {
			return 0;
		} else if (tmp > 0) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(absoluteAngle);
		result = prime * result + id;
		result = prime * result + Float.floatToIntBits(originalAngle);
		result = prime * result + ((shapeId == null) ? 0 : shapeId.hashCode());
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (Float.floatToIntBits(originalAngle) == Float.floatToIntBits(other.originalAngle) && id == other.id
				&& Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y) && other.shapeId.equals(shapeId))
			return true;
		return false;
	}

	public void print() {
		System.out.println(shapeId + ", " + id + ", " + selected + " " + hashCode());
	}
}
