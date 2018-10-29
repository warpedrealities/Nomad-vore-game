
function main(controllable, sense, script)  
	if sense:getViolationLevel()>0 then
		player=sense:getPlayer(controllable,true)
		if not (player == nil ) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:specialCommand("flee")
			end
		else

		end
	end
end  