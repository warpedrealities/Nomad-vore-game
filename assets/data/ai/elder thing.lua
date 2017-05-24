
function melee(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0);
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

function ranged(controllable,sense,pos,hostile)
	if controllable:HasPath() then
			controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
	end
end
function combat(controllable,sense,pos,hostile)
	a=controllable:getRPG():getStatMax(0);
	b=controllable:getRPG():getStat(0);
	if b<20 then
		controllable:startConversation()
	end
	if b<70 then
		melee(controllable,sense,pos,hostile);
	else
		ranged(controllable,sense,pos,hostile);
	end
end

function path(controllable,sense,pos,hostile)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		if (controllable:getvalue(0)==1) then
			hostile=sense:getHostile(controllable,10,false)
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,12)
		end
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)
	controllable:setValue(0,1)
	else
		path(controllable,sense,pos,hostile)
	end
end  