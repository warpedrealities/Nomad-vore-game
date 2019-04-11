
function main(tools)  
	print(tools:getGlobalFlags():readFlag("BRIGHTFEATHER_QUEST"))
	if (tools:getGlobalFlags():readFlag("BRIGHTFEATHER_QUEST")==12) then
		tools:getGlobalFlags():setFlag("BRIGHTFEATHER_QUEST",13)
		tools:spawn("transport0","lucia_1",5,-6)
		tools:deSpawnFaction("pirate")
	end  
end
