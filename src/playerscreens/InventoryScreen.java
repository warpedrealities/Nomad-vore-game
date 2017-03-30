package playerscreens;

import font.NuFont;
import gui.Button;
import gui.Button2;
import gui.DropDown;
import gui.GUIBase;
import gui.List;
import gui.MultiLineText;
import gui.Text;
import gui.TextColoured;
import gui.UI_Popup;
import gui.Window;
import input.Keyboard;
import input.MouseHook;
import item.Item;
import item.ItemAmmo;
import item.ItemConsumable;
import item.ItemDepletableInstance;

import item.ItemEquip;
import item.ItemExpositionInstance;
import item.ItemHasEnergy;
import item.ItemStack;
import item.ItemWeapon;
import item.ItemWidget;
import item.Item.ItemUse;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;


import actor.Inventory;
import actor.Player;
import actorRPG.Player_RPG;

import shared.Callback;
import shared.LogWindow;
import shared.ParserHelper;
import shared.Rect;
import shared.SceneBase;
import shared.Screen;
import shared.Vec2f;
import spaceship.ShipHandler;
import view.ModelController_Int;
import view.ViewScene;

public class InventoryScreen extends Screen implements Callback {

	List m_list;
	Player m_player;
	Window m_window;
	Text m_slots[];
	
	int m_control;
	
	MultiLineText m_description;
	Rect m_rect[];
	MouseHook m_hook;
	ModelController_Int m_callback;
	 
	Text m_encumbrance;
	TextColoured encumbranceWarning;
	Text []currencyTexts;
	DropDown m_dropdown;
	String []m_dropdownstrings;
	Button m_button;
	
	Popup popup;
	UI_Popup popupBig;
	
	boolean screenAlive;
	
	public InventoryScreen(int frame, int button, int buttonalt,Player player, int tint,ModelController_Int callback)
	{
		screenAlive=true;
		m_callback=callback;
		m_control=1;
		m_list=new List(new Vec2f(3,-14.3F),16,frame,tint,this);
		m_player=player;
		String str[]=new String[m_player.getInventory().getNumItems()];
		for (int i=0;i<str.length;i++)
		{
			str[i]=m_player.getInventory().getItem(i).getName()+" "+m_player.getInventory().getItem(i).getWeight();
		}
		m_list.GenList(str);
		
		m_window=new Window(new Vec2f(-20,-16),new Vec2f(23,15),frame,true);
	
		//build text left of window for slot names
		Text fonts[]=new Text[5];
		
		m_slots=new Text[5];
	
		m_rect=new Rect[5];
		
		fonts[0]=new Text(new Vec2f(0.2F,7.0F),"HAND",0.6F,0);
		fonts[1]=new Text(new Vec2f(0.2F,6.2F),"ACCESSORY",0.6F,0);		
		fonts[2]=new Text(new Vec2f(0.2F,5.4F),"BODY",0.6F,0);
		fonts[3]=new Text(new Vec2f(0.2F,4.6F),"HEAD",0.6F,0);
		fonts[4]=new Text(new Vec2f(0.2F,3.8F),"QUICK",0.6F,0);
		
		m_slots[0]=new Text(new Vec2f(3.7F,7.0F),"",0.6F,tint);	
		m_slots[1]=new Text(new Vec2f(3.7F,6.2F),"",0.6F,tint);	
		m_slots[2]=new Text(new Vec2f(3.7F,5.4F),"",0.6F,tint);	
		m_slots[3]=new Text(new Vec2f(3.7F,4.6F),"",0.6F,tint);		
		m_slots[4]=new Text(new Vec2f(3.7F,3.8F),"",0.6F,tint);		
				
		m_rect[0]=new Rect(new Vec2f(14.7F-28,15.5F-18),new Vec2f(8,1.0F));
		m_rect[1]=new Rect(new Vec2f(14.7F-28,14.0F-18),new Vec2f(8,1.0F));
		m_rect[2]=new Rect(new Vec2f(14.7F-28,12.3F-18),new Vec2f(8,1.0F));
		m_rect[3]=new Rect(new Vec2f(14.7F-28,10.9F-18),new Vec2f(8,1.0F));
		m_rect[4]=new Rect(new Vec2f(14.7F-28,9.2F-18),new Vec2f(8,1.0F));	
		
		for (int i=0;i<5;i++)
		{

			if (m_player.getInventory().getSlot(i)!=null)
			{
				m_slots[i].setString(m_player.getInventory().getSlot(i).getName());	
			}
			else
			{
				m_slots[i].setString("none");
			}

			m_window.add(fonts[i]);
			m_window.add(m_slots[i]);
		}
		

		m_description=new MultiLineText(new Vec2f (0.5F,7),10,70,0.8F);
		m_window.add(m_description);
	//	m_description.AddText(m_player.getInventory().getItem(0).getDescription());
		
		m_encumbrance=new Text(new Vec2f(3,-14.9F),"weight:"+Integer.toString(m_player.getInventory().getWeight())+"/"+Integer.toString(m_player.getInventory().getCapacity()),
		0.7F,tint);
		encumbranceWarning=new TextColoured(new Vec2f(3,-15.6F),"warning",
				0.7F,tint);
		calculateWarning();
		currencyTexts=new Text[2];
		currencyTexts[0]=new Text(new Vec2f(8,-14.9F),"credits:"+m_player.getInventory().getPlayerCredits(),
				0.7F,tint);
		currencyTexts[1]=new Text(new Vec2f(8,-15.6F),"gold:"+m_player.getInventory().getPlayerGold(),
				0.7F,tint);

		m_dropdown=new DropDown(frame,new Vec2f(0,0),new Vec2f(7,4),this,SceneBase.getVariables()[0]);
		m_dropdown.setVisible(false);
		m_dropdownstrings=new String[5];
		m_dropdownstrings[2]="info";
		m_dropdownstrings[3]="drop";
		m_dropdownstrings[4]="quick";
		m_dropdownstrings[0]="";
		m_dropdownstrings[1]="";
		
		m_button=new Button(new Vec2f(14.0F,-16.0F),new Vec2f(6,1.8F),button,this,"Exit",3,1);
		
		popup=new Popup(frame,new Vec2f(-18.5F,4));
		
		popupBig=new UI_Popup(new Vec2f(-14,-14),new Vec2f(28,28),frame);
	}
	
	private void calculateWarning()
	{
		switch(m_player.getInventory().getEncumbrance())
		{
		case 1:
			encumbranceWarning.setString("fine");
			encumbranceWarning.setTint(0, 1, 0);
			break;
		case 2:
			encumbranceWarning.setString("slowed");
			encumbranceWarning.setTint(1, 1, 0);	
			break;
		case 3:
			encumbranceWarning.setString("staggering");
			encumbranceWarning.setTint(1, 0.5F, 0);	
			break;		
		case 4:
			encumbranceWarning.setString("critical");
			encumbranceWarning.setTint(1, 0, 0);	
			break;		
			
		}
	}
	
	@Override
	public void start(MouseHook hook)
	{
		m_hook=hook;
		hook.Register(m_list);
		hook.Register(m_window);
		hook.Register(m_dropdown);
		hook.Register(m_button);
		hook.Register(popupBig);
		m_dropdown.setHook(hook);
	}
	

	@Override
	public void update(float DT)
	{
	
		m_window.update(DT);
		popup.update(DT);
		popupBig.update(DT);
		if (m_dropdown.getVisible()==false)
		{
			m_list.update(DT);
			if (m_hook.buttonDown())
			{
				for (int i=0;i<5;i++)
				{
					if (m_rect[i].Contains(m_hook.getPosition()))
					{
						for (int j=0;j<5;j++)
						{
							m_slots[j].setTint(false);
						}
						m_slots[i].setTint(true);
						m_control=(i*-1)-1;
						m_list.setSelect(-1);
						GenDropDown();	

				
						break;
					}
				}
				
			}		
		}
		else
		{
			m_dropdown.update(DT);
		}

		
		if (m_list.getSelect()!=-1 && m_control<0)
		{
			m_control=m_list.getSelect()+1;
			for (int j=0;j<4;j++)
			{
				m_slots[j].setTint(false);
			}	
		//	m_description.AddText(m_player.getInventory().getItem(m_control-1).getDescription());
		}
		if (m_list.getSelect()!=-1 && m_control!=m_list.getSelect()+1)
		{
		//	m_description.AddText(m_player.getInventory().getItem(m_control-1).getDescription());
		}
		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
		{
			m_player.calcMove();
			m_callback.Remove();	
		}
	}
	
	
	@Override
	public void draw(FloatBuffer buffer, int matrixloc)
	{
		m_list.Draw(buffer, matrixloc);
		m_window.Draw(buffer, matrixloc);
		m_encumbrance.Draw(buffer, matrixloc);
		encumbranceWarning.Draw(buffer, matrixloc);
		for (int i=0;i<2;i++)
		{
			currencyTexts[i].Draw(buffer, matrixloc);
		}
		m_dropdown.Draw(buffer, matrixloc);
		m_button.Draw(buffer, matrixloc);
		popup.Draw(buffer, matrixloc);
		popupBig.Draw(buffer, matrixloc);
		
	}
	
	@Override
	public void discard(MouseHook mouse)
	{
		
		mouse.Remove(m_list);
		mouse.Remove(m_window);
		mouse.Remove(popup);
		mouse.Remove(popupBig);
		mouse.Remove(m_dropdown);
		mouse.Remove(m_button);
		m_list.discard();
		m_encumbrance.discard();
		encumbranceWarning.discard();
		for (int i=0;i<2;i++)
		{
			currencyTexts[i].discard();
		}
		m_dropdown.discard();
		m_button.discard();
		popup.discard();
		popupBig.discard();
		screenAlive=false;
	}



	void UseItem()
	{
		if (m_control>0)
		{
		//check slot
		Item item=m_player.getInventory().getItem(m_control-1);
		if (item.getUse()!=ItemUse.USE)
		{
			m_dropdown.setVisible(false);
			return;
		}
		item=m_player.getInventory().RemoveItem(item);
		//apply item
		
		if (item.getClass().getName().contains("Consumable"))
		{
			ItemConsumable consumable=(ItemConsumable)item;
			for (int i=0;i<consumable.getNumEffects();i++)
			{
				m_player.ApplyEffect(consumable.getEffect(i));	
				popup.setClock(10);
	
				popup.setText(ViewScene.m_interface.getLastMessage());
			}
			
			m_player.setBusy(2);
			if (m_player.getInventory().getItem(m_control-1)==null)
			{
				m_dropdown.setVisible(false);
			}
			calculateWarning();
		}
	
				//reset list				
				ResetList();				

		}
	}
	
	void DropItem()
	{
		//check slot
		Item item=null;
		//drop item
		if (m_control>0)
		{
			item=m_player.getInventory().getItem(m_control-1);
			item=m_player.getInventory().RemoveItem(item);
		}
		else
		{
			item=m_player.UnEquip((m_control*-1)-1);
		}
		
		if (item!=null)
		{
			m_callback.Drop((int)m_player.getPosition().x,(int)m_player.getPosition().y,item);
		}
		//time tick
		
		m_player.TakeAction();
		
		//reset list
		ResetList();
		m_dropdown.setVisible(false);
		calculateWarning();
	}
	
	void ResetList()
	{
		if (screenAlive)
		{
			String str[]=new String[m_player.getInventory().getNumItems()];
			for (int i=0;i<str.length;i++)
			{
				str[i]=m_player.getInventory().getItem(i).getName()+" "+m_player.getInventory().getItem(i).getWeight();
				
			}
			m_encumbrance.setString("weight:"+Integer.toString(m_player.getInventory().getWeight())+"/"+Integer.toString(m_player.getInventory().getCapacity()));
			m_list.GenList(str);
			
			for (int i=0;i<5;i++)
			{
				if (m_player.getInventory().getSlot(i)!=null)
				{
					m_slots[i].setString(m_player.getInventory().getSlot(i).getName());	
				}
				else
				{
					m_slots[i].setString("none");
				}
			}		
		}
	}
	
	void Equip()
	{
		Item item=m_player.getInventory().getItem(m_control-1);
		if (item.getUse()==ItemUse.EQUIP)
		{
			ItemEquip equip=(ItemEquip)item.getItem();
			Item it=null;
			if (equip.isStackEquip()==false)
			{
				it=m_player.getInventory().RemoveItem(item);
			}
			else
			{
				it=item;
				m_player.getInventory().setWeight(m_player.getInventory().getWeight()-it.getWeight());
				m_player.getInventory().getItems().remove(item);
			}
			
			item=m_player.Equip(equip.getSlot(), it);
			if (item!=null)
			{
				m_player.getInventory().AddItem(item);
			}
			switch (equip.getSlot())
			{
				case Inventory.HAND:
				m_player.setBusy(1);

				break;
				case Inventory.BODY:
				m_player.setBusy(4);	
				break;
				case Inventory.ACCESSORY:
					m_player.setBusy(2);		
					break;
				case Inventory.HEAD:
					m_player.setBusy(4);		
					break;
			}

		}
		m_control=0;
		m_list.setSelect(-1);
		ResetList();
		m_dropdown.setVisible(false);
		m_callback.UpdateInfo();
	}
	
	void UnEquip()
	{
		Item item=m_player.UnEquip((m_control*-1)-1);
		
		if (item!=null)
		{
			m_player.getInventory().AddItem(item);		
		}	
		m_control=0;
		m_list.setSelect(-1);
		ResetList();	
		m_dropdown.setVisible(false);
		m_callback.UpdateInfo();
	}
	
	@Override
	public void ButtonCallback(int ID, Vec2f p) {
	
		
		switch (ID)
		{
		case 0:
			//equip/unequip
			if (m_control>0)
			{
				Equip();
			}
			else if (m_control<0)
			{
				UnEquip();
			}
			break;
		
		case 1:
			//use
			UseItem();
			break;
			
		case 2:
			//drop
			DropItem();
			break;
		
		
		case 3:
			m_player.calcMove();
			m_callback.Remove();
			break;

		case 12:
			HandleDropdown();
			break;
		}
	}

	
	void UnequipEquiporUse()
	{
		if (m_dropdownstrings[0].equals("use"))
		{
			UseItem();
		}
		if (m_dropdownstrings[0].equals("equip"))
		{
			Equip();
		}
		if (m_dropdownstrings[0].equals("unequip"))
		{
			UnEquip();
		}
	}
	
	
	void quickslot()
	{
		Item quick=m_player.getInventory().getItem(m_control-1);
		if (quick==null)
		{
			return;
		}
		//take item out of quickslot
		Item item=m_player.getInventory().getSlot(Inventory.QUICK);
		if (item!=null)
		{
			m_player.getInventory().setSlot(null, Inventory.QUICK);
			m_player.addBusy(1);
			m_player.getInventory().getItems().add(item);

		}
		//put item in quickslot
		m_player.getInventory().setSlot(quick, Inventory.QUICK);
		m_player.addBusy(1);
		m_player.getInventory().getItems().remove(quick);
		m_callback.UpdateInfo();
		ResetList();
		m_dropdown.setVisible(false);
	}
	
	
	void Reload(ItemDepletableInstance instance)
	{
		ItemHasEnergy item=(ItemHasEnergy)instance.getItem();
		for (int i=0;i<m_player.getInventory().getNumItems();i++)
		{
			if (ItemAmmo.class.isInstance(m_player.getInventory().getItem(i).getItem()))
			{
				if (item.getEnergy().getRefill().contains(m_player.getInventory().getItem(i).getItem().getName()))
				{
					ItemDepletableInstance ammo=(ItemDepletableInstance)m_player.getInventory().getItem(i);
					if (ammo.getEnergy()>0)
					{
						m_player.TakeAction();
						//drain item to try and replenish that which we're recharging
						float Eneed=item.getEnergy().getMaxEnergy()-instance.getEnergy();
						float Eavailable=((float)ammo.getEnergy())*item.getEnergy().getrefillrate();
						if (Eneed>=Eavailable)
						{
							//drain ammo entirely
							ammo.setEnergy(0);
							instance.setEnergy(instance.getEnergy()+Eavailable);
							if (DisposeAmmo((ItemHasEnergy)ammo.getItem()))
							{
								Item item0=null;

									item0=m_player.getInventory().getItem(i);
									item0=m_player.getInventory().RemoveItem(item0);
								//time tick
								
								m_player.TakeAction();
								
								//reset list
								ResetList();
							}
						}
						else
						{
							float Edrain=Eneed/item.getEnergy().getrefillrate();
							ammo.setEnergy(ammo.getEnergy()-Edrain);
							instance.setEnergy(item.getEnergy().getMaxEnergy());
						}
					}
				}
			}
		}	
		m_callback.UpdateInfo();
		calculateWarning();
	}
	
	boolean DisposeAmmo(ItemHasEnergy def)
	{
		if (def.getEnergy().getRefill()==null)
		{
			return true;
		}
		return false;
	}
	
	
	void Info()
	{
		if (m_control<0)
		{
			if (m_player.getInventory().getSlot((m_control*-1)-1)!=null)
			{
				m_description.addText(m_player.getInventory().getSlot((m_control*-1)-1).getDescription());
			}
			else
			{
				m_description.clean();
			}
		}
		if (m_control>0)
		{
			if (ItemExpositionInstance.class.isInstance(m_player.getInventory().getItem(m_control-1)))
			{
				ItemExpositionInstance iei=(ItemExpositionInstance)m_player.getInventory().getItem(m_control-1);
				try {
					popupBig.setString(ParserHelper.readString("assets/data/exposition/"+iei.getExposition()+".txt"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				m_description.addText(m_player.getInventory().getItem(m_control-1).getDescription());
			}
		
		}
	}
	
	private void split(ItemStack item)
	{
		if (item.getCount()>1)
		{
			m_player.getInventory().getItems().add(item.getItem());
			item.setCount(item.getCount()-1);
			ResetList();
		}
	}
	
	void HandleDropdown()
	{
		switch (m_dropdown.getSelect())
		{
		case 0:
			UnequipEquiporUse();
			break;
		
		case 1:
			if (m_dropdownstrings[1].equals("reload"))
			{
				
				ItemDepletableInstance instance=null;
				if (m_control>0)
				{
					instance=(ItemDepletableInstance)m_player.getInventory().getItem(m_control-1);
				}
				if (m_control<0)
				{
					instance=(ItemDepletableInstance)m_player.getInventory().getSlot((m_control*-1)-1);	
				}
				Reload(instance);
			}
			if (m_dropdownstrings[1].equals("split") && ItemStack.class.isInstance(m_player.getInventory().getItem(m_control-1)))
			{
				split((ItemStack)m_player.getInventory().getItem(m_control-1));
			}
			break;
		
		case 2:
			Info();
			break;
			
		case 3:
			DropItem();
			break;
		case 4:
			if (m_dropdownstrings[4].equals("quick"))
			{
				quickslot();
			}
	
			break;
		
		}
	}

	void GenDropDown()
	{
		
		m_dropdownstrings[4]="";
		
		//get control
		Item item=null;
		if (m_control>0)
		{
			item=m_player.getInventory().getItem(m_control-1);
		}
		else if (m_control<0)
		{
			item=m_player.getInventory().getSlot((m_control*-1)-1);
		}
		//get item
		if (item!=null)
		{
			if (item.getItem().getUse()==Item.ItemUse.NONE)
			{
				m_dropdownstrings[0]="";
			}
			else
			{
				if (item.getItem().getUse()==Item.ItemUse.EQUIP)
				{
					if (m_control<0)
					{
						m_dropdownstrings[0]="unequip";				
					}
					else
					{
						m_dropdownstrings[0]="equip";	
						if (ItemWeapon.class.isInstance(item.getItem()))
						{
							m_dropdownstrings[4]="quick";
						}
					}

				}
				if (item.getItem().getUse()==Item.ItemUse.USE)
				{
					m_dropdownstrings[0]="use";	
					m_dropdownstrings[4]="quick";
				}
			}
			if (ItemDepletableInstance.class.isInstance(item))
			{
				ItemDepletableInstance instance=(ItemDepletableInstance)item;
				ItemHasEnergy def=(ItemHasEnergy)instance.getItem();
				if (def.getEnergy()!=null)
				{
					if (def.getEnergy().getRefill()!=null)
					{
						if (CanReload(def) && def.getEnergy().getMaxEnergy()>instance.getEnergy())
						{
							m_dropdownstrings[1]="reload";
						}
					}
					else
					{
						m_dropdownstrings[1]="";
					}
				}
				else
				{
					m_dropdownstrings[1]="";		
				}
				
			}
			else
			{
				if (ItemStack.class.isInstance(item))
				{
					m_dropdownstrings[1]="split";
				}
				else
				{
					m_dropdownstrings[1]="";		
				}
			}
			m_dropdown.BuildFonts(m_dropdownstrings);
			
			m_dropdown.setVisible(true);
			m_dropdown.AdjustPos(new Vec2f(m_hook.getPosition().x-1.0F,m_hook.getPosition().y-1.0F));	
		}
	
	}
	
	boolean CanReload(ItemHasEnergy item)
	{
		//check for ammo
		for (int i=0;i<m_player.getInventory().getNumItems();i++)
		{
			if (ItemAmmo.class.isInstance(m_player.getInventory().getItem(i).getItem()))
			{
				if (item.getEnergy().getRefill().contains(m_player.getInventory().getItem(i).getItem().getName()))
				{
					ItemDepletableInstance instance=(ItemDepletableInstance)m_player.getInventory().getItem(i);
					if (instance.getEnergy()>0)
					{
						return true;
					
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void Callback() {
		if (m_dropdown.getVisible()==false)
		{
			m_control=m_list.getSelect()+1;
			GenDropDown();		
		}
		// TODO Auto-generated method stub

		//m_description.AddText(m_player.getInventory().getItem(m_control-1).getDescription());
	}

}
