package worldgentools.preload;

import java.util.ArrayList;

import nomad.Universe;

public class GuaranteePathsFindPosition {

	
	static int fromTheTop(ArrayList<Integer> list)
	{
		for (int i=list.size()-1;i>0;i--)
		{
			if (list.get(i)==1 && Universe.m_random.nextInt(6)==0)
			{
				return i;
			}
		}
		for (int i=list.size()-1;i>0;i--)
		{
			if (list.get(i)==1)
			{
				return i;
			}
		}
		
		return 0;
	}
	
	static int fromTheBottom(ArrayList<Integer> list)
	{
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i)==1 && Universe.m_random.nextInt(6)==0)
			{
				return i;
			}
		}	
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i)==1)
			{
				return i;
			}
		}	
		return 0;
	}	
}
