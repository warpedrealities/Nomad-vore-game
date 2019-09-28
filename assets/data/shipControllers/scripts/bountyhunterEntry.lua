
function checkBoarded(script,sense)
	boarded=sense:getFlags():readFlag("boarded")
	if (boarded>0)then
		script:removeShip()
		tools:getFactions():getFaction("talharan"):getFactionFlags():setFlag("bountyhunter", 0)
	end
end

function checkTime(script,sense)
	currentTime=sense:getTime()/100;
	arrivalTime=sense:getFlags():readFlag("ARRIVAL")
	if (arrivalTime==0) then
	sense:getFlags():setFlag("ARRIVAL",currentTime)	
	else 
		if (currentTime>arrivalTime+50) then
			if not (sense:getDetection():getHostile(script:getPosition().x,script:getPosition().y,8) == nil) then
			script:removeShip()
			tools:getFactions():getFaction("talharan"):getFactionFlags():setFlag("bountyhunter", 0)
			end
		end
	end

end

function main(script,sense)  

	checkBoarded(script,sense)
	checkTime(script,sense)
	if (tools:getFactions():getFaction("talharan"):getFactionFlags():readFlag("bountyhunter") == 2) then
		script:removeShip()
		tools:getFactions():getFaction("talharan"):getFactionFlags():setFlag("bountyhunter", 0)
	end
end  