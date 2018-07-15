package interactionscreens.navscreen;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import shared.SceneManager;
import vmo.Game;

public class Vortex_Rendering_Engine {

	private int textureID;
	private int frameBuffer;
	private int depthBuffer;
	
	public Vortex_Rendering_Engine()
	{
		buildTexture();
		
	}

	private void buildTexture()
	{
		textureID=GL11.glGenTextures();
		GL13.glActiveTexture(1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);	
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 512, 512, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				0);

		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);               // make it linear filterd

		//texture setup complete
		
		//setup colour buffer
		frameBuffer= EXTFramebufferObject.glGenFramebuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT , frameBuffer);	
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);          
		
		EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, 
				EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, 
				GL11.GL_TEXTURE_2D, textureID, 0);
	
		//setup depth buffer
		depthBuffer= EXTFramebufferObject.glGenFramebuffersEXT();	
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
				depthBuffer);
	    GL30.glRenderbufferStorage(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
	    		GL14.GL_DEPTH_COMPONENT24, 480, 320); // get the data space for it
	    EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
	    		EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
	    		EXTFramebufferObject.GL_RENDERBUFFER_EXT, 
	    		depthBuffer); // bind it to the renderbuffer
  
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);     

	}
	
	public int getTexture()
	{
		return textureID;
	}
	
	public void update(float dt) {
		// TODO Auto-generated method stub
	                         // set The Current Viewport to the fbo size
	
	}

	public void discard() {
		GL15.glDeleteBuffers(frameBuffer);
		GL15.glDeleteBuffers(depthBuffer);
		GL11.glDeleteTextures(textureID);

	}

	public void draw() {

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);                                // unlink textures because if we dont it all is gonna fail
        EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, frameBuffer);        // switch to rendering on our FBO
        GL11.glViewport (0, 0, 480, 320);               
        GL11.glClearColor (1.0f, 0.0f, 0.0f, 0.5f);
        GL11.glClear (GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 
        
        EXTFramebufferObject.glBindFramebufferEXT(GL30.GL_FRAMEBUFFER, 0);  
		int WIDTH = (int) (1280 * Game.sceneManager.getConfig().getScale());
		int HEIGHT = (int) (1024 * Game.sceneManager.getConfig().getScale());
		 GL11.glViewport (0, 0, WIDTH, HEIGHT);        
	        GL11.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
	    	
		
	}
	
}
