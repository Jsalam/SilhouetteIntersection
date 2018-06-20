package utilities;

import processing.core.PApplet;
import processing.core.PVector;
import shapes.VertexCircle;

public abstract class CircleIntersector {
	/*
	 * Implementation of
	 * http://www.ambrsoft.com/TrigoCalc/Circles2/circle2intersection/
	 * CircleCircleIntersection.htm
	 */

	/**
	 * True is there is a strict intersection.
	 * 
	 * @param a
	 *            first circle
	 * @param b
	 *            second circle
	 * @return true if circles intersect
	 */
	public static boolean validateIntersection(VertexCircle a, VertexCircle b) {
		if (a.radius + b.radius > dist(a, b) && dist(a, b) > PApplet.abs(a.radius - b.radius)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Finds the intersecting points between two overlapping circles
	 * 
	 * @param a
	 *            first circle
	 * @param b
	 *            second circle
	 * @return two vectors with x and y positions for each point. IMPORTANT: The
	 *         order of vectors depends of the order of circles. If no intersection
	 *         returns null
	 */
	public static PVector[] twoIntersectionPoints(VertexCircle a, VertexCircle b) {
		PVector[] rtn = new PVector[2];
		if (validateIntersection(a, b)) {
			float w = getW(a, b);
			float dist2 = PApplet.pow(dist(a, b), 2);

			float term1 = (a.orig.x + b.orig.x) / 2;
			float term2 = ((b.orig.x - a.orig.x) * (PApplet.pow(a.radius, 2) - PApplet.pow(b.radius, 2))) / (2 * dist2);
			float term3 = 2 * ((a.orig.y - b.orig.y) / dist2) * w;

			float x0 = term1 + term2 + term3;
			float x1 = term1 + term2 - term3;

			term1 = (a.orig.y + b.orig.y) / 2;
			term2 = ((b.orig.y - a.orig.y) * (PApplet.pow(a.radius, 2) - PApplet.pow(b.radius, 2))) / (2 * dist2);
			term3 = 2 * ((a.orig.x - b.orig.x) / dist2) * w;

			float y0 = term1 + term2 - term3;
			float y1 = term1 + term2 + term3;

			// For circle a
			rtn[0] = new PVector(x0, y0);
			rtn[1] = new PVector(x1, y1);

		}
		return rtn;
	}

	/**
	 * Finds the middle point between two centers
	 * 
	 * @param a
	 *            first circle
	 * @param b
	 *            second circle
	 * @return x and y of middle point
	 */
	public static PVector middlePoint(VertexCircle a, VertexCircle b) {
		float midX = (a.orig.x + b.orig.x) / 2;
		float midY = (a.orig.y + b.orig.y) / 2;
		return new PVector(midX, midY);
	}

	public static float dist(VertexCircle a, VertexCircle b) {
		return PApplet.dist(a.orig.x, a.orig.y, b.orig.x, b.orig.y);
	}

	/**
	 * W is the area of the triangle formed by the two circle centers and one of the
	 * intersection point. Calculated with Heron's formula
	 * https://en.wikipedia.org/wiki/Heron%27s_formula
	 */
	private static float getW(VertexCircle a, VertexCircle b) {
		float term1 = dist(a, b) + a.radius + b.radius;
		float term2 = dist(a, b) + a.radius - b.radius;
		float term3 = dist(a, b) - a.radius + b.radius;
		float term4 = -dist(a, b) + a.radius + b.radius;
		return (1f / 4f) * PApplet.sqrt(term1 * term2 * term3 * term4);
	}

	/**
	 * Returns the start and end angle in radians described by the intersecting
	 * points and the center of the first circle
	 * 
	 * @param a
	 *            first circle
	 * @param b
	 *            second circle
	 * @return angle in radians
	 */
	public static float[] startEndAngleIntersection(VertexCircle a, PVector startPoint, PVector endPoint) {
		float[] rtn = new float[2]; // 0 start, 1 end

		try {
			// Start
			rtn[0] = PApplet.atan2(endPoint.y - a.orig.y, endPoint.x - a.orig.x);

			// End angleBetweenCenters(a, b) -
			rtn[1] = PApplet.atan2(startPoint.y - a.orig.y, startPoint.x - a.orig.x);

			if (rtn[0] <= 0)
				rtn[0] += PApplet.TWO_PI;

			if (rtn[1] <= 0)
				rtn[1] += PApplet.TWO_PI;
			// System.out.println(a.id + " start: " + rtn[0] + " end: " + rtn[1]);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtn;
	}

	/**
	 * Gets the chord between to intersection circles Implementation of
	 * http://mathworld.wolfram.com/Circle-CircleIntersection.html
	 * 
	 * @param a
	 *            first circle
	 * @param b
	 *            second circle
	 * @return length of chord
	 */
	public static float findIntersectionChord(VertexCircle a, VertexCircle b) {

		if (validateIntersection(a, b)) {
			float gapD = PApplet.sqrt(PApplet.pow(a.orig.x - b.orig.x, 2) + PApplet.pow(a.orig.y - b.orig.y, 2));
			float term1 = -gapD + b.radius - a.radius;
			float term2 = -gapD - b.radius + a.radius;
			float term3 = -gapD + b.radius + a.radius;
			float term4 = gapD + b.radius + a.radius;
			return (1 / gapD) * PApplet.sqrt(term1 * term2 * term3 * term4);
		} else
			return 0;
	}

	public static float angleBetweenCenters(VertexCircle a, VertexCircle b) {
		return PApplet.atan2(b.orig.y - a.orig.y, b.orig.x - a.orig.x);
	}

	/**
	 * Used to determine the midpoint between to intersecting circles. This midpoint
	 * is used to set the relative angles of nodes to this centroid.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static PVector getMidIntersection(VertexCircle A, VertexCircle B) {
		// find the distance to midpoint
		float d = A.radius - ((B.radius + A.radius) - PApplet.dist(A.orig.x, A.orig.y, B.orig.x, B.orig.y)) / 2;

		// find the angle between centers
		float angle = PApplet.atan2(A.orig.y - B.orig.y, A.orig.x - B.orig.x);

		// find the coordinates of midpoint on the direction of the angle
		float x = A.orig.x - PApplet.cos(angle) * d;
		float y = A.orig.y - PApplet.sin(angle) * d;

		// return the coordinates
		return new PVector(x, y);
	}
}
