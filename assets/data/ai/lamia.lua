
function coil(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
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

function crush(controllable,sense,pos,hostile)
	if (hostile:getRPG():getBindOrigin()==controllable) then
		controllable:setAttack(1)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		randomwalk(controllable)
	end

end

function aggressive(controllable,sense,pos,hostile)
	if hostile:getRPG():hasStatus(20) then
		crush(controllable,sense,pos,hostile)
	else
		coil(controllable,sense,pos,hostile)
	end
end

function randomwalk(controllable)
	a=math.random(0,8)
	controllable:move(a);

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	aggressive(controllable,sense,pos,hostile)
	else
	randomwalk(controllable)
	end
end 
