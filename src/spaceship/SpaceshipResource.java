package spaceship;

public class SpaceshipResource {

	private String resourceName;
	private float resourceAmount;
	private float resourceCap;
	
	public SpaceshipResource(String name, float amount, float cap)
	{
		resourceAmount=amount;
		resourceCap=cap;
		resourceName=name;
		
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
	
	public void subtractResourceAmount(float resourceMod)
	{
		this.resourceAmount-=resourceMod;
	}
	
}
