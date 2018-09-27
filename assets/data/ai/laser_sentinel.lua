
function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
	controllable:Wait()
	end
end  