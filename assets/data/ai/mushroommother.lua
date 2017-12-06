

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil )  and not controllable:isPeace() then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	end

end  