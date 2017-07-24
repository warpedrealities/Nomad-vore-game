function combat(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		
		a=math.random(0,2)
		b=controllable:getValue(3)
		if (a>0 and b<6) then
			b=b+1
			controllable:setValue(3,b)
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
function pursue(controllable,sense,script)
	x=controllable:getValue(0)
	y=controllable:getValue(1)
	controllable:Pathto(x,y,16)
	controllable:setValue(2,1)
end
function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else 
	
		b=controllable:getValue(2)
		if ( b==0 ) then
		pursue(controllable,sense,script)
		else
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				a=math.random(0,8)
				controllable:move(a);			
			end
		end
	end  
end