package sandBox;

import processing.core.PApplet;
import geomerative.*;

public class RShapeTrial extends PApplet {

	RShape shp1;
	RShape shp2;
	RShape shp3;
	RShape u;
	RShape w;

	public void settings() {
		size(600, 600);
	}

	public void setup() {

		RG.init(this);
		shp1 = RG.getEllipse(200, height / 2, 200, 200);
		shp2 = RG.getEllipse(300, height / 2, 150, 150);
		shp3 = RG.getEllipse(mouseX, mouseY, 150, 150);
		u = shp1.union(shp2);
		w = u;
	}

	public void draw() {
		background(50, 50, 195);
		noFill();
		stroke(255);

		RPoint tmp = shp3.getCenter();
		shp3.translate(new RPoint(mouseX - tmp.x, mouseY - tmp.y));
		fill(255, 30);

		if (w != null)
			RG.shape(w);
	}

	public void mouseMoved() {
		if (shp3.intersects(u))
			w = u.union(shp3);
		else
			w = u;

	}

	public static void main(String[] args) {
		PApplet.main("sandBox.RShapeTrial");
	}

}
