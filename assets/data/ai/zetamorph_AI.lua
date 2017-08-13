
function moveTo(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end
end

function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
			controllable:FollowPath()
			moveTo(controllable,sense,pos,hostile)
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)
			controllable:FollowPath()	
			moveTo(controllable,sense,pos,hostile)
		end
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,false)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  