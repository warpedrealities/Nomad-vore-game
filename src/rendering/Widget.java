package rendering;

import java.nio.FloatBuffer;

public interface Widget {

	public void Draw(FloatBuffer matrix44Buffer, int matrixloc, int colourloc);

	public void Discard();

	public void setValue(int value);

}
