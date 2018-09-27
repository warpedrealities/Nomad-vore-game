package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class WidgetSprite extends Widget {

	String m_sprite;
	int m_width, m_height;

	public WidgetSprite(String sprite, int width, int height) {
		isVisionBlocking = false;
		isWalkable = true;
		m_height = height;
		m_width = width;
		m_sprite = sprite;
	}

	public int getWidth() {
		return m_width;
	}

	public int getheight() {
		return m_height;
	}

	public String getImage() {
		return m_sprite;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(0);
		commonSave(dstream);
		ParserHelper.SaveString(dstream, m_sprite);
		dstream.writeInt(m_width);
		dstream.writeInt(m_height);
	}

	public WidgetSprite(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		m_sprite = ParserHelper.LoadString(dstream);
		m_width = dstream.readInt();
		m_height = dstream.readInt();
	}

}
