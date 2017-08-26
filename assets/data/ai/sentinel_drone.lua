function main(controllable, sense, script, pos)  
	hostile=sense:getHostile(controllable,10,true)
	player=sense:getPlayer(controllable,false)
	if not (hostile == nil ) then
		--combat ai here
		if pos:getDistance(hostile:getPosition())<2 then
			--if in melee range attack
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

		else
			--if not move towards player
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
				controllable:Pathto(player:getPosition().x,player:getPosition().y)
			end

	end
end  