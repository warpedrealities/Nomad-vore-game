package rendering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class ColourMap {

	Vector<Colour> m_array;
	private static ColourMap m_instance = null;

	protected ColourMap() {
		m_array = new Vector<Colour>();

		Scanner reader = null;
		try {
			reader = new Scanner(new FileReader("assets/data/colours.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (reader.hasNext()) {

			int r = reader.nextInt();
			int g = reader.nextInt();
			int b = reader.nextInt();
			Colour colour = new Colour((byte) r, (byte) g, (byte) b);
			m_array.add(colour);
		}
		reader.close();
	}

	public byte[] getColour(int i) {
		return m_array.get(i).m_channels;
	}

	public static ColourMap getInstance() {
		if (m_instance == null) {
			m_instance = new ColourMap();
		}
		return m_instance;
	}
}
