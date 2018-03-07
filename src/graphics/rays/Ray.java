package graphics.rays;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector4f;

import rendering.SquareRenderer;
import rendering.Square_Int;
import shared.Vec2f;

public class Ray implements Square_Int {

	private SquareRenderer [] lineMarkers;
	private int length;
	private Vec2f position;
	private Vec2f endPoint;
	public Ray(int index, Vec2f position, Vector4f colour) {
		length=0;
		lineMarkers=new SquareRenderer[12];
		for (int i=0;i<12;i++)
		{
			lineMarkers[i]=new SquareRenderer(254, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1),0.5F);			
		}
		this.position=position;
	}

	@Override
	public void reposition(Vec2f p) {
		position=p;
	}

	@Override
	public void repositionF(Vec2f p) {
		if (p!=null && endPoint!=null)
		{
			generate();		
		}
		endPoint=p;
		if (p==null)
		{
			length=0;
		}
	}

	private void generate()
	{
		length=(int)endPoint.getDistance(0,0);
		Vec2f interval=endPoint.replicate(); 
		interval.normalize();
		Vec2f p=position.replicate();
		for (int i=0;i<length;i++)
		{
			p.x+=interval.x;
			p.y+=interval.y;
			lineMarkers[i].reposition(new Vec2f(p.x+0.25F,p.y+0.25F));
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlashing(int bool) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setImage(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColour(float r, float g, float b) {
		for (int i=0;i<lineMarkers.length;i++)
		{
			lineMarkers[i].setColour(r, g, b);
		}
	}

	@Override
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		for (int i=0;i<length;i++)
		{
			lineMarkers[i].draw(objmatrix, tintvar, matrix44fbuffer);
		}
	}

	@Override
	public void discard() {
		for (int i = 0; i < lineMarkers.length; i++) {
			lineMarkers[i].discard();
		}
	}

}
