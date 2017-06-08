
function aggressive(controllable,sense,pos,hostile)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		if pos:getDistance(hostile:getPosition())>2 then
		--if in melee range attack
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	
		else
		--if not move towards player
		if !controllable:HasPath() then
			controllable:specialCommand("flee")
		end
	end
end

function move(controllable,sense,pos)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
	if controllable:getValue(0)!=0 then
		controllable:Pathto(controllable:getValue(0),controllable:getvalue(1),4)
	else
	a=math.random(0,8)
	controllable:move(a);	
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()	
	hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) and not controllable:getPeace() then
	--combat ai here
	aggressive(controllable,sense,pos,hostile)
		controllable:setValue(0,hostile:getPosition().x)
		controllable:setValue(1,hostile:getPosition().y)
	else
	move(controllable,sense,pos)

	end
end  
