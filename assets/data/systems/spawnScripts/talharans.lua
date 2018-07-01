
function main(tools)  
	a=math.random(0,8)
	if (a<2) then	
		if (tools:countFaction("pirate")==0 and tools:countShips("talharan_tradeship") then
			tools:spawn("talharan_tradeship","talharan_tradeship")
		end
	end
end
