
function combat(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		if (controllable:getValue(0)==3) then
			controllable:setValue(0,0)		
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			controllable:setAttack(0)
			result = controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			if (result.getValue()==1) then
				controllable:setValue(0,0)	
			else
				controllable:setValue(0,controllable:getValue(0)+1)
			end	
		end
		
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,3)
		end
	end		
end

function victimize(controllable,sense,pos,hostile)
	if not (hostile== nil) then
		if (pos:getDistance(hostile:getPosition())<5) then
			return false;
		end
	end
	
	if (controllable:controllable:getRPG():getStat(0)==controllable:getValue(3) and
	then
		(controllable:controllable:getRPG():getStat(1)==controllable:getValue(4)
		controllable:setValue(1,controllable:getValue(1)+1)	
	else
		controllable:setValue(1,0)
		controllable:setValue(3,controllable:controllable:getRPG():getStat(0))
		controllable:setValue(4,controllable:controllable:getRPG():getStat(1))	
		return false;
	end

	if (controllable:getValue(1)==10) then
		victim=sense:getVictim(controllable,8,true,"station warden",false)		
		if not (victim== nil) then
			if pos:getDistance(victim:getPosition())<2 then
				controllable:startVoreScript("ruffian_warden_OV", victim)
				return true;
			end
		else
			return false;
		end
	end
end

function main(controllable,sense,script)
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	
	if not victimize(controllable,sense,pos,hostile) not (hostile == nil ) and not controllable:isPeace() then
		--combat ai here
		combat(controllable,sense,pos,hostile)

	else
		a=math.random(0,16)
		if (a<8) then
			controllable:move(a);
		end
	end
end
