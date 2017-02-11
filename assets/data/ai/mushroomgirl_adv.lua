
function doattack(controllable,sense)
		
end

function combat(controllable,sense,pos,hostile)

	if math.random(0,1)==0 then
	--use pheremone attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end

end

function main(controllable, sense, script)    
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
	a=math.random(0,8)
	controllable:move(a);
	end
end  