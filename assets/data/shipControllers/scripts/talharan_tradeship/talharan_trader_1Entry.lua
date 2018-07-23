function checkBoarded(script,sense)
	boarded=sense:getFlags():readFlag("boarded")
	if (boarded>0)then
		script:removeShip()
	end
end
function main(script,sense)  


	pirates = sense:getDetection():getSpawnScriptTools():countFaction("pirate")
	if (pirates > 0) then
		script:removeShip()
	else
		checkBoarded(script,sense)
	end

end  