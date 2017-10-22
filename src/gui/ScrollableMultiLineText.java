package gui;

import java.nio.FloatBuffer;
import java.util.Scanner;

import font.NuFont;
import gui.subelements.Slider;
import shared.Callback;
import shared.Vec2f;
import vmo.Game;

public class ScrollableMultiLineText extends GUIBase implements Callback {

	private NuFont m_fonts[];
	private String strings[];
	int m_length;
	private int offset;
	private Slider scrollbar;

	public ScrollableMultiLineText(Vec2f p, int lines, int length, float lwidth,Vec2f offset) {
		m_length = (int) ((int) length / (Game.sceneManager.getConfig().getTextscale()));
		m_fonts = new NuFont[lines];
		strings=new String[lines*2];
		for (int i = 0; i < lines; i++) {
			m_fonts[i] = new NuFont(new Vec2f(p.x, p.y - ((float) i * (lwidth + 0.1F))), length, lwidth);
			m_fonts[i].setString("");
		}
		scrollbar=new Slider(offset, lines*0.3F, this);
		scrollbar.setNotches(lines);
	}

	@Override
	public void update(float DT) {
		scrollbar.update(DT);
	}

	public void addText(String text) {
		dirtSimpleAddText(text);
	}

	private void dirtSimpleAddText(String text) {
		clean();
		Scanner scanner = new Scanner(text);
		int index = 0;
		StringBuilder builder = new StringBuilder();
		int l = 0;
		int count=0;
		while (scanner.hasNext() && index < m_fonts.length) {
			String str = scanner.next();
			if (str.equals("LBREAK")) {
				m_fonts[index].setString(builder.toString());
				builder = new StringBuilder();
				index++;
				l = 0;
			} else {
				if (str.length() + l > m_length * 0.8F) {
					l = 0;
					strings[index]=builder.toString();
					count++;
					index++;
					builder = new StringBuilder();
					builder.append(str);
					builder.append(" ");
					l += str.length();
				} else {
					builder.append(str);
					builder.append(" ");
					l += str.length();
				}

			}
		}
		if (index < strings.length) {
			strings[index]=builder.toString();
			count++;
		}
		scanner.close();
		scrollbar.setNotches(count);
		genFonts();
	}
	
	private void genFonts()
	{
		for (int i=offset;i<offset+m_fonts.length;i++)
		{
			if (strings[i]!=null)
			{
				m_fonts[i-offset].setString(strings[i]);	
			}
			else
			{
				m_fonts[i-offset].setString("");
			}

		}
	}

	public void clean() {
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].setString("");
		}
		for (int i=0;i<strings.length;i++)
		{
			strings[i]="";
		}
		offset=0;
		scrollbar.setIndex(0);
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].Draw(buffer, matrixloc);
		}
		scrollbar.Draw(buffer, matrixloc);
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].Discard();
		}
		scrollbar.discard();
	}

	@Override
	public void AdjustPos(Vec2f p) {
		for (int i = 0; i < m_fonts.length; i++) {
			m_fonts[i].AdjustPos(p);
		}
	}

	@Override
	public void Callback() {
		offset=scrollbar.getPositionIndex();
		genFonts();
	}

}
