package actor;

public class ThreatAssessment {

	private int threat;
	
	
	public void reset()
	{
		threat=0;
	}
	
	public void update() {
		if (threat>0)
		{
			threat--;
		}
	}

	public void addThreat(int threat)
	{
		this.threat+=threat;
	}
	
	public int getThreat()
	{
		return threat;
	}
	
	
}
