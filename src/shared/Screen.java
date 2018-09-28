package shared;

import java.nio.FloatBuffer;

import input.MouseHook;

public abstract class Screen implements MyListener {

	public abstract void update(float DT);

	public abstract void draw(FloatBuffer buffer, int matrixloc);

	public abstract void discard(MouseHook mouse);

	@Override
	public abstract void ButtonCallback(int ID, Vec2f p);

	public abstract void start(MouseHook hook);

	public void initialize(int[] textures, Callback callback) {
		// TODO Auto-generated method stub

	}
}
