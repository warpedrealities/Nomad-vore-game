package solarBackdrop.decorations;

import org.lwjgl.util.vector.Matrix4f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import rendering.Sprite;
import shared.ParserHelper;
import shared.Vec2f;
import solarBackdrop.BackdropStar;
import solarBackdrop.StarScape;

public class DecorationLayer extends StarScape {


	public DecorationLayer(String systemName) {
		super("assets/art/solar/decorations/");
		viewScale=4;
		Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().equals("decorations"))
				{
					loadDecorations(Enode);
					
					break;
				}
			}
		}
		FAKE_PARALLAX=0.90F;
		setMatrix();
	}
	
	@Override
	public void setScale(float scale) {
		viewScale=scale;
		setMatrix();
	}

	@Override
	protected void setMatrix() {
		Matrix4f.setIdentity(backdropMatrix);
		backdropMatrix.m00 = 0.025F * viewScale;
		backdropMatrix.m11 = 0.03125F * viewScale;
		backdropMatrix.m22 = 1.0F;
		backdropMatrix.m33 = 1.0F;
		float xscale = 1 / (0.025F * viewScale);
		float yscale = 1 / (0.03125F * viewScale);
		backdropMatrix.m30 = ((currentPosition.x * -1*FAKE_PARALLAX) / xscale) - 0.32F;
		backdropMatrix.m31 = ((currentPosition.y * -1*FAKE_PARALLAX) / yscale) + 0.125F;

	}
	
	private void loadDecorations(Element enode)
	{
		NodeList children=enode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().equals("decoration"))
				{
					Vec2f p=new Vec2f(Integer.parseInt(Enode.getAttribute("x"))+0.0F,Float.parseFloat(Enode.getAttribute("y"))+0.0F);
					Sprite sprite = new Sprite(p,Float.parseFloat(Enode.getAttribute("size")),1);
					int v = Universe.m_random.nextInt(4);
					sprite.setVisible(true);
					sprite.setImage(v);
					addSprite(sprite, Enode.getAttribute("sprite")+".png");
				}
			}
		}
		
	}

	public float getScale() {
		return viewScale;
	}

}
