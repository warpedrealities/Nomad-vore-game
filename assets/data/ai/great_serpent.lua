

function combat(controllable,sense)
-- fight player--
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		
		if pos:getDistance(hostile:getPosition())<8 then
		--if in melee range attack
		controllable:setAttack(0)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		script:setValue(2,hostile:getPosition().x)
		script:setValue(3,hostile:getPosition().y)
		else
			--if not move towards player
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		end
	else
		x=script:getValue(0)
		y=script:getValue(1)
		if (x>0) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
			controllable:Pathto(x,y,4)
			end		
		end
	end
end

function flee(controllable, sense)
-- if see player, flee player, if can't see player, regenerate
	hostile=sense:getHostile(controllable,10,true,"PSYCHIC",4)
	if not (hostile == nil ) and not controllable:getPeace() then
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:specialCommand("flee")
			end
	else
		--use health booster
			pos=controllable:getPosition()
		controllable:setAttack(1)
		controllable:Attack(pos.x,pos.y)

		a=controllable:getValue(0)
		a=a+1
		controllable:setValue(0,a)
	end
end

function regenerate(controllable,sense)
-- if can see player, fight player, else 
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
			
		if pos:getDistance(hostile:getPosition())<8 then
		--if in melee range attack
		controllable:setAttack(0)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

		else
			--if not move towards player
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
			end
		end	
	else
		a=script:getValue(0)
		a=a+1
		if (a==20) then
			a=0
		end
		controllable:setValue(0,a)

	
	end
end

function main(controllable, sense, script)  
	
	
	a=controllable:getValue(0)
	b=controllable:getHealth()
	--if ( b > 30 ) then
		combat(controllable,sense)
	
	--end
	
	--if ( b < 30 ) then
	--	if ( a==0 ) then
	--		flee(controllable,sense)
	--	else
	--		regenerate(controllable, sense)
	--	end
	--end 
	
end  