
function main(script,sense)  
	sense:getFlags():setFlag("boarded",1)

	a={"space/talharan/bountyhunter_boarding"}	
	sense:spawnBoarders(a)
	sense:toView()
end  