function combat(controllable,sense,pos,script,hostile)

	d=pos:getDistance(hostile:getPosition())
	if d<4 then
	--if in melee range attack
		if (d<2) then
			controllable:setAttack(0)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)		
		end
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
	hostile=sense:getHostile(controllable,10,true,"TEASE",4)
	if not (hostile == nil ) and not controllable:getPeace() then
		--combat ai here
		combat(controllable,sense,pos,script,hostile)

	else
		a=math.random(0,8)
		controllable:move(a);
	end
end  

