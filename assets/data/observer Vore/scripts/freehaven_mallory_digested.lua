
function main(controllable, view, script)  
	view:getSceneController():getGameUniverse():getPlayer():getFlags():setFlag("adaptive_computation",2)
	view:getSceneController():getGameUniverse():getPlayer():addJournal("main","mallory_digested");
end  