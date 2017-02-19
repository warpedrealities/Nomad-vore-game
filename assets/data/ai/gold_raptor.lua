function combat(controllable,sense,pos,script,hostile)

	if pos:getDistance(hostile:getPosition())<4 then
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
	pos=controllable:getPosition()	
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		--combat ai here
		combat(controllable,sense,pos,script,hostile)

	else
		a=math.random(0,8)
		controllable:move(a);
	end
end  

