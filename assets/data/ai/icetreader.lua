
function melee(controllable,sense,pos,hostile)

	a=controllable:getValue(0)
	if (a==1) then
	controllable:setAttack(2);
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	a=2
	controllable:setValue(0,a)	
	else
	controllable:setAttack(0);
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	end

end

function ranged(controllable,sense,pos,hostile)

	a=controllable:getValue(0)
	if (a==0) and pos:getDistance(hostile:getPosition())<4 then
	controllable:setAttack(1);
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	a=1
	controllable:setValue(0,a)	
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end	
	end
	
end

function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
		melee(controllable,sense,pos,hostile)
	else
	--if not move towards player
		ranged(controllable,sense,pos,hostile)
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)
	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  