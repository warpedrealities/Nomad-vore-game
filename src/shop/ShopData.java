package shop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Screen;

public interface ShopData {

	int getType();
	String getName();
	void refreshStore();
	void save(DataOutputStream dstream) throws IOException;
	Screen getScreen();
}
