
function move(controllable,sense,script)
	x=script:getValue(0)
	y=script:getValue(1)
	if (x>0) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,4)
		end		
	end
end


function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	script:setValue(0,hostile:getPosition().x)
	script:setValue(1,hostile:getPosition().y)
	else
	move(controllable,sense,script)
	end
end  