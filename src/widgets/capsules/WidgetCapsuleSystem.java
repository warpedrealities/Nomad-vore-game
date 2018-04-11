package widgets.capsules;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import nomad.universe.Universe;
import shared.ParserHelper;
import spaceship.Spaceship;
import view.ViewScene;
import widgets.WidgetBreakable;

public class WidgetCapsuleSystem extends WidgetBreakable {

	private int deployedSprite,readySprite;
	private float fuelUse;
	private boolean deployed;
	
	public WidgetCapsuleSystem(Element element)
	{
		super(element);
		deployed=false;
		readySprite=this.widgetSpriteNumber;
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "capsuleMechanics") {
					deployedSprite=Integer.parseInt(Enode.getAttribute("sprite"));
					fuelUse=Float.parseFloat(Enode.getAttribute("fuelUse"));
				}
			}
		}
		
	}
	
	public WidgetCapsuleSystem(DataInputStream dstream) throws IOException {
		deployed=dstream.readBoolean();
		deployedSprite=dstream.readInt();
		readySprite=dstream.readInt();
		fuelUse=dstream.readFloat();
		commonLoad(dstream);
		load(dstream);
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(25);
		dstream.writeBoolean(deployed);
		dstream.writeInt(deployedSprite);
		dstream.writeInt(readySprite);
		dstream.writeFloat(fuelUse);
		commonSave(dstream);
		saveBreakable(dstream);
	}

	public boolean Interact(Player player) {
		CapsuleBehaviour behaviour=new CapsuleBehaviour(this,(Spaceship)Universe.getInstance().getCurrentEntity());
		if (!deployed)
		{
	
			if (behaviour.sufficientFuel())
			{
				if (behaviour.canLaunch())
				{
					return behaviour.landing();
				}
				return true;
			}
			else
			{
				behaviour.finish();
				return false;
			}
		}
		else
		{
			ViewScene.m_interface.DrawText("capsule already deployed");
			return false;
		}
	}

	public float getFuelCost() {
		return fuelUse;
	}

	public void setDeployed(boolean deployed) {

		this.deployed=deployed;
		if (deployed)
		{
			this.widgetSpriteNumber=deployedSprite;
		}
		else
		{
			this.widgetSpriteNumber=readySprite;
		}
	}

	public boolean isDeployed() {
		return deployed;
	}
	
}
