
function combat(controllable,sense,pos,hostile)
	attack=controllable:getValue(0)
	if (hostile:getPosition():getDistance(pos)<2) then
		controllable:setAttack(0)	
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		if (attack==0) then
		controllable:setValue(0,1);
		end
	else
		if (attack==1) then
			if controllable:HasPath() then
					controllable:FollowPath()
				else
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
						controllable:FollowPath()	
				end		
		else
			controllable:setAttack(1)	
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)		
		end
	end
	
	if (attack==0) then
			health=controllable:getRPG():getStat(0);
			if health<25 then
				controllable:setValue(0,1);
			end
		end
end


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  
