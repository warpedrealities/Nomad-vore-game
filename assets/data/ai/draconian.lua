
function melee(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0);
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

function ranged(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<4 then
		if pos:getDistance(hostile:getPosition())<2 then
			--if in melee range attack
			controllable:setAttack(0);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			else
			--if in flame range attack
			controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			end
	else
		--if not move towards player
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end
end

function attack(controllable,sense,pos,hostile)
	a=hostile:getRPG():getStatMax(0);
	b=hostile:getRPG():getStat(0);
	
	if b<(a/2) then
		melee(controllable,sense,pos,hostile);
	else
		ranged(controllable,sense,pos,hostile);
	end

end

function combat(controllable,sense,pos,hostile)
	a=controllable:getValue(0)
	b=controllable:getHealth()
	if (a>15 or b<50) then
		attack(controllable,sense,pos,hostile)	
	else
		controllable:setValue(0,a+1)
		if a>15 then
		sense:drawText("you have been noticed")
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
		if controllable:getValue(0)>0 then
			controllable:setValue(0,0);
		end
		a=math.random(0,32)
		if (a<8) then
			controllable:move(a);
		end
	end
end  