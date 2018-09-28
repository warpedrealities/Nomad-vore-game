package input;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

import java.util.Vector;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import shared.Clickable;
import shared.Vec2f;
import vmo.Game;

public class MouseHook extends GLFWCursorPosCallback {

	static private MouseHook instance;
	private Vector<Clickable> m_clickables;
	private boolean m_clickenabled;
	private Vec2f m_position;
	private Vec2f lastPosition;
	private Vec2f mouseDelta;
	private long openGLWindow;
	private ScrollHandler scrollHandler;
	
	static public MouseHook getInstance() {
		return instance;
	}

	public MouseHook(long openGLWindow) {
		instance = this;
		this.openGLWindow = openGLWindow;
		m_clickenabled = true;

		m_clickables = new Vector<Clickable>();
		// create mouse

		m_position = new Vec2f(0, 0);
		mouseDelta = new Vec2f(0, 0);
		lastPosition = new Vec2f(0, 0);
		scrollHandler=new ScrollHandler();
	}

	public Vec2f getPosition() {
		return m_position;
	}

	public Vec2f getMouseDelta() {
		return mouseDelta;
	}

	public void Register(Clickable click) {
		m_clickables.add(click);
	}

	public void Disable() {
		m_clickenabled = false;
	}

	public void Remove(Clickable click) {
		m_clickables.remove(click);
	}

	public void Clean() {
		m_clickables.clear();
	}

	public void Discard() {

	}

	public boolean click() {
		if (glfwGetMouseButton(openGLWindow, GLFW_MOUSE_BUTTON_1) == 1) {
			return true;
		}
		return false;
	}

	public void update() {
		mouseDelta.x = m_position.x - lastPosition.x;
		mouseDelta.y = m_position.y - lastPosition.y;
		lastPosition.x = m_position.x;
		lastPosition.y = m_position.y;

		if (glfwGetMouseButton(openGLWindow, GLFW_MOUSE_BUTTON_1) == 1) {
			if (m_clickenabled == true) {

				for (int i = 0; i < m_clickables.size(); i++) {
					if (m_clickables.get(i).ClickEvent(m_position)) {
						break;
					}
				}
				m_clickenabled = false;
			}
		} else {
			m_clickenabled = true;
		}
		if (scrollHandler.isScrolled())
		{
			for (int i = 0; i < m_clickables.size(); i++) {
				if (m_clickables.get(i).scrollEvent(m_position,scrollHandler.getScroll())) {
					scrollHandler.setScroll(0);
					break;
				}
			}		
		}
	}

	public boolean IsEnabled() {
		return m_clickenabled;
	}

	@Override
	public void invoke(long screen, double xpos, double ypos) {
		float x = ((float) xpos - (640 * Game.sceneManager.getConfig().getScale()))
				/ (32 * Game.sceneManager.getConfig().getScale());
		float y = ((float) ypos - (512 * Game.sceneManager.getConfig().getScale()*Game.sceneManager.getConfig().getyInputScale()))
				/ (32 * Game.sceneManager.getConfig().getScale()*Game.sceneManager.getConfig().getyInputScale()) * -1;

		m_position.x = x;
		m_position.y = y;

	}

	public boolean buttonDown() {
		if (glfwGetMouseButton(openGLWindow, GLFW_MOUSE_BUTTON_1) == 1) {
			return true;
		}
		return false;
	}

	public ScrollHandler getScroll() {
		return scrollHandler;
	}

}
