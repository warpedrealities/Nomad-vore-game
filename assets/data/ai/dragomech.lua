function melee(hostile,controllable,sense,pos)
	r=math.random(0,4)
	if (r==0) then
		controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		controllable:setAttack(0);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	end
end

function ranged(hostile,controllable,sense,pos)
	control=controllable:getValue(0)
	if (control>10) then
		r=math.random(0,4)	
		if (hostile:getRPG():hasStatus(170)) then
			controllable:setAttack(3);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		else
			if (r==0) then
				controllable:setAttack(4);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)		
			else
				controllable:setAttack(2);
				controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
			end
		end
	else
		controllable:setAttack(2);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	end
	control=control+1
	controllable:setValue(0,control)
	controllable:setValue(1,1)
	controllable:setValue(2,hostile:getPosition().x)
	controllable:setValue(3,hostile:getPosition().y)	
end

function combat(hostile,controllable,sense,pos)
		if (pos:getDistance(hostile:getPosition())<2) then
			melee(hostile,controllable,sense,pos)
		else
			ranged(hostile,controllable,sense,pos)
		end
end

function noncombat(hostile,controllable,sense,pos)
	controllable:setValue(0,0)
	if controllable:HasPath() then
			controllable:FollowPath()
	else
		if (controllable:getValue(1)==1) then	
			controllable:Pathto(controllable:getValue(2),controllable:getValue(3),8)
			controllable:setValue(1,0)
			controllable:setValue(4,0)			
			taunt_seek(sense)
		else
			a=math.random(0,8)
			controllable:move(a);
		end
	end
end

function taunt_seek(sense)

a=math.random(0,6)
	if (a==0) then
		sense:drawText("dragomech:Target lost, search pattern initiated")
	end
	if (a==1) then
		sense:drawText("dragomech:I will find you")
	end
	if (a==2) then
		sense:drawText("dragomech:Run coward run")
	end
	if (a==3) then
		sense:drawText("dragomech:Target is fleeing, search pattern initiated")
	end
	if (a==4) then
		sense:drawText("dragomech:i will reacquire the target")
	end
	if (a==5) then
		sense:drawText("dragomech:I hunger for victory")
	end
	if (a==6) then
		sense:drawText("dragomech:I hope you find my insides to your liking ma'am")
	end
end

function taunt_initial(sense)

a=math.random(0,8)
	if (a==0) then
		sense:drawText("dragomech:Opposition chance of victory, zero percent")
	end
	if (a==1) then
		sense:drawText("dragomech:And I shoot lasers out of my fucking eyes")
	end
	if (a==2) then
		sense:drawText("dragomech:Every one of us is a one dragon army")
	end
	if (a==3) then
		sense:drawText("dragomech:I am a walking arsenal, rawr.")
	end
	if (a==4) then
		sense:drawText("dragomech:This destruction is beautiful")
	end
	if (a==5) then
		sense:drawText("dragomech:Victory is non-negotiable")
	end
	if (a==6) then
		sense:drawText("dragomech:peace through superior fire power.")
	end
	if (a==7) then
		sense:drawText("dragomech:Combat capabilities optimal.")
	end
	if (a==8) then
		sense:drawText("dragomech:Crush, Kill, Destroy, trample, Annihilate.")
	end	
end


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
		combat(hostile,controllable,sense,pos)
		if (controllable:getValue(4)==0) then
			taunt_initial(sense)
			controllable:setValue(4,1)
		end
	else
		noncombat(hostile,controllable,sense,pos)
	end
end  