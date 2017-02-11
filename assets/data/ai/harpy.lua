
function combat(controllable,sense,pos)

	player=sense:getPlayerPosition()

	if pos:getDistance(player)<2 then
	--if in melee range attack
	controllable:AttackPlayer(0)

	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(player.x,player.y,1)
		end
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if sense:CanSeePlayer(pos.x,pos.y) then
	--combat ai here
	combat(controllable,sense,pos)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  