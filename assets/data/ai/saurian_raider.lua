
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

function victimize(controllable,victim,script)
	if pos:getDistance(victim:getPosition())<2 then
		rand=math.random()
		if (rand<0.5) then
			controllable:startVoreScript("saurian_raider_elf_raider_CV", victim)
		else
			controllable:startVoreScript("saurian_raider_elf_raider_OV", victim)	
		end
			script:setValue(4,50)	
	else
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(victim:getPosition().x,victim:getPosition().y,8)
			end		
	end

end

function shouldPatrol(controllable,sense,script)
	waitTime=script:getValue(3)
	voreChance=script:getValue(4)	
	if not (waitTime>0) then
		patrol(controllable,sense,script)
	else if (voreChance==0) then
		victim=sense:getVictim(controllable,8,true,"elf warrior",false)
		if not (victim==nil) then
			victimize(controllable,victim,script)	
		end
	end
	end
end

function patrol(controllable,sense,script)
	var_time=script:getValue(0)
	var_time=var_time+1
	var_x=script:getValue(1)
	var_y=script:getValue(2)
	if (var_time>300) or (var_time==0) then
		x=math.random(0,63)
		y=math.random(0,63)
		if sense:CanWalk(x,y) then
			var_x=x
			var_y=y
		end
		script:setValue(1,var_x)
		script:setValue(2,var_y)
		script:getShared():setValue(2,var_x)
		script:getShared():setValue(3,var_y)		
		var_time=0
	else
		if (var_x>0) then
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(var_x,var_y)
			end
		end
	end
	
	script:setValue(0,var_time)
end

function tick(script)
	var_time=script:getValue(0)
	var_time=var_time+1
	script:setValue(0,var_time)
	
	waitTime=script:getValue(3)
	if (waitTime>0) then
	waitTime=waitTime-1
	script:setValue(3,waitTime)		
	end
	
	voreTime=script:getValue(4)
	if (voreTime>0) then
	voreTime=voreTime-1
	script:setValue(4,voreTime)		
	end	
end

function main(controllable, sense, script)  
		hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else
	shouldPatrol(controllable,sense,script)
	end
end  