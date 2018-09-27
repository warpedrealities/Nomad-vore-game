package menu.preferenceControls;

public class MasterListPref {

	private String preference;
	private int count;
	
	public MasterListPref(String preference)
	{
		this.preference=preference;
		count=1;
	}
	
	public MasterListPref(String preference, int count) {
		this.preference=preference;
		this.count=count;
	}

	public void incrementCount()
	{
		count++;
	}
	
	public String getPreference()
	{
		return preference;
	}
	
	public int getCount()
	{
		return count;
	}
	
}
