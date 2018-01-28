
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)	
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		d=math.random(0,8)
		if (d==0) then
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		end
	end

end

function movement(controllable,sense,script,pos)
		a=controllable:getValue(0)
	if (a==0) then
		c=math.random(0,8)
		controllable:move(c);	
	else
		b=controllable:getValue(1)		
		if (pos.x==a) and pos.y==b) then
		controllable:setValue(0,0)
		controllable:setValue(1,0)	
		else
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(a,b,8)
			controllable:FollowPath()	
			end		
		end
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	movement(controllable,sense,script,pos)

	end
end  