
function combat(controllable,sense,pos,hostile)
	distance=pos:getDistance(hostile:getPosition())
	if distance<2 then
	--if in melee range attack
	controllable:setAttack(0)	
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		a=controllable:getValue(1)
		if (a == 0 and not hostile:getRPG():hasStatus(20)) then
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			a=10
		else
			if (a>0) then
			a=a-1	
			end
		--if not move towards player
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				if (distance>4) then
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,4)
				else
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)	
				end
				controllable:FollowPath()	
			end
		end
		controllable:setValue(1,a)	
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if (controllable:getValue(0)==1) then
		hostile=sense:getHostile(controllable,10,false)
	else
		hostile=sense:getHostile(controllable,10,true)
	end

	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)
	controllable:setValue(0,1)
	else
	controllable:Wait()
	end
end  