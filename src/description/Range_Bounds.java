package description;

import org.w3c.dom.Element;

public class Range_Bounds {

	private int lowerBound;
	private int upperBound;
	private String string;

	public Range_Bounds(Element node) {
		lowerBound = Integer.parseInt(node.getAttribute("lowerbound"));
		upperBound = Integer.parseInt(node.getAttribute("upperbound"));
		string = node.getAttribute("string");
	}

	public String getString() {
		return string;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public boolean inBounds(int value) {
		if (value >= lowerBound && value < upperBound) {
			return true;
		}
		return false;
	}
}
