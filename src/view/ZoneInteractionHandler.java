package view;

import nomad.Universe;
import faction.violation.FactionListener;
import faction.violation.FactionRule.ViolationType;
import rlforj.los.ILosBoard;
import shared.Vec2f;
import shared.Vec2i;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetBreakable;
import widgets.WidgetSlot;
import zone.Zone;
import actor.Actor;
import actor.Attackable;
import actor.npc.NPC;
import actor.player.Player;
import actorRPG.Actor_RPG;
import actorRPG.Player_RPG;
import combat.CombatMove.AttackPattern;

public class ZoneInteractionHandler {

	Zone m_zone;

	private FactionListener factionListener;

	public static ModelController_Int m_view;

	ZoneInteractionHandler(Zone zone, ModelController_Int view) {
		setZone(zone);
		m_view = view;
	}

	public void update() {
		if (factionListener != null) {
			factionListener.update();
		}

	}

	public void setZone(Zone zone) {
		if (zone != null) {
			if (factionListener != null) {
				m_zone.setViolationLevel(factionListener.getViolationLevel());
			}
			m_zone = zone;
			factionListener = null;
			if (zone.getZoneRules() != null) {
				factionListener = new FactionListener(zone.getZoneRules(), m_zone.getActors());
				if (zone.getViolationLevel() > 0) {
					factionListener.setViolation(zone.getViolationLevel(), null);
				}
			}
		}
	}

	public void shutdown() {
		if (factionListener != null) {
			if (factionListener.getViolationLevel() > 0) {
				m_zone.setViolationLevel(factionListener.getViolationLevel());
			}
		}
	}

	public boolean ContainsPoint(Vec2f p) {
		if (p.x >= 0 && p.x < m_zone.getWidth()) {
			if (p.y >= 0 && p.y < m_zone.getHeight()) {
				return true;
			}
		}
		return false;
	}

	public boolean Look(Vec2f p) {
		// check for tile visibility
		int x0 = (int) p.x;
		int y0 = (int) p.y;
		if (m_zone.zoneTileGrid[x0][y0] == null) {
			return false;
		}
		if (m_zone.zoneTileGrid[x0][y0].getVisible() == false) {
			return false;
		}

		// check for actor in tile
		if (m_zone.zoneTileGrid[(int) p.x][(int) p.y] != null
				&& m_zone.zoneTileGrid[(int) p.x][(int) p.y].getActorInTile() != null) {
			if (m_zone.zoneTileGrid[(int) p.x][(int) p.y].getActorInTile().getVisible()) {
				m_view.DrawText(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getActorInTile().getDescription());

				return false;
			}
		}
		/*
		 * for (int i=0;i<m_zone.zoneActors.size();i++) {
		 * 
		 * int x1=(int)m_zone.zoneActors.get(i).getPosition().x; int
		 * y1=(int)m_zone.zoneActors.get(i).getPosition().y; if (x0==x1 &&
		 * y0==y1 && m_zone.zoneActors.get(i).getVisible()==true) { //post
		 * m_view.DrawText(m_zone.zoneActors.get(i).getDescription());
		 * 
		 * return false; } }
		 */
		if (m_zone.zoneTileGrid[x0][y0].getWidgetObject() != null) {
			String str = m_zone.zoneTileGrid[x0][y0].getWidgetObject().getDescription();
			if (str == null) {
				str = m_zone.zoneTileGrid[x0][y0].getDefinition().getDescription();
			}
			m_view.DrawText(str);

			return false;
		}

		// just post the description for the tile
		m_view.DrawText(m_zone.zoneTileGrid[x0][y0].getDefinition().getDescription());

		return false;
	}

	boolean InteractActor(Vec2f p, Player player) {
		// if actor is player dont interact
		Actor actor = m_zone.getActor((int) p.x, (int) p.y);
		if (actor != null) {
			if (actor != player) {
				actor.Interact(player);
				player.TakeAction();
				violationCheck(actor.getName(), p, ViolationType.Interact);
				return true;
			}
		}
		return false;
	}

	public boolean CanTalk(Player player) {
		for (int i = 0; i < m_zone.getActors().size(); i++) {
			Actor actor = m_zone.getActors().get(i);
			if (actor.getClass().getName().contains("NPC")) {
				NPC npc = (NPC) actor;
				if (npc.getVisible() == true && npc.getRPGHandler().getActive() == true) {
					if (npc.isHostile(player.getActorFaction().getFilename()) == true) {
						return false;
					}

				}

			}

		}
		return true;
	}

	public void Interact(Vec2f p, Player player) {
		if (m_zone.zoneTileGrid[(int) p.x][(int) p.y] == null) {
			return;
		}
		// check position within 1 tile
		float d = p.getDistance(player.getPosition());
		if (d < 2.5F) {
			if (CanTalk(player)) {
				// actor check
				if (InteractActor(p, player) == false) {
					// check for widget to interact with
					if (m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject() != null) {
						m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject().Interact(player);
						player.TakeAction();
						if (WidgetBreakable.class
								.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
							WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
									.getWidgetObject();
							violationCheck(w.getName(), p, ViolationType.Interact);
						}

					}
				}
			} else {
				// check for widget to interact with
				if (m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject() != null) {
					Widget widget = m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject();
					if (widget.safeOnly()) {
						if (CanTalk(player)) {
							widget.Interact(player);
							player.TakeAction();
							if (WidgetBreakable.class
									.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
								WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
										.getWidgetObject();
								violationCheck(w.getName(), p, ViolationType.Interact);
							}
						}
					} else {
						widget.Interact(player);
						player.TakeAction();
						if (WidgetBreakable.class
								.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
							WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
									.getWidgetObject();
							violationCheck(w.getName(), p, ViolationType.Interact);
						}
					}

				} else {
					m_view.DrawText("Not safe to do that right now");
				}

			}
		}
	}

	public void violationCheck(String name, Vec2f p, ViolationType type) {
		if (factionListener != null) {
			factionListener.checkViolation(name, p, type);
		}
	}
	/*
	 * boolean MeleeAttack(Vec2f p, Player player) { int xt=(int)p.x; int
	 * yt=(int)p.y; //check widget if
	 * (m_zone.zoneTileGrid[(int)p.x][(int)p.y].getWidgetObject()!=null) { if
	 * (Attackable.class.isInstance(m_zone.zoneTileGrid[(int)p.x][(int)p.y].
	 * getWidgetObject())) { Attackable
	 * attackable=(Attackable)m_zone.zoneTileGrid[(int)p.x][(int)p.y].
	 * getWidgetObject(); //attack this
	 * attackable.Harm(player.getAttack().getDamage(0), player,0);
	 * m_view.Flash(new Vec2f(xt,yt),0); player.TakeAction();
	 * violationCheck(attackable.getName(),p,ViolationType.Attack); return true;
	 * } } //check for actor in tile for (int
	 * i=0;i<m_zone.zoneActors.size();i++) { if
	 * (m_zone.zoneActors.get(i)!=player &&
	 * m_zone.zoneActors.get(i).getVisible()==true &&
	 * m_zone.zoneActors.get(i).getAttackable()) { int
	 * x=(int)m_zone.zoneActors.get(i).getPosition().x; int
	 * y=(int)m_zone.zoneActors.get(i).getPosition().y; if (xt==x && yt==y) {
	 * //conduct attack Attackable
	 * attackable=(Attackable)m_zone.zoneActors.get(i);
	 * player.Attack(attackable,m_view); player.TakeAction();
	 * violationCheck(attackable.getName(),p,ViolationType.Attack); return true;
	 * } } } return false; }
	 * 
	 * boolean RangedAttack(Vec2f p, Player player) { //check widget int
	 * xt=(int)p.x; int yt=(int)p.y; //check line of sight if
	 * (GameManager.m_los.existsLineOfSight((ILosBoard)m_zone,(int)player.
	 * getPosition().x, (int)player.getPosition().y,(int)p.x, (int)p.y, true)) {
	 * if (m_zone.zoneTileGrid[(int)p.x][(int)p.y].getWidgetObject()!=null) { if
	 * (Attackable.class.isInstance(m_zone.zoneTileGrid[(int)p.x][(int)p.y].
	 * getWidgetObject())) { Attackable
	 * attackable=(Attackable)m_zone.zoneTileGrid[(int)p.x][(int)p.y].
	 * getWidgetObject(); //attack this m_view.Flash(new Vec2f(xt,yt),0);
	 * attackable.Harm(player.getAttack().getDamage(0), player,0);
	 * player.TakeAction();
	 * violationCheck(attackable.getName(),p,ViolationType.Attack); return true;
	 * } } //check for actor in tile for (int
	 * i=0;i<m_zone.zoneActors.size();i++) { if
	 * (m_zone.zoneActors.get(i)!=player &&
	 * m_zone.zoneActors.get(i).getVisible()==true &&
	 * m_zone.zoneActors.get(i).getAttackable()) { int
	 * x=(int)m_zone.zoneActors.get(i).getPosition().x; int
	 * y=(int)m_zone.zoneActors.get(i).getPosition().y; if (xt==x && yt==y) {
	 * //conduct attack Attackable
	 * attackable=(Attackable)m_zone.zoneActors.get(i);
	 * player.Attack(attackable,m_view); player.TakeAction();
	 * violationCheck(attackable.getName(),p,ViolationType.Attack); return true;
	 * } } } }
	 * 
	 * 
	 * return false; }
	 * 
	 * public void Attack (Vec2f p, Player player) { attack.Attack
	 * attack=player.getAttack(); if (attack.getRanged()==true) { //handle
	 * ranged RangedAttack(p, player); } else if
	 * (player.getPosition().getDistance(p)<2) { //handle melee
	 * MeleeAttack(p,player); } }
	 * 
	 * public void Seduce(Vec2f p, Player player) { int xt=(int)p.x; int
	 * yt=(int)p.y; for (int i=0;i<m_zone.zoneActors.size();i++) { if
	 * (m_zone.zoneActors.get(i)!=player &&
	 * m_zone.zoneActors.get(i).getVisible()==true &&
	 * m_zone.zoneActors.get(i).getAttackable()) { int
	 * x=(int)m_zone.zoneActors.get(i).getPosition().x; int
	 * y=(int)m_zone.zoneActors.get(i).getPosition().y; if (xt==x && yt==y) { if
	 * (GameManager.m_los.existsLineOfSight((ILosBoard)m_zone,(int)player.
	 * getPosition().x, (int)player.getPosition().y,(int)p.x, (int)p.y, true)) {
	 * //roll damage int
	 * dmg=GameManager.m_random.nextInt(6)+player.getRPG().getAttribute(
	 * Actor_RPG.SEDUCTION); //reduce by distance
	 * dmg=dmg-(int)(player.getPosition().getDistance(m_zone.zoneActors.get(i).
	 * getPosition())); //apply to actor if (dmg<0) { dmg=0; } int
	 * strength=m_zone.zoneActors.get(i).Weaken(dmg, Actor_RPG.TEASE, player);
	 * //text message go String
	 * str="You seduce "+m_zone.zoneActors.get(i).getName()+" and they lose "
	 * +Integer.toString(strength)+" resolve";
	 * ViewScene.m_interface.DrawText(str);
	 * 
	 * player.TakeAction();
	 * violationCheck(m_zone.zoneActors.get(i).getName(),p,ViolationType.Seduce)
	 * ; break; } }
	 * 
	 * } } }
	 */

	public static int getDirection(Vec2f o, Vec2f p) {
		int d = 0;
		if (o.x > p.x) {
			d = d + 1; // east
		}
		if (o.x < p.x) {
			d = d + 2;// west
		}
		if (o.y > p.y) {
			d = d + 4; // south
		}
		if (o.y < p.y) {
			d = d + 8; // north
		}
		switch (d) {

		case 2:
			return 2;
		case 1:
			return 6;
		case 4:
			return 4;
		case 5:
			return 5;
		case 6:
			return 3;
		case 8:
			return 0;
		case 9:
			return 7;
		case 10:
			return 1;
		}

		return -1;
	}

	public static Vec2f getPos(int i, Vec2f p) {
		switch (i) {
		case 0:
			return new Vec2f(p.x, p.y + 1);
		case 1:
			return new Vec2f(p.x + 1, p.y + 1);
		case 2:
			return new Vec2f(p.x + 1, p.y);
		case 3:
			return new Vec2f(p.x + 1, p.y - 1);
		case 4:
			return new Vec2f(p.x, p.y - 1);
		case 5:
			return new Vec2f(p.x - 1, p.y - 1);
		case 6:
			return new Vec2f(p.x - 1, p.y);
		case 7:
			return new Vec2f(p.x - 1, p.y + 1);
		}
		return p;
	}

	public static Vec2i getPos(int i, Vec2i p) {
		switch (i) {
		case 0:
			return new Vec2i(p.x, p.y + 1);
		case 1:
			return new Vec2i(p.x + 1, p.y + 1);
		case 2:
			return new Vec2i(p.x + 1, p.y);
		case 3:
			return new Vec2i(p.x + 1, p.y - 1);
		case 4:
			return new Vec2i(p.x, p.y - 1);
		case 5:
			return new Vec2i(p.x - 1, p.y - 1);
		case 6:
			return new Vec2i(p.x - 1, p.y);
		case 7:
			return new Vec2i(p.x - 1, p.y + 1);
		}
		return p;
	}

	public FactionListener getFactionListener() {
		return factionListener;
	}

	public void setFactionListener(FactionListener factionListener) {
		this.factionListener = factionListener;
	}

	public void attack(Vec2f p, Player player) {

		// use first attack on location
		if (!useMove(0, p, player)) {
			Player_RPG rpg = (Player_RPG) player.getRPG();
			rpg.genMoveList();
		}
	}

	public boolean special(Vec2f p, Player player) {
		// use numbered attack on the location
		return useMove(player.getSpecialMove(), p, player);
	}

	private boolean useTargetedMove(int number, int x, int y, Player player) {
		if (GameManager.m_los.existsLineOfSight((ILosBoard) m_zone, (int) player.getPosition().x,
				(int) player.getPosition().y, x, y, true)) {
			if (m_zone.zoneTileGrid[x][y].getWidgetObject() != null) {
				if (Attackable.class.isInstance(m_zone.zoneTileGrid[x][y].getWidgetObject())) {
					Attackable attackable = (Attackable) m_zone.zoneTileGrid[x][y].getWidgetObject();
					// attack this
					m_view.Flash(new Vec2f(x, y), 0);
					// attackable.Harm(player.getAttack().getDamage(0),
					// player,0);
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Attack);
					return player.useMove(number, attackable);
					// return true;
				}
				if (WidgetSlot.class.isInstance(m_zone.zoneTileGrid[x][y].getWidgetObject())) {
					WidgetSlot ws = (WidgetSlot) m_zone.zoneTileGrid[x][y].getWidgetObject();
					if (ws.getWidget() != null) {
						Attackable attackable = (Attackable) ws.getWidget();

						// attack this
						// m_view.Flash(new Vec2f(xt,yt),0);
						// attackable.Harm(player.getAttack().getDamage(0),
						// player,0);
						if (player.useMove(number, attackable)) {
							ws.handleAttack();
						} else {
							return false;
						}
					}
				}
			}
			// check for actor in tile
			if (m_zone.zoneTileGrid[x][y] != null && m_zone.zoneTileGrid[x][y].getActorInTile() != null
					&& m_zone.zoneTileGrid[x][y].getActorInTile() != player) {
				Attackable attackable = (Attackable) m_zone.zoneTileGrid[x][y].getActorInTile();
				// player.Attack(attackable,m_view);
				if (player.getMove(number).isNonViolent()) {
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Seduce);
				} else {
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Attack);
				}

				return player.useMove(number, attackable);
			}

		}

		return false;
	}

	private boolean useSelfMove(int number, Player player) {
		if (player.useMove(number, player)) {
			m_view.Flash(new Vec2f(player.getPosition().x, player.getPosition().y), 2);
			ViewScene.m_interface.redrawBars();
			return true;
		}
		return false;
	}

	private boolean useMove(int number, Vec2f p, Player player) {
		// check widget
		int xt = (int) p.x;
		int yt = (int) p.y;
		// check line of sight
		if (player.getMove(number).getAttackPattern() == AttackPattern.P_SELF) {
			return useSelfMove(number, player);
		}
		return useTargetedMove(number, xt, yt, player);

	}
}
