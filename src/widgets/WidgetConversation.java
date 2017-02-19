package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;
import view.ViewScene;

import actor.Player;
import nomad.FlagField;

public class WidgetConversation extends WidgetBreakable {

	String conversationFileName;
	FlagField localFlags;
	
	public WidgetConversation(Element node)
	{
		
		super(node);
		localFlags=new FlagField();

	}
	
	
	
	public void setConversationFileName(String conversationFileName) {
		this.conversationFileName = conversationFileName;
	}

	public void setSprite(int sprite)
	{
		widgetSpriteNumber=sprite;
	}

	public WidgetConversation(DataInputStream dstream) throws IOException {
		commonLoad(dstream);
		load(dstream);
		conversationFileName=ParserHelper.LoadString(dstream);
		localFlags=new FlagField();
		localFlags.load(dstream);
	}
	
	@Override
	public boolean safeOnly()
	{
		return true;
	}
	
	
	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		dstream.write(13);
		commonSave(dstream);
		super.saveBreakable(dstream);
		ParserHelper.SaveString(dstream, conversationFileName);
		localFlags.save(dstream);
	}
	
	@Override
	public boolean Interact(Player player)
	{
	
		ViewScene.m_interface.StartConversation(conversationFileName, this);
		
		return false;
	}



	public FlagField getFlags() {
		return localFlags;
	}
	
}
