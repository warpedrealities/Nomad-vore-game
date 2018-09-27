
function attack(controllable,sense,pos,hostile)
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

function unstealthed(controllable,sense,pos,hostile)
	--if in melee range and the player doesn't have a status
	if (not (hostile:getRPG():hasStatus(50))) then
		--attack
		if pos:getDistance(hostile:getPosition())<2 then
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end	
		end
		else
	--else flee
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end

		end
end

function stealthed(controllable,sense,pos,hostile)

	--if not in melee range
	if pos:getDistance(hostile:getPosition())<3 then
	--if in melee range unstealth
			controllable:getRPG():removeStatus(23)
		else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end


end

function combat(controllable,sense,pos,hostile)
		if (controllable:getRPG():hasStatus(23)==false) then
			unstealthed(controllable, sense,pos,hostile)
			
		else 
			stealthed(controllable,sense,pos,hostile)
		end
	
end	


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	elseif (controllable:getVisible()==false) then
	--stealth occurs here
		if (controllable:getRPG():hasStatus(23)==false) then
			controllable:useSelfMove(1)
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
