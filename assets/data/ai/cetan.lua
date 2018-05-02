

function attack(controllable,sense,pos,hostile)
	attack=controllable:getValue(1)
	if (attack<=0) and (pos:getDistance(hostile:getPosition())>=2) then
		if (pos:getDistance(hostile:getPosition())>4) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
				controllable:FollowPath()	
			end	
		else
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			attack=6	
		end

	else
		attack=attack-1
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end	
	end
		controllable:setValue(1,attack)	

end

function combat(controllable,sense,pos,hostile)
	a=controllable:getValue(0)
	b=controllable:getHealth()
	if (a>s or b<40) then
		attack(controllable,sense,pos,hostile)
		
	else		
		controllable:setValue(0,a+1)
		if (a==14) then
		sense:drawText("Cetan lets out a roar")
		end
	end
	
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
		b=controllable:getValue(0);
		if (b>0 and b<15) then
			controllable:setValue(0,b-1);
			controllable:setValue(1,0)		
		end
		a=math.random(0,16)
		if (a<8) then
			controllable:move(a);
		end
	end
end  