
function stationKeeping(controllable)
	pos=controllable:getPosition()
	a=controllable:getValue(0)
	b=controllable:getValue(1)
	if (a==0) then
		a=pos.x
	end
	if (b==0) then
		b=pos.y
	end
	
	controllable:setValue(0,a)
	controllable:setValue(1,b)
	
	if ( not a==pos.x and not b==pos.y ) then
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(a,b)
		end
	end
end

function combat(controllable,sense, hostile)
	pos=controllable:getPosition()
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		if pos:getDistance(hostile:getPosition())>2 then
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	
		else
			if not controllable:HasPath() then
				controllable:specialCommand("flee")
			end
		end
	end
end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	
	combat(controllable,sense,hostile)
	else 
		target=sense:getHostile(controllable,12,false)
		if not (target == nil) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(target:getPosition().x,target:getPosition().y)
			end		
		else
			stationKeeping(controllable)
		end
	end
end  