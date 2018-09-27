
function combat(controllable,sense,pos)
	if hostile:getRPG():hasStatus(90) then
		if pos:getDistance(hostile:getPosition())<2 then
			controllable:startConversation()
		end	
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("flee")
		end
	end
end


function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,sense,pos)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  
