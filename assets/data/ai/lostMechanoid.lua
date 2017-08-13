
function robot(controllable,sense)
	local a=controllable:getValue(0)
	if not controllable:move(a) then
	local a=math.random(0,8)
	controllable:setValue(0,a)
	end
end
function combat(controllable,sense,script,hostile)
	local pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(1)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
	local a=controllable:getValue(1)
	local a=a+1
	if (a>10) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end	
		a=0
	else
		controllable:setAttack(0)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end 
	controllable:setValue(1,a)	
	end
	
end

function speak(controllable,sense)
	local a=math.random(0,6)
	if (a==0) then
		sense:drawText("mechanoid: combat mechanoid online")
	end
	if (a==1) then
		sense:drawText("mechanoid: flux capacitor output nominal")
	end
	if (a==2) then
		sense:drawText("mechanoid: command downlink failed")
	end
	if (a==3) then
		sense:drawText("mechanoid: lacking mission parameters")
	end
	if (a==4) then
		sense:drawText("mechanoid: orders not received, standby")
	end
	if (a==5) then
		sense:drawText("mechanoid: combat mechanoid all systems ready")
	end
end

function voice(controllable,sense)
	local a=controllable:getValue(2)
	local a=a+1
	if (a > 10 ) then
		speak(controllable,sense)
		a=0
	end
	controllable:setValue(2,a)	
end

function main(controllable, sense, script)  
	local hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,script,hostile)
	else
		robot(controllable,sense)
		if controllable:isActorVisibility() then
			voice(controllable,sense)
		end
	end
end  