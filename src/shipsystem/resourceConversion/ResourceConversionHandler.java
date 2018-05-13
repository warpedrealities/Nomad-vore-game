package shipsystem.resourceConversion;

import java.util.HashMap;
import java.util.Map;

public class ResourceConversionHandler {

	private static ResourceConversionHandler instance;
	private Map<String, ResourceConverter> converterMap;
	
	public static ResourceConversionHandler getInstance()
	{
		if (instance==null)
		{
			instance=new ResourceConversionHandler();
		}
		return instance;
	}
	
	private ResourceConversionHandler()
	{
		converterMap=new HashMap<String,ResourceConverter>();		
	}
	
	private ResourceConverter getConverter(String resourceName)
	{
		ResourceConverter converter=new ResourceConverter(resourceName);
		
		converterMap.put(resourceName, converter);
		return converter;
	}
	
	public boolean canConvert(String resourceName, String itemName)
	{
		ResourceConverter converter= converterMap.get(resourceName);
		if (converter==null)
		{
			converter=getConverter(resourceName);
		}
		if (converter==null)
		{	
			return false;
		}
		return converter.canConvert(itemName);
	}
	
	public int conversionValue(String resourceName, String itemName)
	{
		ResourceConverter converter= converterMap.get(resourceName);
		if (converter==null)
		{
			converter=getConverter(resourceName);
		}
		return converter.conversionValue(itemName);
	}
}
