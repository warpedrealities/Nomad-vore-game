
function robot(controllable,sense)
	a=math.random(0,8)
	controllable:move(a);
end

function melee(controllable,sense,hostile)
	if hostile:getRPG():hasStatus(17) then
		controllable:specialCommand("hide")
		if controllable:HasPath() then
			controllable:FollowPath()
		end
	else
		if (hostile:getRPG():getTagged("radio")) then
			controllable:setAttack(2)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		end	
	end

end

function combat(controllable,sense,hostile,pos)
	if (controllable:getFlag("overdrive")==1) and (controllable:getValue(0)==0)then
		controllable:useSelfMove(3)
		controllable:setValue(0,1)	
	end
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
		 melee(controllable,sense,hostile)
	else
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) and not controllable:isPeace() then
		--combat ai here
		combat(controllable,sense,hostile,pos)
		else
		robot(controllable,sense)
		end
	end
end  