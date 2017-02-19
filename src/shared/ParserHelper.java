package shared;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nomad.StarSystem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParserHelper {

	static DocumentBuilder m_builder;
	
	public static Document LoadXML(String filename)
	{

			try {
				File fXmlFile = new File(filename);
				if (m_builder==null)
				{
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					m_builder = dbFactory.newDocumentBuilder();	
				}

				Document doc = m_builder.parse(fXmlFile);
						
			
			
				return doc;

				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		return null;
	}
	
	public static void SaveString(DataOutputStream stream, String string) throws IOException
	{
		int length=string.length();
		stream.writeInt(length);
		stream.writeChars(string);
	}
	
	public static String LoadString(DataInputStream stream) throws IOException
	{
		int length=stream.readInt();
		char buffer[]=new char[length];
		for (int i=0;i<length;i++)
		{
			buffer[i]=stream.readChar();
		}
		String string=new String(buffer);
		return string;
	}
	
	public static String readString(String file) throws IOException
	{
		
		StringBuilder builder=new StringBuilder();
		BufferedReader reader=new BufferedReader(new FileReader(new File(file)));
		while (reader.ready())
		{
			builder.append(reader.readLine());
		}
		
		return builder.toString();
	}
	
}
