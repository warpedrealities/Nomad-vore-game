package spaceship;

public class SpaceshipResource {

	private String resourceName;
	private float resourceAmount;
	private float resourceCap;
	private boolean nonCombat;
	public SpaceshipResource(String name, float amount, float cap,boolean nonCombat) {
		resourceAmount = amount;
		resourceCap = cap;
		resourceName = name;
		this.nonCombat=nonCombat;

	}

	public float getResourceAmount() {
		return resourceAmount;
	}

	public void setResourceAmount(float resourceAmount) {
		this.resourceAmount = resourceAmount;
	}

	public float getResourceCap() {
		return resourceCap;
	}

	public void setResourceCap(float resourceCap) {
		this.resourceCap = resourceCap;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void subtractResourceAmount(float resourceMod) {
		this.resourceAmount -= resourceMod;
	}

	public boolean isNonCombat() {
		return nonCombat;
	}

}
