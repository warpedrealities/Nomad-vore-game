package vmo;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Matrix4f;

import font.FontSupport;

public class Config {

	private float m_scale,yScale=1,yInputScale=1;
	private float m_textscale;
	private boolean verboseCombat;
	private boolean disableAutosave;
	protected Matrix4f matrix;
	public static final int VERSION = 311;

	public Config() {
		// open config file
		// read
		FileReader FR;
		BufferedReader BR;
		try {
			FR = new FileReader("assets/config.txt");
			BR = new BufferedReader(FR);
			String str;
			try {
				while (true) {
					str = BR.readLine();

					if (str != null) {
						if (str.contains("screenscale")) {
							String input = BR.readLine();
							m_scale = Float.parseFloat(input);
						}
						if (str.contains("textscale")) {
							String input = BR.readLine();
							m_textscale = Float.parseFloat(input);
						}
						if (str.contains("yScale")) {
							String input = BR.readLine();
							yScale = Float.parseFloat(input);
						}
						if (str.contains("yInputScale")) {
							String input = BR.readLine();
							yInputScale = Float.parseFloat(input);
						}
					} else {
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		float px = Toolkit.getDefaultToolkit().getScreenSize().height;
		if (px - 40 < (1024F * m_scale)) {
			m_scale = ((px - 40) / 1024);
		}
		matrix = new Matrix4f();
		matrix.m00 = 0.05F;
		matrix.m11 = 0.0625F*yScale;
		matrix.m22 = 1.0F;
		matrix.m33 = 1.0F;
		matrix.m31 = 0;
		matrix.m32 = 0;
	}

	public Matrix4f getMatrix()
	{
		Matrix4f r=new Matrix4f();
		Matrix4f.load(matrix, r);
		return r;
	}

	public float getScale() {
		return m_scale;
	}

	public float getTextscale() {
		return m_textscale;
	}
	public float getTextWidth() {
		return m_textscale*FontSupport.getInstance().getWidthAdjustment();
	}
	public boolean isVerboseCombat() {
		return verboseCombat;
	}

	public void setVerboseCombat(boolean verboseCombat) {
		this.verboseCombat = verboseCombat;
	}

	public boolean isDisableAutosave() {
		return disableAutosave;
	}

	public void setDisableAutosave(boolean disableAutosave) {
		this.disableAutosave = disableAutosave;
	}

	public float getyInputScale() {
		return yInputScale;
	}

}
