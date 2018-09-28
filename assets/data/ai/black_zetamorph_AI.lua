
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)	
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		a=controllable:getValue(1)
		if (a == 0 and not hostile:getRPG():hasStatus(20)) then
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			a=10
		else
			if (a>0) then
			a=a-1	
			end
		--if not move towards player
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)
				controllable:FollowPath()	
			end
		end
		controllable:setValue(1,a)	
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	controllable:Wait()
	end
end  