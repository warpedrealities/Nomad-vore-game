package worldgentools.blockdungeon;

import org.w3c.dom.Element;

import shared.Vec2i;

public class WidgetDefinition {

	Vec2i position;
	String widgetName;
	String widgetInfo;
	String widgetVariable;

	public WidgetDefinition(Element element) {
		position = new Vec2i(Integer.parseInt(element.getAttribute("x")), Integer.parseInt(element.getAttribute("y")));
		widgetName = element.getAttribute("name");
		if (element.getAttribute("info").length() > 0) {
			widgetInfo = element.getAttribute("info");
		}
		if (element.getAttribute("variable").length() > 0) {
			widgetVariable = element.getAttribute("variable");
		}

	}

	public String getWidgetName() {
		return widgetName;
	}

	public String getWidgetInfo() {
		return widgetInfo;
	}

	public String getWidgetVariable() {
		return widgetVariable;
	}

	public Vec2i getPosition() {
		return position;
	}

}
