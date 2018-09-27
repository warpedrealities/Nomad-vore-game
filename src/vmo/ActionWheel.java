package vmo;

public class ActionWheel {
	byte[] m_wheel;
	int m_writepos;

	public ActionWheel() {
		m_wheel = new byte[24];
		m_writepos = 0;
	}

	public void Write(byte b) {
		m_wheel[m_writepos] = b;
		m_writepos++;
		if (m_writepos >= 24) {
			m_writepos = 0;
		}

	}

	public byte[] Read() {
		byte b[] = new byte[12];
		int readpos = m_writepos + 1;

		for (int i = 0; i < 12; i++) {
			if (readpos == 24) {
				readpos = 0;
			}
			b[i] = m_wheel[readpos];
			readpos++;
		}

		return b;
	}
}
