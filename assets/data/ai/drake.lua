
function aggressive(controllable,sense,pos,hostile)
	
	if hostile:getRPG():hasStatus(90) then
		if pos:getDistance(hostile:getPosition())<2 then
			controllable:startConversation()
		else
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
				controllable:FollowPath()	
			end	
		end
	else
	
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			if pos:getDistance(hostile:getPosition())>2 then
			--if in melee range attack
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		
			else
			
				if not controllable:HasPath() then
				controllable:specialCommand("flee")
				end
			end
		end
	end
end

function move(controllable,sense,pos)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		if not (controllable:getValue(0) == 0) then
			x=controllable:getValue(0)
			y=controllable:getValue(1)
			controllable:Pathto(x,y,4)
		else
			a=math.random(0,8)
			controllable:move(a);	
		end
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()	
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
		aggressive(controllable,sense,pos,hostile)
		controllable:setValue(0,hostile:getPosition().x)
		controllable:setValue(1,hostile:getPosition().y)
	else
	move(controllable,sense,pos)
	end
end  
