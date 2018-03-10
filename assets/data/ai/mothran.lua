
function aggressive(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		controllable:setAttack(1)	
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		controllable:setValue(0,10)
		controllable:specialCommand("flee")	
	else
		a=controllable:getValue(0)
		if (a==0) then
			controllable:setAttack(0)	
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)		
		else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end
			a=a-1
			controllable:setvalue(0)
		end
	end

end


function main(controllable, sense, script)  
	pos=controllable:getPosition()	
	hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	aggressive(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  
