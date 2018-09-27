package font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import shared.Vec2f;

public class GlyphLoader {

	private Map<Integer, Glyph> map;

	public void buildMap(String filename) {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader eventReader = factory.createXMLStreamReader(new FileInputStream(filename));
			map = new HashMap<Integer, Glyph>();
			findGlyphs(eventReader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void findGlyphs(XMLStreamReader xml) throws XMLStreamException {

		while (xml.hasNext()) {
			int event = xml.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:

				String qName = xml.getLocalName();
				if (qName.equalsIgnoreCase("chars")) {
					buildGlyphs(xml);
					break;
				}
				break;
			}

		}
	}

	private void buildGlyphs(XMLStreamReader xml) throws XMLStreamException {
		while (xml.hasNext()) {
			int event = xml.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:

				String qName = xml.getLocalName();
				if (qName.equalsIgnoreCase("char")) {
					makeGlyph(xml);
				}

			}

		}

	}

	private void makeGlyph(XMLStreamReader xml) throws XMLStreamException {
		// TODO Auto-generated method stub
		int value = 0;
		float x = 0, y = 0, width = 0, height = 0, offsetx = 0, offsety = 0, advance = 0;
		for (int i = 0; i < xml.getAttributeCount(); i++) {
			if (xml.getAttributeLocalName(i).equals("id")) {
				value = Integer.parseInt(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("x")) {
				x = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("y")) {
				y = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("width")) {
				width = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("height")) {
				height = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("xoffet")) {
				offsetx = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("yoffset")) {
				offsety = Float.parseFloat(xml.getAttributeValue(i));
			}
			if (xml.getAttributeLocalName(i).equals("xadvance")) {
				advance = Float.parseFloat(xml.getAttributeValue(i));
			}
		}
		// build glyph and add

		Glyph glyph = new Glyph(new Vec2f(x / 256, y / 256), new Vec2f(width / 256, height / 256),
				new Vec2f(offsetx / 32, offsety / 32), advance / 32);

		map.put(value, glyph);
	}

	public Map<Integer, Glyph> getGlyphs() {
		return map;
	}

}
