
function combat(controllable,sense,script,hostile)
		script:setValue(3,25)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		
		a=math.random(0,2)
		b=controllable:getValue(0)
		if (a>0 and b<6) then
			b=b+1
			controllable:setValue(0,b)
			controllable:setAttack(1)
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
end

function shouldPatrol(controllable,sense,script)



end

function patrol(controllable,sense,script)
	var_time=script:getValue(0)
	var_time=var_time+1
	var_x=script:getValue(1)
	var_y=script:getValue(2)
	if (var_time>300) then
		x=math.random(0,64)
		y=math.random(0,64)
		if sense:CanWalk(x,y) then
			var_x=x
			var_y=y
		end
		script:setValue(1,var_x)
		script:setValue(2,var_y)		
		var_time=0
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(var_x,var_y)
		end
	
	end
	
	script:setValue(0,var_time)
end

function tick(script)
	a=script:getValue(0)
	a=a+1
	script:setValue(0,a)
	
	a=script:getValue(3)
	if (a>0) then
	a=a-1
	script:setValue(0,a)		
	end

end

function main(controllable, sense, script)  
		hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else
	patrol(controllable,sense,script)
	end
end  