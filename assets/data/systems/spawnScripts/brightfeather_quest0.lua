
function main(tools)  
	print(tools:getGlobalFlags():readFlag("BRIGHTFEATHER_QUEST"))
	if (tools:getGlobalFlags():readFlag("BRIGHTFEATHER_QUEST")==9) then
		tools:getGlobalFlags():setFlag("BRIGHTFEATHER_QUEST",10)
		tools:spawn("transport0","lucia_0",5,6)
	end  
end
