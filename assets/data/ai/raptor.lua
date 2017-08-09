

function hide(controllable,sense,pos)
	controllable:specialCommand("hide")

end

function aggressive(controllable,sense,pos,hostile)


	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

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

function combat(controllable,sense,pos,script,hostile)
a=script:getValue(0)
b=controllable:getHealth()
c=controllable:getValue(0)
c=c+1
controllable:setValue(0)
a=a+1
script:setValue(0,a)
if (a>3 or b<20 or c>10) then
	aggressive(controllable,sense,pos,hostile)

else

	if controllable:HasPath() then
		controllable:FollowPath()
	else
		hide(controllable,sense,pos)
	end
end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	combat(controllable,sense,pos,script,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  

function tick(script)
a=script:getValue(0)
if (a>0) then
	a=a-1
	script:setValue(0,a)
end
end