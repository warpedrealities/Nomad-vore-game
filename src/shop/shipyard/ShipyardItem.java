package shop.shipyard;

import org.w3c.dom.Element;

public class ShipyardItem {

	private String file, image, description;
	private int cost, imageWidth, imageHeight;

	public ShipyardItem(Element enode) {
		file = enode.getAttribute("file");
		description = enode.getElementsByTagName("description").item(0).getTextContent();
		cost = Integer.parseInt(enode.getAttribute("cost"));
		Element e = (Element) enode.getElementsByTagName("image").item(0);
		image = e.getAttribute("art");
		imageWidth = Integer.parseInt(e.getAttribute("width"));
		imageHeight = Integer.parseInt(e.getAttribute("height"));
	}

	public String getFile() {
		return file;
	}

	public String getImage() {
		return image;
	}

	public String getDescription() {
		return description;
	}

	public int getCost() {
		return cost;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

}
