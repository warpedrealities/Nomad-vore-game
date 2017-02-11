package input;

import java.util.ArrayDeque;

import org.lwjgl.glfw.GLFW;

public class Keyboard extends org.lwjgl.glfw.GLFWCharCallback {

	static private long openWindow;
	static private Keyboard instance;
	private ArrayDeque <Integer> charQueue;
	private boolean readingChars;
	
	private Keyboard()
	{
		charQueue=new ArrayDeque<Integer>();
	}
	
	static public void setWindow(long window)
	{
		openWindow=window;
	}
	
	public static boolean isKeyDown(int key) {

		if (GLFW.glfwGetKey(openWindow, key)>=1)
		{
			return true;
		}
		return false;
	}

	@Override
	public void invoke(long window, int codepoint) {
		if (readingChars==true)
		{
			charQueue.add(codepoint);
		}
	}

	public void enableCharInput()
	{
		charQueue.clear();
		readingChars=true;
	}
	
	public void disableCharInput()
	{
		charQueue.clear();
		readingChars=false;		
	}
	
	public int readChar()
	{
		if (charQueue.size()>0)
		{
			return charQueue.poll();
		}
		return 0;
	}
	
	public boolean next()
	{
		if (charQueue.size()>0)
		{
			return true;
		}
		return false;
	}
	public static Keyboard getInstance() {
		// TODO Auto-generated method stub
		if (instance==null)
		{
			instance=new Keyboard();
		}
		
		return instance;
	}

}
