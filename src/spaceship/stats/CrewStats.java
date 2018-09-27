package spaceship.stats;

import actor.npc.Crew;

public class CrewStats {

	private int navigation,gunnery, engineer, technician;
	
	public CrewStats()
	{
		
	}
	
	public CrewStats(int nav,int gun, int eng, int tec)
	{
		navigation=nav;
		gunnery=gun;
		engineer=eng;
		technician=tec;
	}

	public int getNavigation() {
		return navigation;
	}

	public int getGunnery() {
		return gunnery;
	}

	public int getEngineer() {
		return engineer;
	}

	public int getTechnician() {
		return technician;
	}

	public void setNavigation(int navigation) {
		this.navigation = navigation;
	}

	public void setGunnery(int gunnery) {
		this.gunnery = gunnery;
	}

	public void setEngineer(int engineer) {
		this.engineer = engineer;
	}

	public void setTechnician(int technician) {
		this.technician = technician;
	}
	
	public void addCrewSkill(Crew crew)
	{
		switch (crew.getSkill())
		{
		case NAVIGATION:
			if (navigation<crew.getRank())
			{
				navigation=crew.getRank();
			}
			break;
		case GUNNERY:
			if (gunnery<crew.getRank())
			{
				gunnery=crew.getRank();
			}
			break;		
		case ENGINEER:
			if (engineer<crew.getRank())
			{
				engineer=crew.getRank();
			}
			break;
		case TECHNICIAN:
			if (technician<crew.getRank())
			{
				technician=crew.getRank();
			}
			break;	
		}
	}
	
	
}
