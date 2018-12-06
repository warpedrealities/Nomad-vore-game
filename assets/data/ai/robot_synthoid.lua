
function robot(controllable,sense,pos)
	a=math.random(0,8)
	controllable:move(a);
end

function speakGreet(controllable,sense)
	a=math.random(0,8)
	if (a==0) then
		sense:drawText("synthoid:hello meat, let me use you")
	end
	if (a==1) then
		sense:drawText("synthoid:meat, do not run, it will do you no good")
	end
	if (a==2) then
		sense:drawText("synthoid:this is the omnico way")
	end
	if (a==3) then
		sense:drawText("synthoid:we will experience you, thoroughly")
	end
	if (a==4) then
		sense:drawText("synthoid:we have chosen you, do not run")
	end
	if (a==5) then
		sense:drawText("synthoid:a new plaything, allow us to demonstrate")
	end
	if (a==6) then
		sense:drawText("synthoid: we will enjoy you meat")
	end
	if (a==7) then
		sense:drawText("synthoid: you look like you will make a fine plaything meat")
	end
end

function speakQuery(controllable,sense)
	a=math.random(0,6)
	if (a==0) then
		sense:drawText("synthoid:WHERE ARE YOU")
	end
	if (a==1) then
		sense:drawText("synthoid:I could of sworn I saw ...meat, fresh meat")
	end
	if (a==2) then
		sense:drawText("synthoid:Here at Omnico we value your holes")
	end
	if (a==3) then
		sense:drawText("synthoid:we seek we seek")
	end
	if (a==4) then
		sense:drawText("synthoid:do not make me run")
	end
	if (a==5) then
		sense:drawText("synthoid:i will find you")
	end
end


function combat(controllable,sense,pos,hostile)

		controllable:setValue(1,hostile:getPosition().x)
		controllable:setValue(2,hostile:getPosition().y)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0);
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		hasjumped=controllable:getValue(4)
		if (hasjumped==0 and pos:getDistance(hostile:getPosition())<4) then
			controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
			controllable:setValue(4,1)	
		else
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
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
	if not controllable:isPeace() then
		pos=controllable:getPosition()
		hostile=sense:getHostile(controllable,10,true)
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
					chase(controllable,sense,script,pos)
				else
					robot(controllable,sense,pos)
				end
			end
	end
end  