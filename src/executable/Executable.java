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
		int sections = 6;
		a = new VertexCircle(320, 420, 150, sections, "A");
		b = new VertexCircle(200, 230, 85, sections, "B");
		c = new VertexCircle(0, 0, 100, sections, "C");
		s = new Silhouette();
		s.addVertexCircle(a);
		s.addVertexCircle(b);
		s.addVertexCircle(c);
		
		textSize(8);
		
	}

	public void draw() {
		background(250);
		stroke(0, 30);
		line(0, height / 2, width, height / 2);
		line(width / 2, 0, width / 2, height);

		s.getSilhouette(this);
		a.show(this);
		b.show(this);
		c.show(this);
		fill(125, 125, 250);

		//a.radius = dist(a.orig.x, a.orig.y, mouseX, mouseY);
		c.updateCenter(new PVector(mouseX, mouseY));
		
		s.showSilouette(this);
	}

	public static void main(String[] args) {
		PApplet.main("executable.Executable");
	}

}
