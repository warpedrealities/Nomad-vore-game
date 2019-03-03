
function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	pos=controllable:getPosition()	
	
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		player=sense:getPlayer(controllable,false)	
		if (pos:getDistance(player:getPosition())>3) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(player:getPosition().x,player:getPosition().y)
			end
		else
			controllable:Wait()
		end
	end
end  