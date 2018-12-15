
function flee (controllable,sense)
	if controllable:getValue(1)==0 then
		--pick a destination location
		x=math.random(1,63)
		y=math.random(1,63)
		if sense:CanWalk(x,y) then
		controllable:setValue(1,x)
		controllable:setValue(2,y)
		controllable:Pathto(controllable:getValue(1),controllable:getValue(2))
		controllable:FollowPath()
		end
	else
		--travel to destination
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(controllable:getValue(1),controllable:getValue(2))
		end
	end

end

function combat(controllable,sense)

	flee(controllable,sense)

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if not controllable:isPeace() and controllable:getRPG():getStat(0)<40 then
		combat(controllable,sense)

	else
		a=math.random(0,32)
		if a<8 then
			controllable:move(a);
		end

	end
	
end  