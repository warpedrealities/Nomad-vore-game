
function combat(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		
		a=math.random(0,2)
		if (a>0) then
			controllable:setAttack(1)
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
end

function stationKeeping(controllable)
	if (controllable:getFlag("office")==1)  then
		x=8
		y=23
		pos=controllable:getPosition()
		if not (pos.x==x) or not (pos.y==y) then	
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(x,y,16)
			end
		end
	end
end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else 
		if (controllable:getFlag("talked")==0) then
			controllable:startConversation()
		end
		stationKeeping(controllable)

	end  
end