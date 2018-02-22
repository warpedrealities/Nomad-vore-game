
function shoot(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<4 then
	--too close	
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end
	else
		--if not move towards player
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end
end

function support(controllable,sense,script,hostile)
	bufftarget=sense:getActor(controllable,10,true,sense:getCriteria("hasNotStatus,17,name,saurian warrior"));
	if (bufftarget==nil) then
		bufftarget=sense:getActor(controllable,10,true,sense:getCriteria("hasNotStatus,17,name,saurian 	footlizard"));	
	end
	if not (bufftarget==nil) then
	controllable:setAttack(1)
	controllable:Attack(bufftarget:getPosition().x,bufftarget:getPosition().y)
	end
end

function combat(controllable,sense,script,hostile)
		a=controllable:getValue(0)
		a=a+1
		shoot(controllable,sense,script,hostile)	
		if (a>10) then
		a=0
		support(controllable,sense,script,hostile)
		end
	
	controllable:setValue(0,a)		
end

function patrol(controllable,sense,script)

	var_x=script:getShared():getValue(2)
	var_y=script:getShared():getValue(3)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		controllable:Pathto(var_x,var_y)
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