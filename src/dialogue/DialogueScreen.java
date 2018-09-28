package dialogue;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import actor.player.Player;
import actorRPG.RPG_Helper;
import description.MacroLibrary;
import dialogue.choiceHandler.ChoiceHandler;
import dialogue.choiceHandler.ChoiceLoader;
import dialogue.choiceHandler.ChoiceText;
import dialogue.effectProcessing.EffectProcessor;
import dialogue.evaluation.OutEvaluator;
import dialogue.random.Randomizer_Library;
import gui.Image;
import gui.TextView;
import gui.Window;
import input.Keyboard;
import input.MouseHook;
import nomad.GameOver;
import nomad.universe.Universe;
import shared.Callback;
import shared.ParserHelper;
import shared.SceneBase;
import shared.Scene_Int;
import shared.Screen;
import shared.Vec2f;
import spaceship.Spaceship;
import view.ViewScene;
import vmo.Game;
import vmo.GameManager;
import widgets.Widget;

public class DialogueScreen extends Screen implements Callback {

	NPC talkingNpc;
	Player m_player;
	Element m_root;
	NodeList m_children;
	TextView m_text;
	Window m_window;
	Image portraitImage;

	ChoiceHandler choiceHandler;
	ChoiceLoader choiceLoader;

	int m_numchoices;
	String m_choices[];
	String m_Debug;
	OutEvaluator m_evaluator;
	EffectProcessor m_processor;
	Callback callback;
	float m_stagnation;

	
	static public final int DISPOSITION = 0;

	public DialogueScreen(int frame, int button, int buttonalt, int tint, Player player, TextView textview,
			Callback callback) {
		this.callback=callback;
		m_player = player;
		m_text = textview;
		m_window = new Window(new Vec2f(3, -1), new Vec2f(17, 17), frame, true);
		m_choices = new String[16];

		portraitImage = new Image(new Vec2f(-20, -1), new Vec2f(23, 17), "assets/art/portraits/test.png");
		portraitImage.setVisible(false);

		m_stagnation = 0.5F;

		choiceLoader = new ChoiceLoader();
		choiceHandler = new ChoiceHandler(new Vec2f(4, 15.5F), tint, this);
	}

	public NPC getTalkingNpc() {
		return talkingNpc;
	}

	public void setTalkingNpc(NPC npc) {
		this.talkingNpc = npc;
	}

	public boolean Load(String Conversation, NPC npc,Scene_Int scene) {
		Document doc = ParserHelper.LoadXML("assets/data/conversations/" + Conversation + ".xml");
		Element n = (Element) doc.getFirstChild();
		m_root = (Element) n;
		m_children = m_root.getChildNodes();
		
		if (ViewScene.m_interface!=null)
		{
			m_evaluator = new OutEvaluator(npc, m_player, ViewScene.m_interface.getSceneController());		
			m_processor = new EffectProcessor(m_player, ViewScene.m_interface.getSceneController(),scene);
		}
		else
		{
			m_evaluator = new OutEvaluator(npc, m_player, null);		
			m_processor = new EffectProcessor(m_player,null,scene);		
		}
		
		m_processor.setNPC(npc);
		if (npc != null) {
			try {
				return FindNode("start");
			} catch (MalformedDialogException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

	
	
	public void setWidget(Widget widget) {
		m_processor.setWidget(widget);
		m_evaluator.setWidget(widget);
		if (talkingNpc == null) {
			try {
				FindNode("start");
			} catch (MalformedDialogException e) {
				e.printStackTrace();

			}
		}
	}

	public void setSpaceship(Spaceship ship)
	{
		m_evaluator.setSpaceship(ship);
		m_processor.setSpaceship(ship);
		if (talkingNpc==null)
		{
			try {
				FindNode("start");
			} catch (MalformedDialogException e) {
				e.printStackTrace();

			}
		}
	}
	
	@Override
	public void start(MouseHook hook) {

		hook.Register(m_window);
		hook.Register(choiceHandler);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {
		m_window.Draw(buffer, matrixloc);
		m_text.Draw(buffer, matrixloc);

		portraitImage.Draw(buffer, matrixloc);
		choiceHandler.Draw(buffer, matrixloc);
	}

	boolean FindNode(String id) throws MalformedDialogException {
		for (int i = 0; i < m_children.getLength(); i++) {
			if (m_children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element E = (Element) m_children.item(i);
				if (E.getAttribute("ID").equals(id) && m_evaluator.EvalConditional(E) == true) {
					ProcessNode(E);
					return true;
				}

			}
		}
		return false;
	}

	void ProcessNode(Element element) throws MalformedDialogException {
		if (element.getTagName().equals("page")) {
			ProcessPage(element);
		}
		if (element.getTagName().equals("check")) {
			ProcessCheck(element);
		}
		if (element.getTagName().equals("end")) {
			EndConversation();
		}
		if (element.getTagName().equals("gameover")) {
			GameOver(element);
		}
	}

	void EndConversation() {
		m_processor.endConversation();
	}

	void GameOver(Element node) throws MalformedDialogException {
		// find text child
		String str = null;

		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element E = (Element) nodes.item(i);
				if (E.getTagName().equals("text")) {
					str = ParseText(E);
					break;
				}
			}
		}
		boolean b=false;
		if ("true".equals(node.getAttribute("noReform")))
		{
			b=true;
		}
		Game.sceneManager.SwapScene(new GameOver(SceneBase.getVariables(), str, m_text.getDisplayStrings(),talkingNpc,b));
	}

	void ProcessPage(Element element) throws MalformedDialogException {
		NodeList nodes = element.getChildNodes();
		m_numchoices = 0;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element E = (Element) nodes.item(i);
				if (E.getTagName().equals("text")) {
					ProcessText(E);
				}
				if (E.getTagName().equals("choice")) {
					ProcessChoice(E);
				}
				if (E.getTagName().equals("effect")) {
					m_processor.ProcessEffect(E);
				}
				if (E.getTagName().equals("special")) {
					m_processor.ProcessSpecial(E);
				}
				if (E.getTagName().equals("picture")) {
					processPortrait(E);
				}

			}
		}
		choiceHandler.addChoices(choiceLoader.getChoices(), choiceLoader.getCount());
		choiceLoader.reset();

	}

	private void processPortrait(Element e) {
		// TODO Auto-generated method stub
		String str = e.getAttribute("source");
		if (str == null) {
			portraitImage.setVisible(false);
		} else {
			portraitImage.setVisible(true);
			portraitImage.setTexture("assets/art/portraits/"+str+".png");
		}

	}

	void ProcessCheck(Element node) throws MalformedDialogException {
		boolean fail = false;
		String destinations[] = new String[2];
		int index = 0;
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element E = (Element) nodes.item(i);
				if (E.getTagName().equals("test")) {
					int what = RPG_Helper.AttributefromString(E.getAttribute("what"));
					int dc = Integer.parseInt(E.getAttribute("DC"));
					if (Test(what, dc) == false) {
						fail = true;
					}
				}
				if (E.getTagName().equals("outcome")) {
					destinations[index] = E.getAttribute("destination");
					index++;
				}

			}
		}
		if (fail == true) {
			FindNode(destinations[1]);
		} else {
			FindNode(destinations[0]);
		}
	}

	boolean Test(int attribute, int DC) {
		int r = GameManager.m_random.nextInt(20);
		r = r + Universe.getInstance().getPlayer().getRPG().getAttribute(attribute);
		if (r > DC) {
			return true;
		}
		return false;
	}

	private String additionalTextProcessing(String text) {
		if (text.contains("PNAME")) {
			text = text.replace("PNAME", m_player.getName());
		}

		return text;
	}

	void ProcessChoice(Element node) throws MalformedDialogException {
		if (m_evaluator.EvalConditional(node) == true) {
			String text = node.getAttribute("text");

			choiceLoader.addString(additionalTextProcessing(text));
			m_choices[m_numchoices] = node.getAttribute("destination");
			m_numchoices++;
		}
	}

	String ParseText(Element node) throws MalformedDialogException {
		StringBuffer buffer = new StringBuffer();
		Node n = node.getFirstChild();
		while (n != null) {
			// write to buffer
			if (n.getNodeType() == Node.TEXT_NODE) {
				buffer.append(n.getNodeValue().replace("\n", ""));
			}
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) n;
				if (Enode.getTagName().equals("conditional")) {
					// evaluate conditional
					if (m_evaluator.EvalConditional(Enode)) {
						// if so use another parse text
						buffer.append(ParseText(Enode));

					}
				}
				if (Enode.getTagName().equals("readequipment")) {

					int slotnum = Integer.parseInt(Enode.getAttribute("slot"));
					// read player slot
					if (m_player.getInventory().getSlot(slotnum) != null) {
						String str = m_player.getInventory().getSlot(slotnum).getName();
						buffer.append(str);
						// write contents of that slot
					}
				}
				if (Enode.getTagName().equals("macro")) {
					String s = Enode.getAttribute("id");
					if (s.length() <= 0) {
						s = Enode.getAttribute("ID");
					}
					buffer.append(MacroLibrary.getInstance().lookupMacro(m_player.getLook(), s));
				}
				if (Enode.getTagName().equals("random")) {
					String s = Enode.getAttribute("id");
					if (s.length() <= 0) {
						s = Enode.getAttribute("ID");
					}
					buffer.append(Randomizer_Library.getInstance().getRandomizedText(s));
				}
				if (Enode.getTagName().equals("measurement")) {
					String part = Enode.getAttribute("part");
					String variable = Enode.getAttribute("variable");
					buffer.append(m_player.getLook().getPart(part).getValue(variable));
				}
				if (Enode.getTagName().equals("result")) {
					int result=m_processor.getResult();
					buffer.append(Integer.toString(result));
				}
			}
			n = n.getNextSibling();
		}
		return buffer.toString();
	}

	void ProcessText(Element node) throws MalformedDialogException {
		String str = ParseText(node);
		m_Debug = str;
		m_text.AddText(additionalTextProcessing(str));
		m_text.BuildStrings();
	}

	@Override
	public void discard(MouseHook mouse) {
		portraitImage.discard();
		m_window.discard();
		mouse.Remove(m_window);
		mouse.Remove(choiceHandler);
	}

	private void echoChoice(ChoiceText choice) {
		switch (choice.getDisplayType()) {
		case NONE:

			break;
		case SELF:
			m_text.AddText(m_player.getName() + ":" + choice.getText());
			break;

		case NARRATOR:
			m_text.AddText(":" + choice.getText());
			break;

		case NPC:
			m_text.AddText(talkingNpc.getName() + ":" + choice.getText());
			break;

		}
		m_text.BuildStrings();
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		if (m_stagnation == 0) {
			if (ID < m_numchoices) {
				echoChoice(choiceHandler.getchoice(ID));

				m_text.BuildStrings();
				if (m_choices[ID].equals("end")) {
					EndConversation();
				} else {
					try {
						FindNode(m_choices[ID]);
					} catch (MalformedDialogException e) {
						e.printStackTrace();
					}

					m_stagnation = 1;
				}
			}
		}

	}

	@Override
	public void update(float DT) {
		m_window.update(DT);
		if (m_stagnation > 0) {
			m_stagnation -= 0.1F;
		}
		if (m_stagnation < 0) {
			m_stagnation = 0;
		}
		if (m_stagnation == 0) {
			keyboardControls();
		}
		m_text.update(DT);
		choiceHandler.update(DT);
	}

	private void keyboardControls() {
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_1)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_1)) {
			ButtonCallback(0, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_2)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_2)) {
			ButtonCallback(1, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_3)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_3)) {
			ButtonCallback(2, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_4)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_4)) {
			ButtonCallback(3, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_5)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_5)) {
			ButtonCallback(4, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_6)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_6)) {
			ButtonCallback(5, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_7)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_7)) {
			ButtonCallback(6, null);
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_8)||Keyboard.isKeyDown(GLFW.GLFW_KEY_KP_8)) {
			ButtonCallback(7, null);
		}
	}

	@Override
	public void Callback() {

		ButtonCallback(choiceHandler.getIndex(), null);
	}
}
