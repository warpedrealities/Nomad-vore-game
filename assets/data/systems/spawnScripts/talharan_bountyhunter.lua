
function main(tools)  
	if (tools:getFactions():getFaction("talharan"):getRelationship("player") < 25) then	
		if (tools:countFaction("pirate")==0 and 
		tools:getFactions():getFaction("talharan"):getFactionFlags():readFlag("bountyhunter") == 0 ) then
			tools:spawn("fighter","talharan_bountyhunter")
			tools:getFactions():getFaction("talharan"):getFactionFlags():setFlag("bountyhunter", 1)
		end
	end
end
