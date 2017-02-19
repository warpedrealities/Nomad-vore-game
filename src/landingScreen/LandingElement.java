package landingScreen;

public class LandingElement {

	enum LandingClass{LC_OPEN,LC_SELECTED,LC_OCCUPIED,LC_INVALID};
	
	LandingClass landingType;
	int xPosition, yPosition;
	
	public LandingElement(LandingClass type, int x, int y)
	{
		xPosition=x;
		yPosition=y;
		landingType=type;
	}
	
	public LandingClass getLandingType() {
		return landingType;
	}
	public void setLandingType(LandingClass landingType) {
		this.landingType = landingType;
	}
	public int getxPosition() {
		return xPosition;
	}
	public int getyPosition() {
		return yPosition;
	}
	
	
	
	
}
