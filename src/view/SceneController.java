package view;

import shared.Vec2f;
import vmo.GameManager;
import widgets.Widget;
import zone.TileDef;
import zone.Zone;
import actor.Actor;
import actor.npc.NPC;
import actor.player.Inventory;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import actorRPG.player.Player_RPG;
import artificial_intelligence.detection.Sense;
import artificial_intelligence.senseCriteria.CriteriaRepository;
import artificial_intelligence.senseCriteria.Sense_Criteria;
import item.Item;
import item.ItemWeapon;
import nomad.FlagField;
import nomad.universe.Universe;

public class SceneController implements Sense {

	private Zone activeZone;
	private Universe gameUniverse;
	private ZoneInteractionHandler interactionHandler;
	private QuickslotHandler quickslotHandler = new QuickslotHandler();
	private CriteriaRepository criteriaRepository;
	
	public void initializeHandler(ModelController_Int view) {
		interactionHandler = new ZoneInteractionHandler(null, view);
		criteriaRepository=new CriteriaRepository();
	}

	public ZoneInteractionHandler getHandler() {
		return interactionHandler;
	}

	public Zone getActiveZone() {
		return activeZone;
	}

	public void setActiveZone(Zone m_zone) {
		this.activeZone = m_zone;
		interactionHandler.setZone(m_zone);
	}

	public Universe getUniverse() {
		return gameUniverse;
	}

	public void setUniverse(Universe m_world) {
		this.gameUniverse = m_world;
	}

	@Override
	public boolean CanWalk(int x, int y) {
		if (activeZone.contains(x, y))
		{
			if (activeZone.getTiles()[x][y] != null) {
				if (activeZone.getTiles()[x][y].getDefinition().getMovement() == TileDef.TileMovement.WALK) {
					return true;
				}

			}		
		}
		return false;
	}

	@Override
	public int getViolationLevel() {

		if (interactionHandler.getFactionListener() != null) {
			return interactionHandler.getFactionListener().getViolationLevel();
		}
		return 0;
	}

	@Override
	public Vec2f getViolationLocation() {

		if (interactionHandler.getFactionListener() != null) {
			return interactionHandler.getFactionListener().getViolationLocation();
		}

		return null;
	}

	public void shutdown() {
		interactionHandler.shutdown();
	}

	public void update() {
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).getPosition().x >= 0) {
				activeZone.getActors().get(i).Update();
			}

		}
		activeZone.update();
		interactionHandler.update();
	}

	@Override
	public Actor getHostile(Actor origin, int maxRange, boolean visibleOnly) {
		Actor hostile = null;
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).getAttackable()
					&& activeZone.getActors().get(i).isHostile(origin.getActorFaction().getFilename())) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition());
				if (d < maxRange) {
					if (visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
						if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
								(int) origin.getPosition().y, (int) target.getPosition().x,
								(int) target.getPosition().y, true)) {
							if (distance > d) {
								distance = d;
								hostile = target;
							}
						}
					} else {
						if (distance > d) {
							distance = d;
							hostile = target;
						}
					}

				}

			}
		}
		return hostile;
	}

	@Override
	public Actor getPlayer(Actor origin, boolean visibleOnly) {
		if (visibleOnly == true) {
			if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
					(int) origin.getPosition().y, (int) gameUniverse.getPlayer().getPosition().x,
					(int) gameUniverse.getPlayer().getPosition().y, true)) {
				return gameUniverse.getPlayer();
			}
		} else {
			return gameUniverse.getPlayer();
		}

		return null;
	}

	@Override
	public boolean getPreference(String preference) {

		return Universe.getInstance().getPrefs().ForbiddenPref(preference);

	}

	@Override
	public void drawText(String text) {

		ViewScene.m_interface.DrawText(text);

	}

	public void useQuickslot() {
		quickslotHandler.handle();

	}

	private boolean checkExclude(Actor actor, int exAttribute, int exValue)
	{
		int comp=actor.getRPG().getAttribute(exAttribute);
		if (comp>=exValue)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public Actor getHostile(Actor origin, int maxRange, boolean visibleOnly, String exclude, int exValue) {
		Actor hostile = null;
		int attribute=RPG_Helper.AttributefromString(exclude);
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).getAttackable()
					&& activeZone.getActors().get(i).isHostile(origin.getActorFaction().getFilename())) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition())-target.getThreat().getThreat();
				if (d < maxRange) {
					if (visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
						if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
								(int) origin.getPosition().y, (int) target.getPosition().x,
								(int) target.getPosition().y, true)) {
							if (distance > d && !checkExclude(target,attribute,exValue)) {
								distance = d;
								hostile = target;
							
							}
						}
					} else {
						if (distance > d && !checkExclude(target,attribute,exValue)) {
							distance = d;
							hostile = target;
						}
					}

				}

			}
		}
		return hostile;
	}

	@Override
	public Actor getVictim(Actor origin, int maxrange, boolean visibleOnly, String name, boolean seduced) {
		Actor victim = null;
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			//if (activeZone.getActors().get(i).isHostile(origin.getActorFaction().getFilename())) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition());
				if (d < 8) {
					if (!target.getRPGHandler().getActive() && NPC.class.isInstance(target) ) {
						if (visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
							if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
									(int) origin.getPosition().y, (int) target.getPosition().x,
									(int) target.getPosition().y, true)) {
								if (distance > d && isVictim(target,name, seduced)) {
									distance = d;
									victim = target;						
								}
							}
						} 
						else
						{
							if (d<distance) {
								victim=target;
								distance=d;
							}
						}
					}
				}
			//}
		}
		return victim;
	}

	private boolean isVictim(Actor target, String name, boolean seduced) {
		if (target.getName().equals(name))
		{
			NPC npc=(NPC)target;
			if (npc.isBusy())
			{
				return false;
			}
			if (seduced && target.getRPG().getStat(Actor_RPG.RESOLVE)<=0)
			{
				return true;
			}
			if (!seduced && target.getRPG().getStat(Actor_RPG.HEALTH)<=0)
			{
				return true;
			}		
		}
		return false;
	}

	@Override
	public Actor getNamedActor(Actor origin, int maxRange, boolean visibleOnly, String name) {
		Actor actor = null;
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).isHostile(origin.getActorFaction().getFilename())) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition());
				if (d < maxRange) {
					if (NPC.class.isInstance(target) && visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
						if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
								(int) origin.getPosition().y, (int) target.getPosition().x,
								(int) target.getPosition().y, true)) {
							if (distance > d && target.getName().equals(name)) {
								distance = d;
								actor = target;						
							}
						}
					} else {
						if (distance > d && target.getName().equals(name)) {
							distance = d;
							actor = target;
						}
					}
				}
			}
		}
		return actor;
	}

	@Override
	public Actor getActor(Actor origin, int maxRange, boolean visibleOnly, Sense_Criteria criteria) {
		Actor actor = null;
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition());
				if (d < maxRange) {
					if (NPC.class.isInstance(target) && target.getRPGHandler().getActive() && visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
						if (GameManager.m_los.existsLineOfSight(activeZone.getBoard(1), (int) origin.getPosition().x,
								(int) origin.getPosition().y, (int) target.getPosition().x,
								(int) target.getPosition().y, true)) {
							if (distance > d && criteria.checkCriteria(target,origin)) {
								distance = d;
								actor = target;						
							}
						}
					} else {
						if (distance > d && criteria.checkCriteria(target,origin)) {
							distance = d;
							actor = target;
						}
					}
				}
		}
		return actor;
	}

	@Override
	public Sense_Criteria getCriteria(String properties) {
		return criteriaRepository.getCriteria(properties);
	}

	public Widget getWidget(int x, int y) {
		return activeZone.getWidget(x, y);
	}

	@Override
	public FlagField getGlobalFlags() {
		return gameUniverse.getPlayer().getFlags();
	}

	@Override
	public Actor getActorInTile(int x, int y) {
		if (activeZone.getActor(x, y)!=null && activeZone.getActor(x, y).getRPGHandler().getActive())
		{
			return activeZone.getActor(x, y);
		}
		return null;
	}

}
