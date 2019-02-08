package nomad.playerScreens.journal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

public class JournalEntry implements Comparable<JournalEntry> {

	private int ID, priority;
	private String title, text;

	public JournalEntry(String file, String name) {
		Document doc = ParserHelper.LoadXML("assets/data/journal/" + file);
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = root.getElementsByTagName("entry");

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getAttribute("name") == name) {
					ID = Integer.parseInt(e.getAttribute("ID"));
					priority = Integer.parseInt(e.getAttribute("priority"));
					title = e.getAttribute("title");
					text = e.getTextContent();
					break;
				}
			}
		}
	}

	public JournalEntry(DataInputStream dstream) throws IOException {
		ID = dstream.readInt();
		priority = dstream.readInt();
		title = ParserHelper.LoadString(dstream);
		text = ParserHelper.LoadString(dstream);
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(ID);
		dstream.writeInt(priority);
		ParserHelper.SaveString(dstream, title);
		ParserHelper.SaveString(dstream, text);
	}

	public int getID() {
		return ID;
	}

	public int getPriority() {
		return priority;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	@Override
	public int compareTo(JournalEntry comp) {

		return comp.priority - priority;
	}

}
