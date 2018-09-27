
function pursue(script,sense,hostile)
	if (hostile:getPosition():getDistance(script:getPosition())<2) then
		script:startFight()
	else
		print("attempting to move")
		direction=sense:getDetection():getDirection(script:getPosition(),hostile:getPosition())
		print(direction)
		script:move(direction)
	end
end

function main(script,sense)  
	hostile=sense:getDetection():getHostile(script:getPosition().x,script:getPosition().y,16)
	if not (hostile == nil ) then
		pursue(script,sense,hostile)
	end
end  