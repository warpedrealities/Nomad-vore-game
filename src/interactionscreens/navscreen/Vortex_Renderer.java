package interactionscreens.navscreen;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import rendering.Renderable;
import rendering.Sprite;
import rendering.SpriteBatch;
import shared.Vec2f;

public class Vortex_Renderer {

	private Vec2f position;
	private Vec2f size;
	private Vortex_View view;
	private Vortex_Rendering_Engine engine;
	public Vortex_Renderer(Vec2f position, Vec2f size) {
		this.size=size;
		this.position=position;
		this.view=new Vortex_View(position,size);
		this.engine=new Vortex_Rendering_Engine();
	}

	public void update(float dt)
	{

	}
	
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		this.engine.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine.getTexture());
		view.draw(objmatrix,tintvar,matrix44fbuffer);
	}


	public void discard() {
		// TODO Auto-generated method stub
		view.discard();
		engine.discard();
	}

}
