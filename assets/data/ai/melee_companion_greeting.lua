

function combat(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
		end
	end

end

function follow(controllable,sense,pos)
player=sense:getPlayer(controllable,false)
	if (pos:getDistance(player:getPosition())>3) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(player:getPosition().x,player:getPosition().y)
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
		if (controllable:getFlag("talked")<=0)  then
			controllable:startConversation()
		end
		
		if (controllable:isCompanion()==true) then
			follow(controllable,sense,pos)
		end
	end
end  
