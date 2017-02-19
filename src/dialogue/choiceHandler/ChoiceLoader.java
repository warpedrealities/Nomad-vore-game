package dialogue.choiceHandler;

public class ChoiceLoader {

	int count;
	String choices[];
	
	public ChoiceLoader()
	{
		count=0;
		choices=new String[8];
	}
	
	public void reset()
	{
		count=0;
	}
	
	public void addString(String string)
	{
		if (count<7)
		{
			choices[count]=string;
			count++;			
		}
	}
	
	public int getCount()
	{
		return count;
	}
	
	public String[] getChoices()
	{
		return choices;
	}
}
