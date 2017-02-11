package rendering;

public class Colour {

	public byte[] m_channels;
	
	public Colour(byte r, byte g, byte b)
	{
		m_channels=new byte[3];
		m_channels[0]=r;
		m_channels[1]=g;
		m_channels[2]=b;
	}
}
