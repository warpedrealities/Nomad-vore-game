
function combat(controllable,sense,pos,hostile)
	a=controllable:getValue(0)
	if (hostile:getPosition():getDistance(pos)<2) and a==0 then
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		controllable:setValue(0,5);
	else
		controllable:setValue(0,a-1);
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end
	end
end


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  
