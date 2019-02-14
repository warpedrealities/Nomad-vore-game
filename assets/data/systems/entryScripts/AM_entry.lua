
function main(tools)  
	if (tools:getGlobalFlags():readFlag("MAIN")<1) then
		tools:addJournal("main","quest1")
		tools:getGlobalFlags():setFlag("MAIN",1)
	end
	ship=tools:getShip();
	if not (ship==nil) and (tools:getGlobalFlags():readFlag("MAIN")<2) then
		if (ship:getShipStats():getFTL()>0) then
			tools:addJournal("main","quest2")
			tools:getGlobalFlags():setFlag("MAIN",2)
		end
	end
end
