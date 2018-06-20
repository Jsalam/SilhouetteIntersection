package executable;

import processing.core.PApplet;
import processing.core.PVector;
import shapes.Silhouette;
import shapes.VertexCircle;
import utilities.CircleIntersector;

public class Executable extends PApplet {

	Silhouette s;
	VertexCircle a, b, c;
	PVector[] points;

	public void settings() {
		size(600, 600);
	}

	public void setup() {
		int sections = 12;
		a = new VertexCircle(320, 320, 150, sections, "A");
		b = new VertexCircle(200, 200, 100, sections, "B");
		c = new VertexCircle(400, 200, 80, sections, "C");
		s = new Silhouette();
		s.addVertexCircle(a);
		//s.addVertexCircle(b);
		s.addVertexCircle(c);
		
		textSize(8);
	}

	public void draw() {
		background(250);
		stroke(0, 30);
		line(0, height / 2, width, height / 2);
		line(width / 2, 0, width / 2, height);

		s.getSilhouette();
		a.show(this);
		b.show(this);
		c.show(this);
		fill(125, 125, 250);

		//a.radius = dist(a.orig.x, a.orig.y, mouseX, mouseY);
		c.updateCenter(new PVector(mouseX, mouseY));
		
		
		float[] startEndA;
		float[] startEndB;
		
		if (CircleIntersector.validateIntersection(a, c)) {
			points = CircleIntersector.twoIntersectionPoints(a, c);
			ellipse(points[0].x, points[0].y, 5, 5);
			ellipse(points[1].x, points[1].y, 5, 5);
			startEndA = CircleIntersector.StartEndAngleIntersection(a, points[0], points[1]);
			startEndB = CircleIntersector.StartEndAngleIntersection(c, points[1], points[0]);
			a.markNodesBetweenAngle(startEndA[0], startEndA[1], true);
			c.markNodesBetweenAngle(startEndB[0], startEndB[1], true);
		} else {
			a.unmarkNodesBetweenAngle();
			c.unmarkNodesBetweenAngle();
		}
		
		s.showSilouette(this);
		
	}

	public static void main(String[] args) {
		PApplet.main("executable.Executable");
	}

}
