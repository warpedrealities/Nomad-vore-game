
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


function pursuit(controllable,sense,script)
	actor=sense:getPlayer(controllable,true)
	
	if not (actor == nil) then
		pos=actor:getPosition()
		me=controllable:getPosition()
		if (pos:getDistance(me)>1.5 and pos:getDistance(me)<8) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(pos.x,pos.y)
			end	
		else 
			if (pos:getDistance(me)<1.5) then
				controllable:startConversation()
			end
		end
	else
	stationKeeping(controllable)
	end

end


function main(controllable, sense, script)  
	if (controllable:getRPG():hasStatus(23)==false) then
		controllable:useSelfMove(0)
	else
		pursuit(controllable,sense,script)
	end
end  
