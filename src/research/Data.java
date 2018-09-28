package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class Data {

	private String name;
	private int count;

	public Data() {

	}

	public Data(String name) {
		this.name = name;
		count = 1;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() {
		count++;
	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, name);
		dstream.writeInt(count);

	}

	public void load(DataInputStream dstream) throws IOException {
		name = ParserHelper.LoadString(dstream);
		count = dstream.readInt();
	}
}
