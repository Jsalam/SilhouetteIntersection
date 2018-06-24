package sandBox;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class SandBoxFakeIntersection extends PApplet {

	ArrayList<Circle> circles;
	ArrayList<Metaball> connections;
	float len_Rate = 2.4f;

	public void settings() {
		size(600, 600);
	}

	public void setup() {
		circles = new ArrayList<Circle>();
		// for (int i = 0; i < 2; i++) {
		// circles.add(new Circle(((float) Math.random() * width), ((float)
		// Math.random() * height), 150f));
		// }
		circles.add(new Circle(300f, 100f, 150f));
		circles.add(new Circle(0, 0, 200));
		connections = new ArrayList<Metaball>();
	}

	public void draw() {
		background(255);
		circles.get(circles.size() - 1).center.set(mouseX, mouseY);
		generateConnections(circles);
		for (Circle c : circles) {
			c.show(this);
		}
	}

	public static void main(String[] args) {
		PApplet.main("sandBox.SandBoxFakeIntersection");

	}

	public void generateConnections(ArrayList<Circle> circles) {
		int l = circles.size();
		for (int i = 0; i < l; i++) {
			for (int j = i - 1; j >= 0; j--) {
				Metaball mB = new Metaball(circles.get(i), circles.get(j), this);
				mB.getContour(len_Rate, 0.5f, 100f);
				if (mB != null) {
					connections.add(mB);
					// mB.removeOnMove
				}
			}

		}
	}

}

class Circle {
	public PVector center;
	public float radius;

	public Circle(float x, float y, float radius) {
		center = new PVector(x, y);
		this.radius = radius;

	}

	public void show(PApplet app) {
		app.noFill();
		app.stroke(30, 50);
		app.ellipse(center.x, center.y, radius, radius);
	}
}

class Metaball {
	PVector center1;
	PVector center2;
	float radius1;
	float radius2;
	float distC;
	float u1, u2;
	PApplet app;

	public Metaball(Circle c1, Circle c2, PApplet app) {
		center1 = c1.center;
		center2 = c2.center;
		radius1 = c1.radius;
		radius2 = c2.radius;
		distC = PApplet.dist(center1.x, center1.y, center2.x, center2.y);
		this.app = app;
	}

	public PShape getContour(float len_rate, float v, float maxDistance) {
		if (radius1 == 0 || radius2 == 0) {
			return null;
		}

		if (distC > maxDistance || distC <= PApplet.abs(radius1 - radius2)) {
			return null;
		} else if (distC < radius1 + radius2) { // Case circles are overlapping
			u1 = PApplet.acos((radius1 * radius1 + distC * distC - radius2 * radius2) / (2 * radius1 * distC));
			u2 = PApplet.acos((radius2 * radius2 + distC * distC - radius1 * radius1) / (2 * radius2 * distC));
		} else {
			u1 = 0;
			u2 = 0;
		}

		float angle1 = PApplet.atan2(center2.y - center1.y, center2.x - center2.y); // ?
		float angle2 = PApplet.acos((radius1 - radius2) / distC);
		float angle1a = angle1 + u1 + (angle2 - u1) * v;
		float angle1b = angle1 - u1 - (angle2 - u1) * v;
		float angle2a = angle1 + PApplet.PI - u2 - (PApplet.PI - u2 - angle2) * v;
		float angle2b = angle1 - PApplet.PI - u2 + (PApplet.PI - u2 - angle2) * v;
		PVector p1a = PVector.add(center1, getVector(angle1a, radius1));
		PVector p1b = PVector.add(center1, getVector(angle1b, radius1));
		PVector p2a = PVector.add(center2, getVector(angle2a, radius1));
		PVector p2b = PVector.add(center2, getVector(angle2b, radius1));
		
		PApplet.println(p1a + " " + p1b);
		app.ellipse(p1a.x, p1a.y, 5, 5);

		float totalRadius = radius1 + radius2;
		float dist2 = PApplet.min(v * len_rate, PApplet.dist(p1a.sub(p2a).x, p1a.sub(p2a).y, 0f, 0f) / totalRadius);
		dist2 *= PApplet.min(1, distC * 2 / totalRadius);

		radius1 *= dist2;
		radius2 *= dist2;

		PShape s = app.createShape();
		s.beginShape();
		s.stroke(0);
		s.fill(0, 0, 255, 30);
		s.vertex(p1a.x, p1a.y);
		s.vertex(p2a.x, p2a.y);
		s.vertex(p2b.x, p2b.y);
		s.vertex(p1b.x, p1b.y);
		s.endShape(PApplet.CLOSE);

		return s;

	}

	public PVector getVector(float rads, float length) {
		return new PVector(rads * 180 / PApplet.PI, length);
	}

}
