function ranged(controllable,sense,pos,script,hostile)

	if controllable:HasPath() then
		controllable:FollowPath()
	else
		a=math.random(0,6)
		if (a == 0)
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		else
			controllable:setAttack(2)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		end
	end
	

end

function melee(controllable,sense,pos,script,hostile)
	a=controllable:getValue(0)
	if (a == 0)
		a=1+math.random(0,1)
		controllable:setValue(0,a)
	else
		if (a == 1)
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		else
			controllable:setAttack(0)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		end
	
	end
end

function combat(controllable,sense,pos,script,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	melee(controllable,sense,pos,script,hostile)
	else
	ranged(controllable,sense,pos,script,hostile)	
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
