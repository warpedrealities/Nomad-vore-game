
function main(script,sense)  
	sense:getFlags():setFlag("boarding",1)
	currentTime=sense:getTime()/100;
	sense:getFlags():setFlag("CLOCK",currentTime)	
	
	a={"space/talharan/talharan_grey","space/talharan/talharan_grey"}
	sense:spawnBoarders(a)
	sense:toView()
end  