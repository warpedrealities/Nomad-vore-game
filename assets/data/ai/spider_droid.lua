
function main(controllable, sense, script, pos)  
	hostile=sense:getHostile(controllable,10,true)
	end
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end
	if (pos:getDistance(player:getPosition())>3) then
		player=sense:getPlayer(controllable,false)	
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(player:getPosition().x,player:getPosition().y)
		end
	else
		controllable:Wait()
	end
end  