
function main(script,sense)  
	fight=sense:getFlags():readFlags("fight")
	if (fight==0) then
		script:startConversation("space/talharan_trader0/contact")
	else
		script:startFight()
	end	
end  