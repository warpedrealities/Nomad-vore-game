package input;

import org.lwjgl.glfw.GLFWScrollCallbackI;

public class ScrollHandler implements GLFWScrollCallbackI {

	private double scrollY;
	
	public ScrollHandler()
	{
		scrollY=0;
	}
	
	@Override
	public void invoke(long arg0, double scrollX, double scrollY) {

		this.scrollY+=scrollY;
	}

	public boolean isScrolled() {
		if (scrollY!=0)
		{
			return true;
		}
		return false;
	}

	public double getScroll() {

		return scrollY;
	}

	public void setScroll(float value) {
		scrollY=value;
	}

}
