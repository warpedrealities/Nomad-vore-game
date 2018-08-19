
function combat(controllable,sense,pos,hostile)

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

function speakGreet(controllable,sense)
	a=math.random(0,8)
	if (a==0) then
		sense:drawText("guardian:trespasser")
	end
	if (a==1) then
		sense:drawText("guardian:intruder")
	end
	if (a==2) then
		sense:drawText("guardian:you are fated to die, i will see to it")
	end
	if (a==3) then
		sense:drawText("guardian:prepare to meet your end fool")
	end
	if (a==4) then
		sense:drawText("guardian:trespasser, your time is at hand")
	end
	if (a==5) then
		sense:drawText("guardian:intruder, your time is upon you")
	end
	if (a==6) then
		sense:drawText("guardian:I will be your end")
	end
	if (a==7) then
		sense:drawText("guardian:trespasser, you will fall at my hand")
	end
end


function voice(controllable,sense)
	voiceDelay=controllable:getValue(3)
	if voiceDelay==0 then
		speakGreet(controllable,sense)
		controllable:setValue(3,10)
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)
	voice(controllable, sense)
	else
	if (controllable:getRPG():getStat(0)>40) then
		a=math.random(0,16)
			if (a<8) then
			controllable:move(a);
			end
		end
	end
end  