package worldgentools.blockdungeon;

import org.w3c.dom.Element;

import shared.Vec2i;

public class SpawnDefinition {
	Vec2i position;
	int tag;
	
	public SpawnDefinition(Element element)
	{
		position=new Vec2i(Integer.parseInt(element.getAttribute("x")),Integer.parseInt(element.getAttribute("y")));
		
		if (element.getAttribute("tag").length()>0)
		{
			tag=Integer.parseInt(element.getAttribute("tag"));
		}
	}
	
	
	public Vec2i getPosition() {
		return position;
	}
	public int getTag() {
		return tag;
	}

	
}
