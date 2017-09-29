
function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
		if (hostile:getStat(0)>10) then
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		end
	else
	end
end  