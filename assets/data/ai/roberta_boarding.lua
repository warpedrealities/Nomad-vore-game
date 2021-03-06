
function move(controllable,sense,script)
	x=script:getShared():getValue(0)
	y=script:getShared():getValue(1)
	if (x>0) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,4)
		end		
	else
		a=math.random(0,8)
		if (a<8) then
			controllable:move(a);
		end
	end
end

function combat(controllable,script,hostile,pos)

	if pos:getDistance(hostile:getPosition())<2 then
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
			controllable:FollowPath()		
		end
	else
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end
end

function retreatCheck(controllable,sense,script)
	if (controllable:getRPG():getStat(0)<25) or (controllable:getRPG():getStat(1)<50) then
		sense:drawText("Roberta: Nice try, but I'm out of here, eat ya next time")
		controllable:Remove(false,true)
		return true
	else
		return false
	end 

end

function main(controllable, sense, script) 
	if not (retreatCheck(controllable,sense, script)) then
		pos=controllable:getPosition()
		hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) then
		--combat ai here
		combat(controllable,script,hostile,pos)
		script:getShared():setValue(0,hostile:getPosition().x)
		script:getShared():setValue(1,hostile:getPosition().y)
		script:getShared():setValue(7,10)
		else
		move(controllable,sense,script)
		end
	end
end  