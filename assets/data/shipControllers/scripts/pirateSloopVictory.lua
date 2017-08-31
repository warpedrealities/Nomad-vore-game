
function main(script,sense)  
	sense:getFlags():setFlag("boarding",1)
	currentTime=sense:getTime()/100;
	sense:getFlags():setFlag("CLOCK",currentTime)	
	
	a={"space/pirates/kitty_pirate","space/pirates/kitty_pirate","space/pirates/kitty_pirate"}	
	sense:spawnBoarders(a)
	sense:toView()
end  