
function combat(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		
		a=math.random(0,2)
		b=controllable:getValue(0)
		if (a>0 and b<6) then
			b=b+1
			controllable:setValue(0,b)
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

function pursue(controllable,sense,script)

		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(sense:getViolationLocation().x,sense:getViolationLocation().y)
		end

end

function stationKeeping(controllable)
	pos=controllable:getPosition()
	a=controllable:getValue(0)
	b=controllable:getValue(1)
	if (a==0) then
		a=pos.x
	end
	if (b==0) then
		a=pos.y
	end
	
	controllable:setValue(0,a)
	controllable:setValue(0,b)
	
	if ( not a==pos.x and not b==pos.y ) then
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(a,b)
		end
	end
end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else 
		if sense:getViolationLevel() > 0 then
		pursue(controllable,sense,script)
		else
		stationKeeping(controllable)
		end
	end  
end