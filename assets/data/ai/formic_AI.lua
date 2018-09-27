
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
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

function alert(controllable,script,sense,pos,hostile)
	v=controllable:getValue(0)

	if (v==1) then
		return;
	end
	sense:drawText("the formic lets out an unearthly shriek and charges to attack")
	script:setValue(0,pos.x)
	script:setValue(1,pos.y)
	controllable:setValue(0,1)
end

function pursue(controllable,script,sense)

		if controllable:HasPath() then
		controllable:FollowPath()
		return true;
		else
			if (script:getValue(0)>0) then
				controllable:Pathto(script:getValue(0),script:getValue(1),16)				
				script:setValue(0,0)
				script:setValue(1,0)
				return true;
			else
				return false;
			end
		end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	alert(controllable,script,sense,pos,hostile)
	combat(controllable,sense,pos,hostile)

	else
		if not pursue(controllable,script,sense) then
			a=math.random(0,8)
			controllable:move(a);
		end
	end
end  