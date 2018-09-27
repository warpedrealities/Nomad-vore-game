

function combat(controllable,sense,pos,hostile)
	
	a=math.random(0,8)
	b=controllable:getValue(1)
	
	if b==0 then
		controllable:setValue(1,1)
		controllable:setAttack(1);
		controllable:Attack(pos.x,pos.y)	
	else	
		if a<6 then
			controllable:setAttack(0);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			else if pos:getDistance(hostile:getPosition())>3 then
			--if not move towards player
				if controllable:HasPath() then
				controllable:FollowPath()
				else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
				end
			else	
				controllable:setAttack(0);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
			end
		end
	end

end

function speak(controllable,sense,script)
a=script:getValue(2)
	if a>10 then
	script:setValue(2,0)
		a=math.random(0,6)
		if (a==0) then
			sense:drawText("robosphere: scanning area")
		end
		if (a==1) then
			sense:drawText("robosphere: systems online, scanning")
		end
		if (a==2) then
			sense:drawText("robosphere: perimeter secure, patrolling")
		end
		if (a==3) then
			sense:drawText("robosphere: defaulting to patrol behaviour")
		end
		if (a==4) then
			sense:drawText("robosphere: awaiting instructions, patrolling")
		end
		if (a==5) then
			sense:drawText("robosphere: instructions overdue by BUFFER OVERFLOW ERROR")
		end
	end
end

function main(controllable, sense, script)  
	if not controllable:isPeace() then
		pos=controllable:getPosition()
		hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) then
		script:setValue(0,1)
		combat(controllable, sense, pos, hostile)	
		speak(controllable,sense,script)
		else
		script:setValue(0,0)
		a=math.random(0,8)
		controllable:move(a);
		end
	end
end  

function tick(script)
a=script:getValue(0)
if a==0 then
	b=script:getValue(2)
	script:setValue(2,b+1)
end
end