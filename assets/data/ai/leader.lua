
function combat(controllable,sense,script,hostile)
		a=controllable:getValue(1)
		if (a == 0) then
			controllable:setAttack(0)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			a=200
		else
			a=a-1
		end
		controllable:setValue(1,a)
end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else 
		controllable:Wait()
	end
end  