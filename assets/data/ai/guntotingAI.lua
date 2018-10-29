
function move(controllable,sense,script)
	a=math.random(0,8)
	controllable:move(a);
end


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
		if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		controllable:setAttack(1)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
		controllable:setAttack(0)	
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		end
	else
	move(controllable,sense,script)
	end
end  