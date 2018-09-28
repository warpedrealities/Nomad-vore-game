function panic (controllable,sense)
	player=sense:getPlayer(controllable,false)

	if pos:getDistance(player:getPosition())<2 then
	--if in melee range attack
		controllable:setAttack(0);
		controllable:Attack(player:getPosition().x,player:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(player:getPosition().x,player:getPosition().y)
		end
	end


end

function flee (controllable,sense)
	if controllable:getValue(1)==0 then
		--pick a destination location
		x=math.random(1,63)
		y=math.random(1,63)
		if sense:CanWalk(x,y) then
		controllable:setValue(1,x)
		controllable:setValue(2,y)
		controllable:Pathto(controllable:getValue(1),controllable:getValue(2))
		controllable:FollowPath()
		end
	else
		--travel to destination
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(controllable:getValue(1),controllable:getValue(2))
		end
	end

end

function combat(controllable,sense)
if controllable:getValue(0)>20 then
	panic(controllable,sense)
else
	player=sense:getPlayer(controllable,true)
	if not (player == nil ) then
		flee(controllable,sense)
		a=controllable:getValue(0)
		a=a+1
		controllable:setValue(0,a)
	else
		controllable:setValue(0,0)
		controllable:setValue(1,0)
		controllable:setValue(2,0)
	end
end

end
function seduce(controllable,sense)
	hostile=sense:getPlayer(controllable,true)
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(1);
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
function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if sense:getViolationLevel()>0 then
		combat(controllable,sense)

	else
		b=controllable:getRPG():getStat(1);
		if b<20 and not controllable:isPeace() then
			seduce(controllable,sense)
		else
			a=math.random(0,16)
			if a<8 then
				controllable:move(a);
			end
		end 

	end
	
end  