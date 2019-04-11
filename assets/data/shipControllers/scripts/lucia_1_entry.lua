
function main(script,sense)  
	quest=sense:getGlobalFlags():readFlag("BRIGHTFEATHER_QUEST")
	if (quest==14) then
		script:removeShip()
	end
end  