
function aggressive(controllable,script,sense,pos,hostile)
	b=controllable:getValue(0)
	a=script:getValue(0)
	if (a<=1) then

		if (pos:getDistance(hostile:getPosition())<2) then
			b=1
			controllable:setAttack(1);
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			b=0
		end
		controllable:setValue(0,b)
	end
	
	if (b==0) then
		controllable:setAttack(0);
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:specialCommand("flee")
		end
	end
end

function grouping(controllable,sense, script)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		a=script:getValue(0)
		if (a<=1) then
			friend=sense:getNamedActor(controllable,8,false,"Tyrant Lizard")
			if not (friend==nil) then
				if (friend:getPosition():getDistance(controllable:getPosition()>4)) then
				controllable:Pathto(friend:getPosition().x,friend:getPosition().y,8)
				controllable:FollowPath()			
				end
			end
		end
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()	
	hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	aggressive(controllable,script,sense,pos,hostile)

	else
	grouping(controllable,sense,script)
	end
end  

function tick(script)
a=script:getValue(0)
a=a+1
if (a==8) then
	a=0
end
script:setValue(0,a)

end