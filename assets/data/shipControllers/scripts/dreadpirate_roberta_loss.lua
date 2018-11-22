
function main(script,sense)  
	currentTime=sense:getTime()/100;
	sense:getFlags():setFlag("CLOCK",currentTime)	
	script:startConversation("space/dreadpirate_roberta/loss")

end  