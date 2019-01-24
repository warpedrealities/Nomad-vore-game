package shipsystem.weapon;

import java.io.DataInputStream;
import java.io.IOException;

import org.w3c.dom.Element;

public class WeaponEffectLoader {


	public static ShipWeaponEffect readEffect(Element element) {
		if (element.getTagName().equals("DamagingEffect")) {
			return new DamagingEffect(element);
		}
		return null;
	}

	public static ShipWeaponEffect loadEffect(DataInputStream dstream) throws IOException {
		int type = dstream.readInt();
		switch (type) {
		case 0:
			return new DamagingEffect(dstream);
		}
		return null;
	}
}
