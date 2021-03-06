package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.ActorLoader;
import actor.npc.NPC;
import actor.player.Player;
import actorRPG.Actor_RPG;
import artificial_intelligence.pathfinding.Path;
import artificial_intelligence.pathfinding.Pathfinder;
import entities.Entity;
import nomad.universe.Universe;
import rlforj.los.ILosBoard;
import shared.Geometry;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import view.ModelController_Int;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetMarker;
import widgets.WidgetPortal;
import worldgentools.ZoneBuildTools;
import zone.TileDef.TileMovement;
import zone.Environment.EnvironmentalConditions;
import zone.lineOfSight.ZoneSight;
import zonePreload.ZonePreload;
import zonePreload.ZonePreloadController;

public class Zone implements Zone_int {

	public enum zoneType {
		SURFACE(0), LIMITED(1), CLOSED(2);

		int v = 0;

		zoneType(int v) {
			this.v = v;
		}

		public int getV() {
			return v;
		}

	};

	public String zoneName;
	public String zoneDescription;
	private String zoneRules;
	public TileDefLibrary zoneTileLibrary;
	public String tilesetName;
	public Vec2f zonePosition;
	public zoneType type;
	private boolean isVisited;
	public Tile[][] zoneTileGrid;
	public ZonePreloadController preload;
	private int violationLevel;
	private EnvironmentalConditions zoneConditions;
	private ZoneSight[] sightBoard;
	public void setZoneTileGrid(Tile[][] zoneTileGrid) {
		this.zoneTileGrid = zoneTileGrid;
	}

	public ArrayList<Actor> zoneActors;
	public int zoneWidth, zoneHeight;

	public String adjacentZoneNames[];

	public Entity zoneEntity;

	public ModelController_Int viewInterface;

	Vec2f fixedLandingSite;

	public void addPreload(ZonePreload zonePreload) {
		if (zoneTileGrid != null) {
			return;
		}
		if (preload == null) {
			preload = new ZonePreloadController();
		}
		preload.addPreload(zonePreload);

	}

	public TileDefLibrary getZoneTileLibrary() {
		return zoneTileLibrary;
	}

	public String getZoneRules() {
		return zoneRules;
	}

	public Zone(String filename, int x, int y, zoneType type, Entity entity) {
		adjacentZoneNames = new String[4];
		zoneName = filename;
		this.type = type;
		zonePosition = new Vec2f(x, y);
		zoneActors = new ArrayList<Actor>();
		zoneEntity = entity;
		sightBoard=new ZoneSight[2];
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public int getViolationLevel() {
		return violationLevel;
	}

	public void setViolationLevel(int violationLevel) {
		this.violationLevel = violationLevel;
	}

	public Vec2f getPortal(int id) {
		for (int i = 0; i < zoneWidth; i++) {
			if (zoneTileGrid[i] != null) {
				for (int j = 0; j < zoneHeight; j++) {
					if (zoneTileGrid[i][j] != null) {
						if (zoneTileGrid[i][j].getWidgetObject() != null) {
							if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject())) {
								WidgetPortal portal = (WidgetPortal) zoneTileGrid[i][j].getWidgetObject();
								if (portal.getID() == id) {
									Vec2f p = new Vec2f(i, j);
									p = ZoneInteractionHandler.getPos(portal.getPortalFacing(), p);
									return p;
								}
							}
							if (WidgetMarker.class.isInstance(zoneTileGrid[i][j].getWidgetObject())) {
								WidgetMarker marker = (WidgetMarker) zoneTileGrid[i][j].getWidgetObject();
								if (marker.getUID() == id) {
									Vec2f p = new Vec2f(i, j);
									return p;
								}

							}
						}
					}
				}
			}
		}
		return null;
	}

	public ArrayList<WidgetPortal> getPortalWidgets() {
		ArrayList<WidgetPortal> portals = new ArrayList<WidgetPortal>();
		for (int i = 0; i < zoneWidth; i++) {
			for (int j = 0; j < zoneHeight; j++) {
				if (zoneTileGrid[i][j] != null) {
					if (zoneTileGrid[i][j].getWidgetObject() != null) {
						if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject())) {
							WidgetPortal portal = (WidgetPortal) zoneTileGrid[i][j].getWidgetObject();

							portals.add(portal);
						}

					}
				}
			}

		}
		return portals;
	}

	public WidgetPortal getPortalWidget(int id) {
		for (int i = 0; i < zoneWidth; i++) {
			for (int j = 0; j < zoneHeight; j++) {
				if (zoneTileGrid[i][j] != null) {
					if (zoneTileGrid[i][j].getWidgetObject() != null) {
						if (WidgetPortal.class.isInstance(zoneTileGrid[i][j].getWidgetObject())) {
							WidgetPortal portal = (WidgetPortal) zoneTileGrid[i][j].getWidgetObject();
							if (portal.getID() == id) {
								return portal;
							}
						}

					}
				}
			}

		}
		return null;
	}

	public void LoadZone() {
		if (zoneTileGrid == null) {
			Element enode = zoneEntity.LoadZone(this);
			LoadZone(enode);
			zoneEntity.postLoad(this);
		}

	}

	public Vec2f getPosition() {
		return zonePosition;
	}

	public Vec2f getLandingSite() {

		if (fixedLandingSite != null) {
			return fixedLandingSite;
		}

		if (zoneTileGrid == null) {
			Element n = zoneEntity.LoadZone(this);
			if (n.getAttribute("spriteset") != null) {
				tilesetName = n.getAttribute("spriteset");
			}
			NodeList children = n.getChildNodes();

			if (fixedLandingSite == null) {
				for (int i = 0; i < children.getLength(); i++) {
					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element Enode = (Element) node;
						if (Enode.getTagName() == "landingsite") {
							fixedLandingSite = new Vec2f(Integer.parseInt(Enode.getAttribute("x")),
									Integer.parseInt(Enode.getAttribute("y")));
							return fixedLandingSite;
						}
					}
				}
			}

			zoneWidth = Integer.parseInt(n.getAttribute("width"));
			zoneHeight = Integer.parseInt(n.getAttribute("height"));
			int spanx = zoneWidth - 32;
			int spany = zoneHeight - 32;
			int x = (GameManager.m_random.nextInt() % (spanx / 2)) + (spanx / 2);
			int y = (GameManager.m_random.nextInt() % (spany / 2)) + (spany / 2);
			int xout = x + 16;
			int yout = y + 16;
			return new Vec2f(xout, yout);
		} else {
			return fixedLandingSite;
		}

	}

	public void cleanup() {
		for (int i = 0; i < zoneActors.size(); i++) {
			if (zoneActors.get(i).getPosition().x >= 0 && zoneActors.get(i).getPosition().x < getWidth()
					&& zoneActors.get(i).getPosition().y >= 0 && zoneActors.get(i).getPosition().y < getHeight()) {
				if (zoneActors.get(i).getRPG().getStat(Actor_RPG.HEALTH) <= 0) {
					if (NPC.class.isInstance(zoneActors.get(i))) {
						((NPC) zoneActors.get(i)).Remove(true,false);
					}
				}
			}
			if (NPC.class.isInstance(zoneActors.get(i))) {
				NPC npc=(NPC)zoneActors.get(i);
				if (npc.isCompanion())
				{
					zoneActors.remove(i);
					i--;
				}
			}
		}
		for (int i = 0; i < zoneTileGrid.length; i++) {
			for (int j = 0; j < zoneTileGrid[0].length; j++) {
				if (zoneTileGrid[i][j] != null) {
					zoneTileGrid[i][j].setActorInTile(null);
				}
			}
		}
	}

	public void LoadZone(Element n) {
		if (zoneTileGrid == null) {
			if (n.getAttribute("spriteset") != null) {
				tilesetName = n.getAttribute("spriteset");
			}
			NodeList children = n.getChildNodes();
			zoneWidth = Integer.parseInt(n.getAttribute("width"));
			zoneHeight = Integer.parseInt(n.getAttribute("height"));

			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;
					if (Enode.getTagName() == "zonerules") {
						zoneRules = Enode.getAttribute("ruleset");
					}
					if (Enode.getTagName() == "zoneConditions")
					{
						zoneConditions=new EnvironmentalConditions(Enode);
					}
					if (Enode.getTagName() == "description") {
						zoneDescription = Enode.getTextContent();
					}
					if (Enode.getTagName() == "tileset") {
						zoneTileLibrary = new TileDefLibrary(Enode);
					}
					if (Enode.getTagName() == "description") {
						zoneDescription = Enode.getTextContent();
					}

					if (Enode.getTagName() == "landingsite") {
						fixedLandingSite = new Vec2f(Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")));
					}

					// layout
					if (Enode.getTagName() == "mapgen") {
						ZoneBuildTools buildtools = new ZoneBuildTools(zoneName, this);
						if (zoneEntity.getLandings() != null) {
							buildtools.BuildShips(zoneEntity.getLandings(), zonePosition);
						}
						zoneTileGrid = buildtools.BuildZone(Enode);

					}
					if (Enode.getTagName() == "transition") {
						String str = Enode.getAttribute("direction");
						if (str.contains("north")) {
							adjacentZoneNames[0] = Enode.getTextContent();
						}
						if (str.contains("east")) {
							adjacentZoneNames[1] = Enode.getTextContent();
						}
						if (str.contains("south")) {
							adjacentZoneNames[2] = Enode.getTextContent();
						}
						if (str.contains("west")) {
							adjacentZoneNames[3] = Enode.getTextContent();
						}
					}
				}

			}
			preload = null;
		}
	}

	@Override
	public int getHeight() {
		if (zoneHeight == 0) {
			Element n = zoneEntity.LoadZone(this);
			zoneHeight = Integer.parseInt(n.getAttribute("height"));
		}
		return zoneHeight;
	}

	public Tile[][] getTiles() {
		return zoneTileGrid;
	}

	@Override
	public int getWidth() {
		if (zoneWidth == 0) {
			Element n = zoneEntity.LoadZone(this);
			zoneWidth = Integer.parseInt(n.getAttribute("width"));
		}
		return zoneWidth;
	}

	public String getTileset() {
		return "tilesets/" + tilesetName;
	}


	public boolean contains(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		if (x >= zoneWidth || y >= zoneHeight) {
			return false;
		}
		return true;
	}


	public void ClearVisibleTiles() {
		for (int i = 0; i < zoneWidth; i++) {
			for (int j = 0; j < zoneHeight; j++) {
				if (zoneTileGrid[i] != null) {
					if (zoneTileGrid[i][j] != null) {
						zoneTileGrid[i][j].Hide();
					}
				}
			}
		}
	}

	@Override
	public Actor getActor(int x, int y) {

		if (x >= 0 && x < zoneWidth && y >= 0 && y < zoneHeight) {
			if (zoneTileGrid[x][y] != null && zoneTileGrid[x][y].getActorInTile() != null) {
				return zoneTileGrid[x][y].getActorInTile();
			}
		}
		return null;
	}

	@Override
	public Widget getWidget(int x, int y) {

		if (x >= 0 && x < zoneWidth) {
			if (y >= 0 && y < zoneHeight) {
				if (zoneTileGrid[x][y] != null) {
					return zoneTileGrid[x][y].getWidgetObject();
				}

			}
		}
		return null;
	}

	public String getName() {
		return zoneName;
	}

	public void AddPlayer(Player player) {
		boolean b = true;
		for (int i = 0; i < zoneActors.size(); i++) {
			if (Player.class.isInstance(zoneActors.get(i))) {
				if (player.equals(zoneActors.get(i)))
				{
					b = false;
					break;
				}
				else
				{
					zoneActors.remove(i);
					break;
				}
			}
		}
		if (b == true) {
			zoneActors.add(player);
		}
		player.setCollisioninterface(this);
	}

	public ArrayList<Actor> getActors() {
		return zoneActors;
	}

	@Override
	public boolean passable(int x, int y, boolean fly) {

		if (x < 0 || x >= zoneWidth) {
			return false;
		}
		if (y < 0 || y >= zoneHeight) {
			return false;
		}
		if (zoneTileGrid[x][y] == null) {
			return false;
		}
		if (getActor(x, y) != null && getActor(x,y).getAttackable()) {
			return false;
		}
		if (zoneTileGrid[x][y].getWidgetObject() != null) {
			if (zoneTileGrid[x][y].getWidgetObject().Walkable() == false) {
				return false;
			}
		}
		switch (zoneTileGrid[x][y].getDefinition().getMovement()) {
		case FLY:
			if (fly == true) {
				return true;
			}

		case BLOCK:
			return false;

		case WALK:
			return true;
		case SLOW:
			return true;
		}

		return false;
	}


	public ILosBoard getBoard(int index) {
		if (sightBoard[index]==null)
		{
			switch (index)
			{
			case 0:
				sightBoard[index]=new ZoneSight(zoneTileGrid,zoneWidth,zoneHeight,true);
				break;
			case 1:
				sightBoard[index]=new ZoneSight(zoneTileGrid,zoneWidth,zoneHeight,false);
				break;
			}
		}
		return sightBoard[index];
	}

	@Override
	public Path getPath(int x0, int y0, int x1, int y1, boolean fly, int length) {
		// TODO Auto-generated method stub
		if (m_astar == null) {
			m_astar = new Pathfinder(this);
		}

		return m_astar.findPath(new Vec2f(x0, y0), new Vec2f(x1, y1), length, fly);

	}

	Pathfinder m_astar;

	@Override
	public Tile getTile(int x, int y) {
		if (zoneTileGrid == null) {
			return null;
		}
		if (x >= 0 && x < zoneWidth) {
			if (y >= 0 && y < zoneHeight) {
				if (zoneTileGrid[x][y] != null) {
					return zoneTileGrid[x][y];
				}
				return null;
			}
		}
		return null;
	}

	public void RegenZone() {

		for (int i = 0; i < zoneWidth; i++) {
			for (int j = 0; j < zoneHeight; j++) {
				if (zoneTileGrid[i][j] != null) {
					if (zoneTileGrid[i][j].getWidgetObject() != null) {
						zoneTileGrid[i][j].getWidgetObject().Regen(GameManager.getClock(), this);
					}
					if (zoneTileGrid[i][j].getThreat()!=null)
					{
						zoneTileGrid[i][j].setThreat(null);
					}
					zoneTileGrid[i][j].setActorInTile(null);
				}

			}
		}

		for (int i = zoneActors.size() - 1; i >= 0; i--) {
			if (zoneActors.get(i) != null) {
				if (zoneActors.get(i).Respawn(GameManager.getClock())) {
					zoneActors.remove(i);
				}
				else
				{
					zoneActors.get(i).setPosition(new Vec2f((int)zoneActors.get(i).getPosition().x,(int)zoneActors.get(i).getPosition().y));
					if (getTile((int) zoneActors.get(i).getPosition().x, (int) zoneActors.get(i).getPosition().y) != null) {
						zoneTileGrid[(int) zoneActors.get(i).getPosition().x][(int) zoneActors.get(i).getPosition().y]
								.setActorInTile(zoneActors.get(i));
					}
				}
			}

		}


	}

	public void Save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, zoneName);

		// save whether its already loaded
		if (zoneTileGrid == null) {
			dstream.writeBoolean(false);
			if (preload != null) {
				dstream.writeBoolean(true);
				preload.save(dstream);
			} else {
				dstream.writeBoolean(false);
			}
		} else {
			dstream.writeBoolean(true);
			dstream.writeBoolean(isVisited);
			// save tilelibrary
			zoneTileLibrary.Save(dstream);
			// save tilesetname
			ParserHelper.SaveString(dstream, tilesetName);
			// save landinglocation
			if (fixedLandingSite != null) {
				dstream.writeBoolean(true);
				dstream.writeFloat(fixedLandingSite.x);
				dstream.writeFloat(fixedLandingSite.y);
			} else {
				dstream.writeBoolean(false);
			}
			// save width
			dstream.write(zoneWidth);
			// save height
			dstream.write(zoneHeight);
			// save tilegrid
			for (int i = 0; i < zoneWidth; i++) {
				for (int j = 0; j < zoneHeight; j++) {
					if (zoneTileGrid[i][j] != null) {
						dstream.writeBoolean(true);
						zoneTileGrid[i][j].Save(dstream);
					} else {
						dstream.writeBoolean(false);
					}

				}
			}
			// save actors
			dstream.write(zoneActors.size());
			if (zoneActors.size() > 0) {
				for (int i = 0; i < zoneActors.size(); i++) {
					if (zoneActors.get(i).canSave()) {
						dstream.writeBoolean(true);
						zoneActors.get(i).Save(dstream);
					} else {
						dstream.writeBoolean(false);
					}
				}
			}

			// adjacentzonenames
			for (int i = 0; i < 4; i++) {
				if (adjacentZoneNames[i] != null) {
					dstream.writeBoolean(true);
					ParserHelper.SaveString(dstream, adjacentZoneNames[i]);
				} else {
					dstream.writeBoolean(false);
				}
			}
		}

		if (zoneRules != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, zoneRules);
			dstream.writeInt(violationLevel);
		} else {
			dstream.writeBoolean(false);
		}
		if (zoneConditions!=null)
		{
			dstream.writeBoolean(true);
			zoneConditions.save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	public void load(DataInputStream dstream) throws IOException {
		boolean load = dstream.readBoolean();

		if (load == true) {
			isVisited = dstream.readBoolean();
			// load tilelibrary
			zoneTileLibrary = new TileDefLibrary(dstream);
			// load tilesetname
			tilesetName = ParserHelper.LoadString(dstream);
			// load fixed landing site
			boolean c = dstream.readBoolean();
			if (c == true) {
				fixedLandingSite = new Vec2f(dstream.readFloat(), dstream.readFloat());
			}

			// load width
			zoneWidth = dstream.read();
			// load height
			zoneHeight = dstream.read();
			// load tilegrid
			zoneTileGrid = new Tile[zoneWidth][];
			for (int i = 0; i < zoneWidth; i++) {
				zoneTileGrid[i] = new Tile[zoneHeight];
				for (int j = 0; j < zoneHeight; j++) {
					boolean b = dstream.readBoolean();
					if (b == true) {
						zoneTileGrid[i][j] = new Tile(i, j, null, this, zoneTileLibrary);
						zoneTileGrid[i][j].Load(dstream, zoneTileLibrary);
					}

				}
			}
			// load actor count
			int count = dstream.read();
			// load actors
			zoneActors = new ArrayList<Actor>();
			for (int i = 0; i < count; i++) {
				boolean b = dstream.readBoolean();
				if (b == true) {
					Actor a = ActorLoader.loadActor(dstream);

					if (a != null) {
						a.setCollisioninterface(this);
						zoneActors.add(a);
					}
				}
			}
			if (zoneActors.size() < count) {
				count = zoneActors.size();
			}
			for (int i = 0; i < count; i++) {
				if (zoneActors.get(i) != null) {
					zoneActors.get(i).linkActors(this);
				}
			}

			// adjacentzonenames
			for (int i = 0; i < 4; i++) {
				boolean l = dstream.readBoolean();
				if (l == true) {
					adjacentZoneNames[i] = ParserHelper.LoadString(dstream);
				} else {
					adjacentZoneNames[i] = null;
				}
			}
		} else {
			if (dstream.readBoolean()) {
				preload = new ZonePreloadController();
				preload.load(dstream);
			}
		}

		if (dstream.readBoolean()) {
			zoneRules = ParserHelper.LoadString(dstream);
			violationLevel = dstream.readInt();
		}
		if (dstream.readBoolean())
		{
			zoneConditions=new EnvironmentalConditions(dstream);

		}
	}

	public Entity getZoneEntity() {
		return zoneEntity;
	}

	public zoneType getType() {
		return type;
	}

	public Vec2f getWidgetPosition(Widget widget) {
		for (int i = 0; i < zoneWidth; i++) {
			for (int j = 0; j < zoneHeight; j++) {
				if (zoneTileGrid[i][j] != null) {
					if (zoneTileGrid[i][j].getWidgetObject() == widget) {
						return new Vec2f(i, j);
					}
				}
			}
		}

		return null;
	}

	public Vec2f getEmptyTileNearP(Vec2f position) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++) {
			Vec2f p = ZoneInteractionHandler.getPos(i, position);
			Tile t = getTile((int) p.x, (int) p.y);
			if (t != null && t.getDefinition().getMovement() == TileMovement.WALK && t.getWidgetObject() == null
					&& t.getActorInTile() == null) {
				return p;
			}
		}
		return null;
	}

	@Override
	public void removeThreat(int x, int y, Actor npc) {
		for (int i=0;i<8;i++)
		{
			Vec2i p=Geometry.getPos(i, x, y);
			if (contains(p.x,p.y))
			{
				if (zoneTileGrid[p.x][p.y]!=null && zoneTileGrid[p.x][p.y].getThreat()==npc)
				{
					zoneTileGrid[p.x][p.y].setThreat(null);
				}
			}
		}
	}

	@Override
	public void addThreat(int x, int y, Actor npc) {
		for (int i=0;i<8;i++)
		{
			Vec2i p=Geometry.getPos(i, x, y);
			if (contains(p.x,p.y))
			{
				if (zoneTileGrid[p.x][p.y]!=null && zoneTileGrid[p.x][p.y].getThreat()==null)
				{
					zoneTileGrid[p.x][p.y].setThreat(npc);
				}
			}
		}
	}

	@Override
	public float getMovementMultiplier() {
		if (this.zoneConditions!=null)
		{
			return this.zoneConditions.getMovementMultiplier();
		}
		return 1;
	}

	@Override
	public void updateZoneEnvironment(Player player) {
		if (this.zoneConditions!=null)
		{
			this.zoneConditions.update(player);
		}
	}

	@Override
	public float getVisionMultiplier() {
		if (this.zoneConditions!=null)
		{
			return this.zoneConditions.getVisionMultiplier();
		}
		return 1;
	}

	public void update() {
		if (this.zoneConditions!=null)
		{
			this.zoneConditions.run(Universe.getInstance().getPlayer());
		}
	}

	public EnvironmentalConditions getZoneConditions() {
		return zoneConditions;
	}


}
