
function robot(controllable,sense)
	x=controllable:getValue(0)
	y=controllable:getValue(1)
	
	if (controllable:getPosition().x==x) and 
		(controllable:getPosition().y==y) then
		--do nothing
		
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,1)
		end
	end

end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	controllable:setValue(0,hostile:getPosition().x)
	controllable:setValue(1,hostile:getPosition().y)
	else
	robot(controllable,sense)
	end
end  