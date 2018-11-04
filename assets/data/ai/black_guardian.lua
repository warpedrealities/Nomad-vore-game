
function combat(controllable,sense,pos,hostile)
	controllable:setValue(1,hostile:getPosition().x)
	controllable:setValue(2,hostile:getPosition().y)

	if pos:getDistance(hostile:getPosition())<3 then
	--if in melee range attack
		if (controllable:getRPG():getStat(0)<60) and (controllable:getValue(3)==0) then
		controllable:setValue(3,1)
			controllable:useSelfMove(1)
		else
			controllable:setAttack(0);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		end

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

function chase(controllable,sense,script,pos)
	x=controllable:getValue(1)
	y=controllable:getValue(2)
	if (x==pos.x and y==pos.y) then
		controllable:setValue(1,0)
		controllable:setValue(2,0)
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,8)
		end	
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
	if (controllable:getRPG():getStat(0)>30) then
		x=controllable:getValue(1)
		if (x>0) then
			chase(controllable,sense,script,pos)	
		else
			a=math.random(0,16)
				if (a<8) then
				controllable:move(a);
				end
			end
		end
	end
end  