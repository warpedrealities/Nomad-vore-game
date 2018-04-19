package zone.Environment;

public class EnvironmentModifiers {

	public float movementModifier;
	public float visionModifier;

	public EnvironmentModifiers()
	{
		reset();
	}
	
	public void reset()
	{
		movementModifier=1;
		visionModifier=1;
	}
}
