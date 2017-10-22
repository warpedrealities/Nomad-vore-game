function start(controllable)
	controllable:useSelfMove(1)
	controllable:setValue(0,1)
end

function wormBehaviour(controllable,sense)
	pos=controllable:getPosition()
	hostile=sense:getPlayer(controllable,false)
	x=hostile:getPosition().x
	y=hostile:getPosition().y
	distance=hostile:getPosition():getDistance(pos)
	
	if (distance<2)
		controllable:getRPG():removeStatus(23)
		sense:drawText("A deathworm erupts from beneath the sands!")		
		controllable:setValue(0,2)		
	else
		if (distance<12) then
			move(controllable,x,y)			
		end
		messageBehaviour(distance,controllable)
	end
end

function move(controllable,x,y)
	x0=controllable:getValue(1)
	y0=controllable:getValue(2)	

	if not (x==x0) or not (y==y0)
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,4)
		end	
	else
		r=math.random(0,8)
		controllable:move(r);			
	end	
	controllable:setValue(1,x0)
	controllable:setValue(1,y0)	
end

function messageBehaviour(distance,controllable)
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
