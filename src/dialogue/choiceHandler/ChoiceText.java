package dialogue.choiceHandler;

public class ChoiceText {
	public enum DisplayType{NONE,NARRATOR,SELF,NPC};
	DisplayType displayType;
	String text;
	
	public ChoiceText(String text)
	{
		this.text=setDisplayType(text);
		
	}
	
	
	
	public DisplayType getDisplayType() {
		return displayType;
	}



	public String getText() {
		return text;
	}

	private String setDisplayType(String string)
	{
		displayType=DisplayType.NONE;
		if (string.charAt(0)=='$')
		{
			displayType=DisplayType.SELF;
			string=string.substring(1);
		}		
		if (string.charAt(0)=='#')
		{
			displayType=DisplayType.NPC;
			string=string.substring(1);
		}	
		if (string.charAt(0)=='!')
		{
			displayType=DisplayType.NARRATOR;
			string=string.substring(1);
		}		
		return string;
	}
}
