
function checkBoarded(script,sense)
	boarded=sense:getFlags():readFlag("boarded")
	if (boarded>0)then
		script:removeShip()
	end
end

function main(script,sense)  

	checkBoarded(script,sense)

	if (tools:getFactions():getFaction("talharan"):getFactionFlags():readFlag("bountyhunter") == 1) then
		script:removeShip()
	end
end  