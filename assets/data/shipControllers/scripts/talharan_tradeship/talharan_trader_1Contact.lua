
function main(script,sense)  
	factionvalue= sense:getFaction():getRelationship("player")
	if (factionvalue<25) then
		script:startFight()	
	else
		script:startConversation("space/talharan_trader1/contact")		
	end 
end  