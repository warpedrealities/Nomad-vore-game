package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;
import view.ViewScene;
import view.ModelController_Int;

public class WidgetDescription extends Widget{

	boolean m_fired;
	String m_description;
	
	public WidgetDescription(String description)
	{
		isWalkable=true;
		isVisionBlocking=false;
		m_description=description;
		m_fired=false;
	}
	


	@Override
	public boolean Visit()
	{
		if (m_fired==false)
		{
			ViewScene.m_interface.DrawText(m_description);
			m_fired=true;
		}
		return false;
	}

	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(4);
		commonSave(dstream);
		dstream.writeBoolean(m_fired);
		ParserHelper.SaveString(dstream, m_description);
	}
	
	public WidgetDescription(DataInputStream dstream)  throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		m_fired=dstream.readBoolean();
		m_description=ParserHelper.LoadString(dstream);
	}
}
