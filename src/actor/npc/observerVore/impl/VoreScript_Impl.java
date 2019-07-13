package actor.npc.observerVore.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import actor.npc.observerVore.VoreScript;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import view.ViewScene;
import zone.Zone;

public class VoreScript_Impl implements VoreScript {

	private ScriptStage []stages;
	private String luaScript, filename;
	private int stageIndex;
	private boolean alive;
	private NPC target,origin;
	private int lastDamaged, uid;
	private boolean blocksConversation = true;


	public VoreScript_Impl(String filename,NPC target, NPC origin)
	{
		this.filename = filename;
		genStages(filename);
		stageIndex=0;
		if (this.stages[0] == null) {
			stageIndex = 1;
		}
		alive=true;
		this.target=target;
		this.origin=origin;
		target.setBusy(true);
	}


	public VoreScript_Impl(DataInputStream dstream, NPC origin) throws IOException {
		this.origin = origin;
		this.filename = ParserHelper.LoadString(dstream);
		genStages(filename);
		alive = true;
		stageIndex = dstream.readInt();
		this.stages[stageIndex].load(dstream);
		this.uid = dstream.readInt();
	}


	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		ParserHelper.SaveString(dstream, filename);
		dstream.writeInt(stageIndex);
		this.stages[stageIndex].save(dstream);
		dstream.writeInt(this.target.getUID());
	}
	private void genStages(String filename)
	{
		Document doc=ParserHelper.LoadXML("assets/data/observer Vore/"+filename+".xml");
		Element n=(Element)doc.getFirstChild();
		blocksConversation = true;


		if (n.getAttribute("blocksConversation").equals("false")) {
			blocksConversation = false;
		}
		NodeList children=n.getChildNodes();
		stages=new ScriptStage[3];
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName()=="insertion")
				{
					stages[0]=new InsertionStage(Enode);
				}
				if (Enode.getTagName()=="digestion")
				{
					stages[1]=new DigestionStage(Enode);
				}
				if (Enode.getTagName()=="finish")
				{
					stages[2]=new FinishStage(Enode);
				}
				if (Enode.getTagName().equals("luaScript"))
				{
					luaScript=Enode.getAttribute("file");
				}
			}
		}
	}

	@Override
	public boolean blocksAI() {
		return stages[stageIndex].blocksAI();
	}

	@Override
	public void update(boolean visible,boolean noEnemies) {
		int progress=stages[stageIndex].update(visible, noEnemies,lastDamaged);
		lastDamaged++;
		if (progress==1)
		{
			if (stages[stageIndex].removeTarget())
			{
				target.setBusy(false);
				target.Remove(false,true);
			}
			stageIndex++;
			if (stageIndex>=stages.length ||stages[stageIndex]==null)
			{
				alive=false;
				if (luaScript!=null) {
					runLuaScript();
				}
			}
		}
	}

	private void runLuaScript() {
		// TODO Auto-generated method stub
		Globals globals;
		LuaValue script = null;
		globals = JsePlatform.standardGlobals();
		try {
			script = globals.load(new FileReader("assets/data/observer Vore/scripts/" + luaScript + ".lua"), "main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LuaError e) {
			System.err.println("script name"+luaScript);
			e.printStackTrace();
		}
		try {
			script.call();

			LuaValue luacontrollable = CoerceJavaToLua.coerce(origin);
			LuaValue luaView = CoerceJavaToLua.coerce(ViewScene.m_interface);
			LuaValue luacontrol = globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luacontrollable, luaView);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			System.err.println("script name"+luaScript);
			e.printStackTrace();
		}
	}

	@Override
	public void attacked() {
		lastDamaged=0;
		if (stages[stageIndex].isInterruptible())
		{
			target.setBusy(false);
			alive=false;
		}
	}

	@Override
	public boolean isActive() {
		return alive;
	}

	@Override
	public boolean blocksConversation() {
		return blocksConversation;
	}

	@Override
	public void linkActors(Zone zone) {
		for (int i = 0; i < zone.getActors().size(); i++) {
			if (zone.getActors().get(i).getUID() == uid && NPC.class.isInstance(zone.getActors().get(i))) {
				this.target = (NPC) zone.getActors().get(i);
				break;
			}
		}
	}

	@Override
	public NPC getTarget() {
		return target;
	}

	@Override
	public void releasePrey() {
		Vec2f p = Universe.getInstance().getCurrentZone().getEmptyTileNearP(origin.getPosition());
		target.Heal();
		target.setPosition(p);

	}

}
