function shield(controllable,sense,pos,shieldTarget)
	if pos:getDistance(shieldTarget:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(1)	
	controllable:Attack(shieldTarget:getPosition().x,shieldTarget:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(shieldTarget:getPosition().x,shieldTarget:getPosition().y,2)
		end
	end
end

function mist(controllable,sense,pos,mistTarget)
	if pos:getDistance(mistTarget:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)	
	controllable:Attack(mistTarget:getPosition().x,mistTarget:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(mistTarget:getPosition().x,mistTarget:getPosition().y,2)
		end
	end
end

function combat(controllable,sense,pos,hostile)
	--find a kitty pirate with less than 50% resolve
	mistTarget=sense:getActor(controllable,10,true,sense:getCriteria("resolveBelow,0.5,name,pirate kitty"));
	if not (mistTarget ==nil) then
		mist(controllable,sense,pos,mistTarget)
	else
		shieldTarget=sense:getActor(controllable,10,true,sense:getCriteria("hasNotStatus,16,name,pirate kitty"));
		if not (shieldTarget==nil) then
			shield(controllable,sense,pos,shieldTarget)
		end
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,false)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	
	end
end  