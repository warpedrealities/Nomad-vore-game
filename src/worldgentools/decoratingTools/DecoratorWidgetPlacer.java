package worldgentools.decoratingTools;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import widgets.Widget;
import worldgentools.WidgetPlacer;
import zone.Zone;

public class DecoratorWidgetPlacer implements DecoratorPlacer {

	Element widget;
	WidgetPlacer widgetPlacer;

	public DecoratorWidgetPlacer(Element element) {
		widgetPlacer = new WidgetPlacer();

		widget = element;
		/*
		 * NodeList children=element.getChildNodes(); for (int
		 * i=0;i<children.getLength();i++) { if
		 * (children.item(i).getNodeType()==Node.ELEMENT_NODE) {
		 * widget=(Element)children.item(i); break; } }
		 */
	}

	@Override
	public void place(int x, int y, Zone zone) {
		Widget placeable = widgetPlacer.genWidget(widget);
		zone.getTile(x, y).setWidget(placeable);
	}

}
