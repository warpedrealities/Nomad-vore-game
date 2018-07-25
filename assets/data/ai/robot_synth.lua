
function turn(controllable,sense)
	a=controllable:getValue(0)
	a=a+1;
	if (a>=4) then
		a=0;
	end
	controllable:setValue(0,a)
end

function roamN(controllable,sense,pos)
	if sense:CanWalk(pos.x,pos.y+1)==false then
		turn(controllable,sense)
	else
			controllable:move(0)
	end

end

function roamE(controllable,sense,pos)
	if sense:CanWalk(pos.x+1,pos.y)==false then
		turn(controllable,sense)
	else
			controllable:move(2)
	end

end

function roamS(controllable,sense,pos)
	if sense:CanWalk(pos.x,pos.y-1)==false then
		turn(controllable,sense)
	else
			controllable:move(4)
	end

end

function roamW(controllable,sense,pos)
	if sense:CanWalk(pos.x-1,pos.y)==false then
		turn(controllable,sense)
	else
			controllable:move(6)
	end
end

function robot(controllable,sense,pos)
	a=controllable:getValue(0)
	if (a==0) then
	roamN(controllable,sense,pos)
	end
	if (a==1) then
	roamE(controllable,sense,pos)
	end
	if (a==2) then
	roamS(controllable,sense,pos)
	end
	if (a==3) then
	roamW(controllable,sense,pos)
	end

end

function speakGreet(controllable,sense)
	a=math.random(0,8)
	if (a==0) then
		sense:drawText("synth:hello ma'am, let me help you")
	end
	if (a==1) then
		sense:drawText("synth:ma'am, please remain calm, this is a product demonstration")
	end
	if (a==2) then
		sense:drawText("synth:that's the omnico way")
	end
	if (a==3) then
		sense:drawText("synth:if you like your experience please consider making a purchase")
	end
	if (a==4) then
		sense:drawText("synth:choose OCC synths for your predation needs")
	end
	if (a==5) then
		sense:drawText("synth:a new customer, please hold still for the demonstration")
	end
	if (a==6) then
		sense:drawText("synth:I hope you find my insides to your liking ma'am")
	end
	if (a==7) then
		sense:drawText("synth:you look like you need some time inside me ma'am")
	end
end

function speakQuery(controllable,sense)
	a=math.random(0,6)
	if (a==0) then
		sense:drawText("synth:Where did you go?")
	end
	if (a==1) then
		sense:drawText("synth:I could of sworn I saw a customer")
	end
	if (a==2) then
		sense:drawText("synth:Here at Omnico we value your custom")
	end
	if (a==3) then
		sense:drawText("synth:Is anyone there?")
	end
	if (a==4) then
		sense:drawText("synth:Ma'am please don't hide from me")
	end
	if (a==5) then
		sense:drawText("synth:Ma'am?")
	end
end


function combat(controllable,sense,pos,hostile)

		controllable:setValue(1,hostile:getPosition().x)
		controllable:setValue(2,hostile:getPosition().y)

	if pos:getDistance(hostile:getPosition())<2 then
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

function voice(controllable,sense)
a=controllable:getValue(3)
	if a==0 then
		speakGreet(controllable,sense)
		controllable:setValue(3,10)
	end
end

function chase(controllable,sense,script,pos,x,y)
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

function outbreak(controllable,sense, script)
	outbreak=sense:getGlobalFlags():readFlag("OMNICO_IIA_MALWARE");
	if outbreak==0 then
		return false;
	end
	victim=sense:getVictim(controllable,8,true,"synth",false)
	if (victim==nil) then
		return false;
	end
	
	if pos:getDistance(victim:getPosition())<2 then
		controllable:startVoreScript("synth_assimilation", victim)
		script:setValue(4,50)	
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(victim:getPosition().x,victim:getPosition().y,8)
		end		
	end	

	return true;
end

function main(controllable, sense, script)  
	if not controllable:isPeace() then
		pos=controllable:getPosition()
		hostile=sense:getHostile(controllable,10,true)
		if not (outbreak(controllable,sense,script) then
			if not (hostile == nil ) then
			--combat ai here
			voice(controllable, sense)
			combat(controllable, sense, pos, hostile)	
			else
				x=controllable:getValue(1)
				y=controllable:getValue(2)
				a=controllable:getValue(3)
				if a>0 then
					a=a-1
					controllable:setValue(3,a)
					if a==0 then
						speakQuery(controllable,sense)
					end
				end
			if not x==0 then
					chase(controllable,sense,script,pos,x,y)
				else
					robot(controllable,sense,pos)
				end
			end
		end
	end
end  