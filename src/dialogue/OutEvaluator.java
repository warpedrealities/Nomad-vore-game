package dialogue;

import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.NPC;
import actor.Player;
import actorRPG.Player_RPG;
import actorRPG.RPG_Helper;
import nomad.Universe;
import perks.PerkInstance;
import perks.PerkQualifier;
import view.SceneController;
import view.ViewScene;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetConversation;

public class OutEvaluator {

	NPC m_npc;
	Widget widget;
	Player m_player;
	PerkQualifier qualifier;
	SceneController controller;
	public OutEvaluator(NPC npc, Player player,SceneController controller)
	{
		m_npc=npc;
		m_player=player;
		this.controller=controller;
		qualifier=new PerkQualifier();

	}
	
	
	boolean Assertion(Element E)
	{
		String eval=E.getAttribute("evaluate");
		if (eval.equals("slothasitem"))
		{
			int slot=Integer.parseInt(E.getAttribute("slot"));
			if (m_player.getInventory().getSlot(slot)==null)
			{
				return false;
			}

		}	
		if (eval.equals("companionSlotFree"))
		{
			if (m_player.isFreeCompanion())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		if (eval.equals("isFaction"))
		{
			if (m_npc.getActorFaction().getFilename().equals(E.getAttribute("faction")))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		if (eval.equals("isCompanion"))
		{
			if (m_npc.isCompanion())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		if (eval.equals("hasperk"))
		{
			PerkInstance perk=((Player_RPG)m_player.getRPG()).getPerkInstance(E.getAttribute("perk"));
			if (perk==null)
			{
				return false;
			}
			if (!qualifier.perkUseable(perk.getPerk()))
			{
				return false;
			}
		}
		if (eval.equals("haspart"))
		{
			if (m_player.getLook().getPart(E.getAttribute("part"))==null)
			{
				return false;
			}
		}
		if (eval.equals("hasEntry"))
		{
			if (!m_player.getEncyclopedia().hasEntry(E.getAttribute("entry")))
			{
				return false;
			}
		}
		if (eval.equals("hasResearch"))
		{
			if (m_player.getEncyclopedia().getResearchList().get(E.getAttribute("research"))==null)
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean Evalthiscondition(Element E) throws MalformedDialogException
	{
		
		if (E.getTagName().equals("preference"))
		{
			if (Universe.getInstance().getPrefs().ForbiddenPref(E.getAttribute("fetish")))
			{
				return false;
			}
		}
		if (E.getTagName().equals("notpreference"))
		{
			if (!Universe.getInstance().getPrefs().ForbiddenPref(E.getAttribute("fetish")))
			{
				return false;
			}
		}
			
		if (E.getTagName().equals("condition"))
		{
			String eval=E.getAttribute("evaluate");
	

			if (eval.equals("DICEROLL"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				int dice=GameManager.m_random.nextInt(20);
				if (ConditionCheck(value,operator,dice)==false)
				{
					return false;
				}		
			}
			if (eval.equals("VIOLATION"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				int violation=controller.getViolationLevel();
				if (ConditionCheck(value,operator,violation)==false)
				{
					return false;
				}		
			}
			if (eval.equals("SCRIPT"))
			{
				String src = E.getAttribute("src");
				if (src != null && !src.isEmpty())
				{
					// TODO refactor this and Script_AI into generic script objects
					Globals globals = JsePlatform.standardGlobals();
					boolean evaluatedScriptValue = false;
					try
					{
						LuaValue script = globals.load(new FileReader("assets/data/conversations/" + src + ".lua"), "main.lua");
						script.call();
						LuaValue npc = CoerceJavaToLua.coerce(m_npc);
						LuaValue player = CoerceJavaToLua.coerce(m_player);
						LuaValue mainFunc = globals.get("main");
						LuaValue returnVal = mainFunc.call(npc, player);
						evaluatedScriptValue = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
					}
					catch (Exception e)
					{  
				        e.printStackTrace();  
					}
					
					return evaluatedScriptValue;
				}
				else {
					throw new MalformedDialogException("conditional element with SCRIPT evaluate attribute must provide src");
				}
			}
			if (eval.equals("FACTIONSTANDING"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				if (ConditionCheck(value,operator,
						m_npc.getActorFaction().getRelationship("player"))==false)
				{
					return false;
				}			
			}
			if (eval.equals("BODYVALUE"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				if (ConditionCheck(value,operator,
						m_player.getLook().getPart(E.getAttribute("bodypart")).getValue(E.getAttribute("partvalue")))==false)
				{
					return false;
				}	
			}
			if (eval.equals("LOCALFLAG"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));	
				if (m_npc!=null)
				{
					if (ConditionCheck(value,operator,m_npc.getFlags().readFlag(E.getAttribute("flag")))==false)
					{
						return false;
					}			
				}
				else if (widget!=null)
				{
					if (ConditionCheck(value,operator,((WidgetConversation)widget).getFlags().readFlag(E.getAttribute("flag")))==false)
					{
						return false;
					}	
				}
			}
			if (eval.equals("GLOBALFLAG"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				if (ConditionCheck(value,operator,m_player.getFlags().readFlag(E.getAttribute("flag")))==false)
				{
					return false;
				}	
			}
			if (eval.equals("FACTIONFLAG"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				if (ConditionCheck(value,operator,m_npc.getActorFaction().getFactionFlags().readFlag(E.getAttribute("flag")))==false)
				{
					return false;
				}	
			}
			if (eval.equals("PLAYERATTRIBUTE"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				int attribute=RPG_Helper.AttributefromString(E.getAttribute("attribute"));
				if (ConditionCheck(value,operator,m_player.getRPG().getAttribute(attribute))==false)
				{
					return false;
				}	
			}
			if (eval.equals("HASITEM"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				String item=E.getAttribute("item");
				if (ConditionCheck(value,operator,m_player.getInventory().countItem(item))==false)
				{
					return false;
				}	
			}
			if (eval.equals("GOLD"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				if (ConditionCheck(value,operator,m_player.getInventory().getPlayerGold())==false)
				{
					return false;
				}	
			}
			if (eval.equals("TIMEPASSED"))
			{
				String operator=E.getAttribute("operator");
				int value=Integer.parseInt(E.getAttribute("value"));
				int comp=(int) ((Universe.getClock()/100)-m_npc.getFlags().readFlag("CLOCK"));
				if (comp>0 && ConditionCheck(value,operator,comp)==false)
				{
					return false;
				}
			}
		}
		
		
		
		if (E.getTagName().equals("assertion"))
		{
			return Assertion(E);
		}	
		if (E.getTagName().equals("assertnot"))
		{
			return !Assertion(E);
		}	
		
		return true;
	}
	
	 public boolean EvalConditional(Element node) throws MalformedDialogException
	{
		NodeList nodes=node.getChildNodes();
		for (int i=0;i<nodes.getLength();i++)
		{	
			if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element E=(Element)nodes.item(i);		
				if (Evalthiscondition(E)==false)
				{
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public static boolean ConditionCheck(int value, String operator, int eval)
	{
		if (operator.equals("lessthan"))
		{
			if (eval<value)
			{
				return true;
			}
		}
		if (operator.equals("greaterthan"))
		{
			if (eval>=value)
			{
				return true;
			}		
		}
		if (operator.equals("equals"))
		{
			if (eval==value)
			{
				return true;
			}
		}
		return false;
	}


	public void setWidget(Widget widget) {
		this.widget = widget;
	}
	
	
}
