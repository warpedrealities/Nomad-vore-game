function combat(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<5 then
		if pos:getDistance(hostile:getPosition())<2 then
			--if in melee range, do d100 roll
			a=math.random(0,100)
			if (a>85) and not(hostile:getRPG():hasStatus(9))then
				--if d100 roll over 85, and Nomad isn't already stunned, stun attack
				controllable:setAttack(1);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			else
				--if not, regular melee attack
				controllable:setAttack(0);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			end
		else
			b=controllable:getValue(3)
			if (b==0)then
				controllable:setAttack(3);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
				controllable:setValue(3,10)
				controllable:setAttack(0);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			else
				controllable:setValue (3,b-1)
				if controllable:HasPath() then
					controllable:FollowPath()
				else
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
				end
			end
		end
		
	else
		a=controllable:getValue(1)
		if (a == 0) then
			controllable:setAttack(2)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			a=5
			controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
			a=a-1
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		end
		controllable:setValue(1,a)
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
		if controllable:getValue(0)>0 then
			controllable:setValue(0,0);
		end
		a=math.random(0,32)
		if (a<8) then
			controllable:move(a);
		end
	end
end  