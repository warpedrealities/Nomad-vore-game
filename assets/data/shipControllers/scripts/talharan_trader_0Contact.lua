
function main(script,sense)  
	fight=sense:getFlags():readFlag("fight")
	print("script start "..fight)
	if fight==0 then
		print("startconversation")
		script:startConversation("space/talharan_trader0/contact")
		
	else
		print("startfight")
		script:startFight()
	end	
end  