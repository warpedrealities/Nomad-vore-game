package shared;

public interface Clickable {

	public boolean ClickEvent(Vec2f pos);

	public default boolean scrollEvent(Vec2f m_position, double scroll)
	{
		return false;
	}
}
