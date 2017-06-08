package artificial_intelligence;

import actor.Actor;
import actor.npc.NPC;
import nomad.Universe;

public class Code_AI implements Controller {

	
	
	
	@Override
	public void RunAI(NPC controllable, Sense senses) {
		
		if (controllable.getPeace()==false)
		{
			//can see player?
			Actor hostile=senses.getHostile(controllable, 11, true);
			if (hostile!=null)
			{
				//if so check if within range of player	
				if (controllable.getPosition().Distance(hostile.getPosition())<2)
				{
					//if so attack	
					controllable.Attack((int)hostile.getPosition().x, (int)hostile.getPosition().y);
				}
				else
				{
					//if not move towards player
					if (controllable.HasPath()==true)
					{
						controllable.FollowPath();
					}
					else
					{
						controllable.Pathto((int)hostile.getPosition().x, (int)hostile.getPosition().y,4);
			
					}		
				}
			}
			else
			{
				if (controllable.HasPath()==true)
				{
					controllable.FollowPath();
				}
				else
				{
				//if not wander			
				int v=Universe.m_random.nextInt(9);
			if (v<8)
				{
					controllable.move(v);
				}
				else
				{
					controllable.Wait();
				}
				}
			}		
		}
		else
		{
			int v=Universe.m_random.nextInt(9);
			if (v<8)
				{
					controllable.move(v);
				}
				else
				{
					controllable.Wait();
				}
						
		}

		

		

		
		
	}

	
	
	
}
