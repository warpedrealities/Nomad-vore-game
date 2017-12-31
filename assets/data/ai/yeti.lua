
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(1)		
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if hostile:getRPG():hasStatus(9) then	
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		else
		--ranged attack
		controllable:setAttack(0)		
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		end
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  