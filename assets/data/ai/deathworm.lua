function start(controllable)
	controllable:useSelfMove(1)
	controllable:setValue(0,1)
end

function wormBehaviour(controllable,sense)
	pos=controllable:getPosition()
	hostile=sense:getPlayer(controllable,false)
	x=hostile:getPosition().x
	y=hostile:getPosition().y
		
		move(controllable,x,y,hostile,sense)			
		messageBehaviour(sense,distance,controllable)

end

function move(controllable,x,y,hostile,sense)
	x0=controllable:getValue(1)
	y0=controllable:getValue(2)	
	distance=hostile:getPosition():getDistance(pos)
	if not (x==x0) or not (y==y0) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,4)
		end	
	
		if (distance<2) then
			controllable:getRPG():removeStatus(23)
			sense:drawText("A deathworm erupts from beneath the sands!")		
			controllable:setValue(0,2)		
		end
		if (distance<6) then
			controllable:setValue(4,20)	
		end
	else
		t=controllable:getValue(4)	
		if controllable:HasPath() then
				controllable:FollowPath()	
		else
			t=controllable:getValue(4)
			if (t==0) then
				controllable:specialCommand("flee")
				controllable:setValue(4,-1)	
			else
				r=math.random(0,8)
				controllable:move(r);	
				t=t-1
				controllable:setValue(4,t)			
			end
		end
	end	
	controllable:setValue(1,x)
	controllable:setValue(2,y)	
end

function messageBehaviour(sense,distance,controllable)
	c=controllable:getValue(3)
	c=c+1

	if (c>4) and (distance<12) then
	c=0
		if (distance<=4) then
			sense:drawText("You feel the vibration of something burrowing beneath you")			
		end
		if (distance>4) and (distance<=8) then
			sense:drawText("You hear the sound of shifting sand close by")		
		end	
		if (distance>8)then
			sense:drawText("You see a dune shift nearby")		
		end			
	end
	controllable:setValue(3,c)	
end

function combat(controllable,sense,script)
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil) then 
		if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
		--if not move towards player
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		end
	else
		start(controllable)
	end
end

function main(controllable, sense, script)  
	a=controllable:getValue(0)
	if (a==0) then
		start(controllable)
	end
	if (a==2) then
		combat(controllable,sense,script)
	end
	if (a==1) then
		wormBehaviour(controllable,sense)	
	end
end  
