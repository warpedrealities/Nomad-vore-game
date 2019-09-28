
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0);	
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		controllable:setAttack(1);	
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,false)
	if not (hostile == nil ) and not controllable:getPeace() then
		--combat ai here
		combat(controllable,sense,pos,hostile)
	else
		hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) then
			if controllable:HasPath() then
				controllable:FollowPath()
				moveTo(controllable,sense,pos,hostile)
			end
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)
			controllable:FollowPath()	
			moveTo(controllable,sense,pos,hostile)
		end
	end
end  