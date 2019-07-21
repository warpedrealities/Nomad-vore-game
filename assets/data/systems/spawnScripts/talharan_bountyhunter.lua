
function main(tools)  
	if (tools:getFactions():getFaction("talharan"):getRelationship("player") <= 50) then	
		if (tools:countFaction("pirate")==0 and 
		tools:getFactions():getFaction("talharan"):getFactionFlags():readFlag("bountyhunter") == 0 ) then
			tools:spawn("fighter","talharan_bountyhunter")
		end
	end
end
