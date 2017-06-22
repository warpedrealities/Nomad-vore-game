package shared;

import input.MouseHook;

public interface Scene {

	// use arrays to pass ints to the scenes
	// 0 is tint variable
	// 1 view matrix
	// 2 object matrix

	public void Update(float dt);

	public void Draw();

	public void start(MouseHook mouse);

	public void end();

}
