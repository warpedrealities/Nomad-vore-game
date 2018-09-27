function start(controllable)
	controllable:useSelfMove(1)
	controllable:setValue(0,1)
end

function attack(controllable,sense,pos,hostile)
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

function harmCheck(controllable)
	a=controllable:getRPG():getStatMax(0);
	b=controllable:getRPG():getStat(0);
	if (b<a) then
		controllable:getRPG():removeStatus(23)	
	end
	a=controllable:getRPG():getStatMax(1);
	b=controllable:getRPG():getStat(1);
	if (b<a) then
		controllable:getRPG():removeStatus(23)	
	end
end

function combat(controllable,sense,pos,hostile)
		if (controllable:getRPG():hasStatus(23)==false) then
		attack(controllable, sense,pos,hostile)
	
		else 
			if (pos:getDistance(hostile:getPosition())<2) then
			controllable:getRPG():removeStatus(23)
			attack(controllable, sense,pos,hostile)
			else
			harmCheck(controllable)
			end
		end
end	


function main(controllable, sense, script)  
	a=controllable:getValue(0)
	if (a==0) then
		start(controllable)
	end
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	elseif (controllable:getVisible()==false) then
	--stealth occurs here
		if (controllable:getRPG():hasStatus(23)==false) then
			controllable:useSelfMove(1)
		end
	end
end  
