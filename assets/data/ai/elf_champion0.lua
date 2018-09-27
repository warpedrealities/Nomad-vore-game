
function combat(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else		
		a=math.random(0,4)
		if (a==0) and hostile:getRPG():hasStatus(20) then
			controllable:setAttack(1)
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
end

function patrol(controllable,sense,script)

	var_x=script:getShared():getValue(0)
	var_y=script:getShared():getValue(1)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		controllable:Pathto(var_x,var_y)
	end

end

function main(controllable, sense, script)  
		hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else
	patrol(controllable,sense,script)
	end
end  