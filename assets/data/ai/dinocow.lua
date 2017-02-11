

function attack(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end

end

function combat(controllable,sense,pos,hostile)
	a=controllable:getValue(0)
	b=controllable:getHealth()
	if (a>15 or b<40) then
		attack(controllable,sense,pos,hostile)
		
	else
		if (hostile:getPosition():getDistance(pos)<5) then
			controllable:setValue(0,a+1)
			if (a==10) then
			sense:drawText("the arkosaur bellows in agitation")
			end
		else 
			if (a>0) then
			controllable:setValue(0,a-1)
			end
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
		a=math.random(0,16)
		if (a<8) then
			controllable:move(a);
		end
	end
end  