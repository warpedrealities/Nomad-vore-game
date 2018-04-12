

function hide(controllable,sense,pos)
	controllable:specialCommand("hide")

end

function aggressive(controllable,sense,pos,hostile)

	hostile=sense:getHostile(controllable,12,false)
	if not (hostile == nil ) and not controllable:getPeace() then
		if pos:getDistance(hostile:getPosition())<2 then
		usedScream=controllable:getValue(1)
			if (usedScream==0) then
				--if in melee range attack
				controllable:setValue(1,40)
				sense:drawText("the huntress lets out a battle scream")
				controllable:setAttack(1)			
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			else			
				controllable:setAttack(0)	
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
				usedScream=usedScream-1
				controllable:setValue(1,usedScream)
			end
		else
		--if not move towards player
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
				controllable:FollowPath()	
			end
		end
	end
end

function passive(controllable,sense, pos)

	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			hide(controllable,sense,pos)
		end	
	else
	a=math.random(0,8)
	controllable:move(a);	
	end
end

function control(controllable,sense,pos,script)
	a=script:getShared():getValue(0)
	b=controllable:getHealth()
	c=controllable:getValue(0)
	c=c+1
	controllable:setValue(0)
	a=a+1
	script:setValue(0,a)
	if (a==1 or b<30 or c>10) then
		aggressive(controllable,sense,pos)

	else
		passive(controllable,sense,pos)

	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	control(controllable,sense,pos,script)
end  
