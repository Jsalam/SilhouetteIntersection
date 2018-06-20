package utilities;

import java.util.ArrayList;

import processing.core.PApplet;
import shapes.Node;

public abstract class HoleSorter {

	public static ArrayList<Node> sortHole(ArrayList<Node> nodes) {
		ArrayList<Node> rtn = new ArrayList<Node>();
		ArrayList<Node> head = new ArrayList<Node>();
		ArrayList<Node> body = new ArrayList<Node>();
		ArrayList<Node> tail = new ArrayList<Node>();

		boolean fillingTail = true;
		boolean fillingBody = false;
		boolean fillingHead = false;

		String caseType = identifyCase(nodes);
		System.out.println(caseType);
		switch (caseType) {

		case "Tail":
			rtn = nodes;
			break;
		case "Head":
			for (Node n : nodes) {
				if (n.selected) {
					tail.add(n);
				} else {
					head.add(n);
				}
			}
			break;
		case "Head&Tail":
			for (Node n : nodes) {
				if (fillingTail) {
					if (n.selected) {
						tail.add(n);
					} else {
						fillingTail = false;
						fillingHead = true;
					}
				}
				if (fillingHead) {
					if (!n.selected) {
						head.add(n);
					} else {
						fillingHead = false;
						fillingBody = true;
					}
				}
				if (fillingBody) {
					if (n.selected) {
						body.add(n);
					}
				}
			}
			break;
		case "Body":
			fillingTail = false;
			fillingBody = true;
			fillingHead = false;
			for (Node n : nodes) {
				if (fillingBody) {
					if (!n.selected) {
						body.add(n);
					} else {
						fillingTail = true;
						fillingBody = false;
					}
				}
				if (fillingTail) {
					if (n.selected) {
						tail.add(n);
					} else {
						fillingHead = true;
						fillingTail = false;
					}
				}
				if (fillingHead) {
					if (!n.selected) {
						head.add(n);
					}
				}
			}
			break;
		}

		rtn.addAll(head);
		if (body.size() > 1)
			rtn.addAll(body);
		rtn.addAll(tail);

		return rtn;
	}

	private static String identifyCase(ArrayList<Node> nodes) {
		boolean[] sequence = new boolean[3];
		int[] seq = { -1, -1, -1 };
		sequence[0] = nodes.get(0).selected;
		if (sequence[0])
			seq[0] = 1;
		else
			seq[0] = 0;

		int count = 0;
		for (Node n : nodes) {
			if (n.selected != sequence[count]) {
				count++;
				sequence[count] = n.selected;
				if (sequence[count])
					seq[count] = 1;
				else
					seq[count] = 0;
			}
		}

		String rtn = null;
		// Case C1
		if (!sequence[0] && sequence[1] && seq[2] == -1)
			rtn = "Tail";

		// Case C2
		if (sequence[0] && !sequence[1] && seq[2] == -1)
			rtn = "Head";

		// Case C3
		if (sequence[0] && !sequence[1] && seq[2] == 1)
			rtn = "Head&Tail";

		// Case C4
		if (!sequence[0] && sequence[1] && seq[2] == 0)
			rtn = "Body";

		return rtn;
	}

	public static void printNodes(ArrayList<Node> nodes) {
		for (Node n : nodes) {
			System.out.println(n.id + " " + n.selected);
		}
	}

	/**
	 * This is used to create a dummy node lists to try the algorithms. The head
	 * and tail of the list are marked as 'selected'
	 * 
	 * @param headSize
	 *            the number of selected elements at the head
	 * @param tailSize
	 *            the number of selected elements at the tail
	 * @param listSize
	 *            the size of the list
	 * @return the list
	 */
	public ArrayList<Node> setHeadTail(int headSize, int tailSize, int listSize) {
		ArrayList<Node> rtn = new ArrayList<Node>();

		if (headSize + tailSize >= listSize)
			System.out.println("*** ALL NODES ARE SELECTED **** ");

		for (int i = 0; i < listSize; i++) {
			float x = PApplet.cos(i/listSize);
			float y = PApplet.sin(i/listSize);
			float angle = PApplet.atan2(y,x);
			Node tmp = new Node(x, y, angle); 
			tmp.setId(i);

			if (i < headSize)
				tmp.markNode();

			if (i >= listSize - tailSize)
				tmp.markNode();

			rtn.add(tmp);
		}
		return rtn;
	}

	/* The head and tail of the list are NOT selected */
	/**
	 * This is used to create a dummy node lists to try the algorithms. The head
	 * and tail of the list are NOT marked as 'selected', just elements from the middle.
	 * 
	 * @param headSize
	 *            the number of selected elements at the head
	 * @param tailSize
	 *            the number of selected elements at the tail
	 * @param listSize
	 *            the size of the list
	 * @return the list
	 */
	public ArrayList<Node> setBody(int start, int bodySize, int listSize) {
		ArrayList<Node> rtn = new ArrayList<Node>();

		if (bodySize <= 0)
			System.out.println("*** EMPTY BODY. NO NODE IS SELECTED **** ");

		for (int i = 0; i < listSize; i++) {
			float x = PApplet.cos(i/listSize);
			float y = PApplet.sin(i/listSize);
			float angle = PApplet.atan2(y,x);
			Node tmp = new Node(x, y, angle);
			tmp.setId(i);

			if (i >= start && i < start + bodySize)
				tmp.markNode();

			rtn.add(tmp);
		}
		return rtn;
	}

}
