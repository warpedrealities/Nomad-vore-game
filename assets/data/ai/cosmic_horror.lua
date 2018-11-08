
function combat(controllable,sense,pos,hostile)
	if (controllable:getValue(1)==0) then
		controllable:setValue(1,controllable:getRPG():getStat(0))	
	end
	if pos:getDistance(hostile:getPosition())<3 then
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

function changeDefence(controllable) 
	if controllable:getRPG():hasStatus(17) then
	controllable:getRPG():removeStatus(17)	
	controllable:useSelfMove(2)
	else
		if controllable:getRPG():hasStatus(18) then
			controllable:getRPG():removeStatus(18)	
			controllable:useSelfMove(3)
		else
			controllable:getRPG():removeStatus(19)
		controllable:useSelfMove(1)			
		end
	end
end

function defence(controllable,sense) 
	timer=controllable:getValue(0)
	timer=timer+1
	if (timer>10) then
		hp=controllable:getRPG():getStat(0);
		comp=controllable:getValue(1)
		if (hp<comp) then
			changeDefence(controllable)
		end
		controllable:setValue(1,hp)
		timer=0;
	end
	controllable:setValue(0,timer)
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,false)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)
	defence(controllable,sense)
	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  