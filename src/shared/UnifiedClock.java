package shared;

public class UnifiedClock {

	
	//a common class for both server and client to have the same timekeeping system

	double m_clock;
	
	public UnifiedClock()
	{
		m_clock=0;
	}
	
	public void SyncClock(int timestamp)
	{
		m_clock=timestamp/60;


	}
	
	public int getTimestamp()
	{
		return (int) (m_clock*60);
	}
	
	public void Update(float dt)
	{
		m_clock+=dt;
	}
	
	public boolean Double(int timestamp)
	{
		if ((m_clock*60)>timestamp+2)
		{
			return true;
		}
		return false;
	}
	
}
