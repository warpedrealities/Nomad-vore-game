package gui;

import input.Keyboard;
import input.MouseHook;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.glfw.GLFW;

import org.lwjgl.opengl.GL20;

import font.NuFont;
import shared.ConcertinaPatch;
import shared.NinePatch;
import shared.Vec2f;
import vmo.Game;

public class TextView extends GUIBase {

	private ConcertinaPatch backgroundPatches[];
	private int tintControl;
	private Vec2f viewPosition, viewSize;
	private NuFont textLines[];
	private boolean isExpanded;
	private int textScrollOffset;
	private ArrayList<String> displayStrings;
	private String lastInput;
	private float keyTimer;
	private int highlightedLine;

	public TextView(int background, Vec2f pos, Vec2f size, int colour) {
		tintControl = colour;
		keyTimer = 0;
		isExpanded = false;
		textScrollOffset = 0;
		backgroundPatches = new ConcertinaPatch[2];
		displayStrings = new ArrayList<String>();
		backgroundPatches[0] = new ConcertinaPatch(pos, size.x, size.y,0.9F, background);
		Vec2f p = new Vec2f(pos.x + 0, pos.y);
		backgroundPatches[1] = new ConcertinaPatch(p, size.x, size.y * 2,0.9F, background);
		textLines = new NuFont[42];
		for (int i = 0; i < 42; i++) {
			// build fonts
			textLines[i] = new NuFont(new Vec2f(pos.x + 0.5F, pos.y + (i * 0.9F) + 1.5F), 256, 0.8F);
		}
		// insert dummy data
		// DummyData();
		BuildStrings();
	}

	public TextView(int background, Vec2f pos, Vec2f size, int colour, ArrayList<String> strings) {
		tintControl = colour;
		keyTimer = 0;
		isExpanded = false;
		textScrollOffset = 0;
		backgroundPatches = new ConcertinaPatch[2];
		displayStrings = strings;
		backgroundPatches[0] = new ConcertinaPatch(pos, size.x, size.y,0.9F, background);
		Vec2f p = new Vec2f(pos.x + 0, pos.y);
		backgroundPatches[1] = new ConcertinaPatch(p, size.x, size.y * 2,0.9F, background);
		textLines = new NuFont[42];
		for (int i = 0; i < 42; i++) {
			// build fonts
			textLines[i] = new NuFont(new Vec2f(pos.x + 0.5F, pos.y + (i * 0.9F) + 1.5F), 256, 0.8F);
		}
		// insert dummy data
		// DummyData();
		BuildStrings();
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	void DummyData() {
		for (int i = 0; i < 8; i++) {
			AddText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
		}
	}

	public void BuildStrings() {
		int index = displayStrings.size() - 1 - textScrollOffset;
		for (int i = 0; i < 42; i++) {
			if (index < displayStrings.size() && index >= 0) {
				textLines[i].setString(displayStrings.get(index));
			} else {
				textLines[i].setString("");
			}
			index--;
		}
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_HOME)) {
			isExpanded = true;
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_END)) {
			isExpanded = false;
		}
		if (keyTimer <= 0) {
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_PAGE_UP)) {
				if (textScrollOffset < displayStrings.size() - 16) {
					textScrollOffset++;
					BuildStrings();
				}

				keyTimer = 0.2F;
			}
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN)) {
				if (textScrollOffset > 0) {
					textScrollOffset--;
					BuildStrings();
				}

				keyTimer = 0.2F;
			}
		}
		if (MouseHook.getInstance().getScroll().isScrolled())
		{
			textScrollOffset+=MouseHook.getInstance().getScroll().getScroll();
			if (textScrollOffset > displayStrings.size() - 16) {
				textScrollOffset=displayStrings.size() - 16;;
			}
			if (textScrollOffset < 0) {
				textScrollOffset=0;
			
			}
			
			BuildStrings();
			MouseHook.getInstance().getScroll().setScroll(0);
		}
		if (keyTimer > 0) {
			keyTimer -= DT;
		}

	}

	public void AddText(String text) {
		if (text == null) {
			return;
		}
		lastInput = text;
		highlightedLine = 0;
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(text);
		int lengthlimit = (int) (125.0F / Game.sceneManager.getConfig().getTextWidth());
		while (scanner.hasNext()) {
			String str = scanner.next();
			if (builder.length()+str.length() > lengthlimit) {
				displayStrings.add(builder.toString());
				builder = new StringBuilder();
				highlightedLine++;
			}
			if (str.indexOf("LBREAK") != -1) {
				if (str.length() > "LBREAK".length()) {
					str = str.replace("LBREAK", "");
					builder.append(str);
					builder.append(" ");
				} 
					displayStrings.add(builder.toString());
					builder = new StringBuilder();
					highlightedLine++;				
			} 
			else 
			{
				builder.append(str);
				builder.append(" ");
			}

		}
		displayStrings.add(builder.toString());
		highlightedLine++;
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		// TODO Auto-generated method stub
		int v = highlightedLine - textScrollOffset;
		GL20.glUniform4f(tintControl, 1, 1, 1, 1);
		if (isExpanded) {
			backgroundPatches[1].Draw(buffer, matrixloc);
			if (v > 0) {
				GL20.glUniform4f(tintControl, 1, 1, 0.8F, 1);
			} else {
				GL20.glUniform4f(tintControl, 0.8F, 0.8F, 1.0F, 1);
			}
			for (int i = 0; i < 32; i++) {
				if (v == i) {
					GL20.glUniform4f(tintControl, 0.8F, 0.8F, 1.0F, 1);
				}
				textLines[i].Draw(buffer, matrixloc);
			}
		} else {
			backgroundPatches[0].Draw(buffer, matrixloc);
			if (v > 0) {
				GL20.glUniform4f(tintControl, 1, 1, 0.8F, 1);
			} else {
				GL20.glUniform4f(tintControl, 0.8F, 0.8F, 1.0F, 1);
			}
			for (int i = 0; i < 16; i++) {
				if (v == i) {

					GL20.glUniform4f(tintControl, 0.8F, 0.8F, 1.0F, 1);
				}
				textLines[i].Draw(buffer, matrixloc);
			}
		}
		GL20.glUniform4f(tintControl, 1, 1, 1, 1);

	}

	@Override
	public void discard() {

		for (int i = 0; i < textLines.length; i++) {
			textLines[i].Discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub

	}

	public ArrayList<String> getDisplayStrings() {
		return displayStrings;
	}

	public String getLastInput() {
		return lastInput;
	}

	public void AddTexts(ArrayList<String> strings) {
		for (int i=0;i<strings.size();i++)
		{
			AddText(strings.get(i));
		}
	}

}
