
function combat(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
			if (math.random(8)==1) then
				controllable:setValue(2,10)
			end
		if (controllable:getValue(0)==3) then
			controllable:setValue(0,0)		
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			controllable:setAttack(0)
			result = controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			if not (result==nil) then
				if (result:getValue()==1) then
					controllable:setValue(0,0)	
				else
					controllable:setValue(0,controllable:getValue(0)+1)
				end
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
	if (controllable:getValue(2)>0) then
		controllable:setValue(2,controllable:getValue(2)-1);
		if (controllable:attemptObserverVore()) then
			controllable:setValue(2,0)
		end
	end
end

function main(controllable,sense,script)
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	victimize(controllable,sense,pos,hostile)
	if (not (hostile == nil ) and not controllable:isPeace()) then
		--combat ai here
		combat(controllable,sense,pos,hostile)
		
	else
		a=math.random(0,16)
		if (a<8) then
			controllable:move(a);
		end
	end
end
