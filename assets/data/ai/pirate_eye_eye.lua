function combat(controllable,script,hostile,pos)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
		controllable:setAttack(0)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else	
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)
		end
	end
end

function search(controllable,sense,script)
	player=sense:getPlayer(controllable,false)
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(player:getPosition().x,player:getPosition().y,8)
		end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,script,hostile,pos)
	script:getShared():setValue(0,hostile:getPosition().x)
	script:getShared():setValue(1,hostile:getPosition().y)
	script:getShared():setValue(7,10)
	else
		search(controllable,sense,script)
	end
end  